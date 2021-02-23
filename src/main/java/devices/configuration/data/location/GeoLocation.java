package devices.configuration.data.location;

import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Embeddable
@Accessors(chain = true)
public class GeoLocation {
    @NotBlank
    private String latitude;
    @NotBlank
    private String longitude;

    public Point toPoint() {
        return Point.of(latitude, longitude);
    }

    @Value(staticConstructor = "of")
    public static class Point {
        BigDecimal latitude;
        BigDecimal longitude;

        public static Point of(String latitude, String longitude) {
            return new Point(new BigDecimal(latitude), new BigDecimal(longitude));
        }

        public boolean inRect(Point bottomLeft, Point topRight) {
            return latitude.compareTo(bottomLeft.getLatitude()) >= 0
                    && latitude.compareTo(topRight.getLatitude()) <= 0
                    && longitude.compareTo(bottomLeft.getLongitude()) >= 0
                    && longitude.compareTo(topRight.getLongitude()) <= 0;
        }
    }
}
