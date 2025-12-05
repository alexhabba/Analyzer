package alex.habba.analyzer.utils;

import alex.habba.analyzer.dto.BybitTickerResponse;
import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class FundingRateChecker {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static BybitTickerResponse.TickerData getTickerDataBySymbol(String symbol) throws IOException, InterruptedException {
        var client = BybitApiClientFactory.newInstance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", BybitApiConfig.MAINNET_DOMAIN).newMarketDataRestClient();
        Object obj = client.getMarketTickers(MarketDataRequest.builder().category(CategoryType.LINEAR).symbol(symbol).build());
        BybitTickerResponse bybitTickerResponse = objectMapper.readValue(objectMapper.writeValueAsString(obj), BybitTickerResponse.class);
        return bybitTickerResponse.getResult().getList().get(0);
    }
}
