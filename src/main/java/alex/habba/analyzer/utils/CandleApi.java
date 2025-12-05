package alex.habba.analyzer.utils;

import alex.habba.analyzer.dto.CandleDto;
import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.impl.BybitApiMarketRestClientImpl;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static alex.habba.analyzer.utils.DateTimeUtils.getDateTime;
import static alex.habba.analyzer.utils.Utils.getResponse;

@Slf4j
public class CandleApi {

    private final static BybitApiClientFactory client = BybitApiClientFactory.newInstance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", BybitApiConfig.MAINNET_DOMAIN);

    public static List<CandleDto> getCandleDtoWithoutTicks(String symbol, LocalDateTime startDateTime, MarketInterval interval) {
        int limit = 3;
        long startTime = startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();


        try {
            MarketDataRequest request = MarketDataRequest.builder()
                    .category(CategoryType.LINEAR)
                    .symbol(symbol)
                    .marketInterval(interval)
                    .start(startTime)
                    .limit(limit)
                    .build();

            Object response = client.newMarketDataRestClient().getMarketLinesData(request);
            return parseKlineResponse(response, symbol);

        } catch (Exception e) {
            System.out.println("Error getting data from Bybit: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    private static List<CandleDto> parseKlineResponse(Object response, String symbol) {
        List<CandleDto> candles = new ArrayList<>();

        try {
            // Ответ уже является Map/объектом, не конвертируем в строку
            if (response instanceof Map) {
                Map<?, ?> responseMap = (Map<?, ?>) response;

                // Проверяем код ответа
                if (!"0".equals(String.valueOf(responseMap.get("retCode")))) {
                    log.error("Bybit API error: {}", responseMap.get("retMsg"));
                    return candles;
                }

                // Получаем результат
                Map<?, ?> result = (Map<?, ?>) responseMap.get("result");
                if (result != null) {
                    List<?> list = (List<?>) result.get("list");

                    // Парсим каждую свечу
                    for (Object item : list) {
                        if (item instanceof List) {
                            List<?> candleData = (List<?>) item;

                            // Формат данных Bybit: [startTime, openPrice, highPrice, lowPrice, closePrice, volume, turnover]
                            CandleDto candle = CandleDto.builder()
                                    .createDate(DateTimeUtils.getDateTime(Long.parseLong(candleData.get(0).toString())))
                                    .open(Double.parseDouble(candleData.get(1).toString()))
                                    .high(Double.parseDouble(candleData.get(2).toString()))
                                    .low(Double.parseDouble(candleData.get(3).toString()))
                                    .close(Double.parseDouble(candleData.get(4).toString()))
                                    .vol(Double.parseDouble(candleData.get(5).toString()))
                                    .interval(1)
                                    .symbol(symbol)
                                    .build();

                            candles.add(candle);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error parsing Bybit response: {}", e.getMessage());
            e.printStackTrace();
        }

        return candles;
    }
}