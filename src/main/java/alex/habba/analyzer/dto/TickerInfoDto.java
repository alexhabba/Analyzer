package alex.habba.analyzer.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TickerInfoDto {
    public String ticker;
    public String coinId;
    public String name;
    public BigDecimal marketCapUsd;
    public BigDecimal circulatingSupply;
    public java.util.Map<String, String> platforms = new java.util.HashMap<>();

    @Override
    public String toString() {
        return "TickerInfo{" +
                "ticker='" + ticker + '\'' +
                ", coinId='" + coinId + '\'' +
                ", name='" + name + '\'' +
                ", marketCapUsd=" + marketCapUsd +
                ", circulatingSupply=" + circulatingSupply +
                ", platforms=" + platforms +
                '}';
    }
}
