package alex.habba.analyzer.utils;

import alex.habba.analyzer.dto.BybitInstrumentsResponse;
import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolUtils {

    public static void main(String[] args) {
        getLinearWhereNotSpotSymbols().forEach(System.out::println);
    }

    /**
     * Получение списка токенов(фьючерсов), у которых нет спота
     */
    @SneakyThrows
    public static List<String> getLinearWhereNotSpotSymbols() {
        ObjectMapper objectMapper = new ObjectMapper();
        var client = BybitApiClientFactory.newInstance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", BybitApiConfig.MAINNET_DOMAIN).newMarketDataRestClient();
        Object instruments = client.getInstrumentsInfo(MarketDataRequest.builder().category(CategoryType.LINEAR).limit(1000).build());
        BybitInstrumentsResponse instrumentInfo = objectMapper.readValue(objectMapper.writeValueAsString(instruments), BybitInstrumentsResponse.class);
        var symbols = instrumentInfo.getResult().getList().stream()
                .filter(i -> i.getSymbol().endsWith("USDT"))
                .filter(i -> i.getLaunchTime().isAfter(LocalDateTime.now().minusMonths(3)))
                .sorted(Comparator.comparing(BybitInstrumentsResponse.InstrumentInfo::getLaunchTime))
                .peek(i -> System.out.println("symbol: " + i.getSymbol() + "    time: " + i.getLaunchTime() + "     " + "https://www.bybit.com/trade/usdt/" + i.getSymbol()))
                .map(BybitInstrumentsResponse.InstrumentInfo::getSymbol)
                .collect(Collectors.toSet());

        var client1 = BybitApiClientFactory.newInstance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", BybitApiConfig.MAINNET_DOMAIN).newMarketDataRestClient();
        Object instruments1 = client1.getInstrumentsInfo(MarketDataRequest.builder().category(CategoryType.SPOT).limit(1000).build());
        BybitInstrumentsResponse instrumentInfo1 = objectMapper.readValue(objectMapper.writeValueAsString(instruments1), BybitInstrumentsResponse.class);
        var symbols1 = instrumentInfo1.getResult().getList().stream()
                .map(BybitInstrumentsResponse.InstrumentInfo::getSymbol)
                .filter(symbol -> symbol.endsWith("USDT"))
                .collect(Collectors.toSet());

        return symbols.stream()
                .filter(sy -> !symbols1.contains(sy))
                .collect(Collectors.toList());
    }


    @SneakyThrows
    public static List<String> getLinearSymbols() {
        ObjectMapper objectMapper = new ObjectMapper();
        var client = BybitApiClientFactory.newInstance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", BybitApiConfig.MAINNET_DOMAIN).newMarketDataRestClient();
        Object instruments = client.getInstrumentsInfo(MarketDataRequest.builder().category(CategoryType.LINEAR).limit(1000).build());
        BybitInstrumentsResponse instrumentInfo = objectMapper.readValue(objectMapper.writeValueAsString(instruments), BybitInstrumentsResponse.class);

        return instrumentInfo.getResult().getList().stream()
                .filter(i -> i.getSymbol().endsWith("USDT"))
//                .filter(i -> i.getLaunchTime().isAfter(LocalDateTime.now().minusMonths(3)))
                .sorted(Comparator.comparing(BybitInstrumentsResponse.InstrumentInfo::getLaunchTime))
                .peek(i -> System.out.println("symbol: " + i.getSymbol() + "    time: " + i.getLaunchTime() + "     " + "https://www.bybit.com/trade/usdt/" + i.getSymbol()))
                .map(BybitInstrumentsResponse.InstrumentInfo::getSymbol)
                .collect(Collectors.toList());
    }
}
