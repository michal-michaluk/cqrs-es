package devices.configuration.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(exclude = "id")
public class Connector {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer ocppConnectorId;
    private String physicalReference;
    private String evseId;
    @Enumerated(EnumType.STRING)
    private ConnectorType type;
    @Enumerated(EnumType.STRING)
    private Format format;
    private BigDecimal power;
    private String voltage;
    private String amps;
    private String phases;
    private boolean ac;
    private boolean dc;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @JsonIgnore
    private Map<String, ?> legacy;

    public int getOcppConnectorId() {
        return ocppConnectorId == null ? -1 : ocppConnectorId;
    }

    public Integer getPhases() {
        return phases == null ? null : Integer.valueOf(phases);
    }

    public PowerType getPowerType() {
        return PowerType.from(isAc(), getPhases());
    }

    String toStringCsv(String delimiter) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(delimiter);
        sb.append(ocppConnectorId).append(delimiter);
        sb.append(physicalReference).append(delimiter);
        sb.append(evseId).append(delimiter);
        sb.append(type).append(delimiter);
        sb.append(format).append(delimiter);
        sb.append(voltage).append(delimiter);
        sb.append(phases).append(delimiter);
        sb.append(amps).append(delimiter);
        sb.append(booleanToString(ac)).append(delimiter);
        sb.append(booleanToString(dc)).append(delimiter);
        return sb.toString();
    }

    private String booleanToString(boolean value) {
        return value ? "YES" : "NO";
    }

    @JsonIgnore
    public BigDecimal getPowerInWatts() {
        return new BigDecimal(calculatePower().stripTrailingZeros().toPlainString());
    }

    @JsonIgnore
    public String getAdvertisedPower() {
        BigDecimal kiloWatts = calculatePower()
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        int significant = Math.max(2, kiloWatts.precision() - kiloWatts.scale());
        return kiloWatts
                .round(new MathContext(significant, RoundingMode.HALF_UP))
                .stripTrailingZeros().toPlainString() + "kW";
    }

    private BigDecimal calculatePower() {
        return new BigDecimal(voltage)
                .multiply(new BigDecimal(amps))
                .multiply(ac ? new BigDecimal(phases) : BigDecimal.ONE);
    }
}
