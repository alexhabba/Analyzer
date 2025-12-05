package alex.habba.analyzer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SymbolInfo {

    @Id
    @UuidGenerator
    private UUID id;
    private LocalDateTime createDate;
    private String symbol;

    private BigDecimal marketCapUsd;
    private BigDecimal circulatingSupply;
    private double vol;
    private double open;
    private double close;
    private double low;
    private double high;
    private int interval;
    private BigDecimal openInterest;
    private BigDecimal openInterestUsd;
    private BigDecimal fundingRate;

}
