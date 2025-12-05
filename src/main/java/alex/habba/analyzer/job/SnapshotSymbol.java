package alex.habba.analyzer.job;

import alex.habba.analyzer.dto.BybitTickerResponse;
import alex.habba.analyzer.dto.CandleDto;
import alex.habba.analyzer.entity.SymbolInfo;
import alex.habba.analyzer.service.SymbolInfoService;
import com.bybit.api.client.domain.market.MarketInterval;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static alex.habba.analyzer.utils.CandleApi.getCandleDtoWithoutTicks;
import static alex.habba.analyzer.utils.FundingRateChecker.getTickerDataBySymbol;
import static alex.habba.analyzer.utils.SymbolUtils.getLinearSymbols;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotSymbol {

    private final SymbolInfoService service;

//        @Scheduled(cron = "01 00 * * * *")
    @Scheduled(fixedDelay = 1000000000)
    public void execute() {
//        List<String> symbols = getLinearSymbols();
        List<String> symbols = List.of("SAPIENUSDT");

        List<SymbolInfo> symbolInfoList = new ArrayList<>();

        symbols.forEach(symb -> {
//            TickerInfoDto tickerInfo = null;
//            try {
//                tickerInfo = fetchByTicker(symb.replace("USDT", ""));
//            } catch (Exception e) {
//                System.out.println("Error getting ticker info " + symb);
//            }

            // todo
            List<CandleDto> candles = getCandleDtoWithoutTicks(symb, LocalDateTime.now(ZoneOffset.UTC).minusHours(3), MarketInterval.HOURLY).stream()
                    .sorted(Comparator.comparing(CandleDto::getCreateDate))
                    .toList();

            BybitTickerResponse.TickerData tickerData = null;
            try {
//                https://www.bybit.com/trade/usdt/SAPIENUSDT
//                tickerData = getTickerDataBySymbol("SAPIENUSDT");
                tickerData = getTickerDataBySymbol(symb);
            } catch (Exception e) {
                System.out.println("Error getting ticker data");
            }

            if (allNotNull(tickerData, candles) && !candles.isEmpty()) {

                BigDecimal fundingRate = tickerData.getFundingRate();
                BigDecimal openInterest = tickerData.getOpenInterest();
                BigDecimal openInterestValue = tickerData.getOpenInterestValue();

                CandleDto candle = candles.get(1);
                SymbolInfo symbolInfo = SymbolInfo.builder()
                        .symbol(symb)
                        .vol(candle.getVol())
                        .createDate(candle.getCreateDate())
                        .open(candle.getOpen())
                        .close(candle.getClose())
                        .high(candle.getHigh())
                        .low(candle.getLow())
//                        .circulatingSupply(tickerInfo.getCirculatingSupply())
//                        .marketCapUsd(tickerInfo.getMarketCapUsd())
                        .fundingRate(fundingRate.multiply(BigDecimal.valueOf(100)))
                        .openInterest(openInterest)
                        .openInterestUsd(openInterestValue)
                        .build();

                symbolInfoList.add(symbolInfo);
            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        });
        service.saveAll(symbolInfoList);
        log.info("Информация о токенах успешно сохранена. {}", symbolInfoList.get(0).getCreateDate());
    }
}
