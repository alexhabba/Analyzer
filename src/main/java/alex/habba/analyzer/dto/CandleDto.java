package alex.habba.analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandleDto {

    private LocalDateTime createDate;
    private String symbol;
    private double volBuy;
    private double volSell;
    private double vol;
    private double open;
    private double close;
    private double low;
    private double high;
    private int interval;
}
