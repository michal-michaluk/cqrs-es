package devices.configuration.device;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Value;

import java.time.LocalTime;

import static devices.configuration.device.OpeningHours.OpeningTime.open24h;

@Value
public class OpeningHours {
    private final static OpeningHours ALWAYS_OPEN = new OpeningHours(true,
            new Week(open24h(), open24h(), open24h(), open24h(), open24h(), open24h(), open24h())
    );

    boolean alwaysOpen;
    Week opened;

    public static OpeningHours alwaysOpen() {
        return ALWAYS_OPEN;
    }

    public static OpeningHours openAt(
            OpeningTime monday,
            OpeningTime tuesday,
            OpeningTime wednesday,
            OpeningTime thursday,
            OpeningTime friday,
            OpeningTime saturday,
            OpeningTime sunday) {
        return new OpeningHours(false, new Week(
                monday, tuesday, wednesday, thursday, friday, saturday, sunday)
        );
    }

    public static OpeningHours alwaysOpenOrGiven(OpeningHours opening) {
        return opening == null ? OpeningHours.alwaysOpen() : opening;
    }

    @Value
    public static class Week {
        OpeningTime monday;
        OpeningTime tuesday;
        OpeningTime wednesday;
        OpeningTime thursday;
        OpeningTime friday;
        OpeningTime saturday;
        OpeningTime sunday;
    }

    @Value
    public static class OpeningTime {
        boolean open24h;
        boolean closed;
        @JsonSerialize(using = LocalTimeSerializer.class)
        LocalTime open;
        @JsonSerialize(using = LocalTimeSerializer.class)
        LocalTime close;

        public static OpeningTime closed() {
            return new OpeningTime(false, true, null, null);
        }

        public static OpeningTime open24h() {
            return new OpeningTime(true, false, null, null);
        }

        public static OpeningTime opened(int open, int close) {
            return new OpeningTime(false, false, LocalTime.of(open, 0), LocalTime.of(close, 0));
        }
    }
}
