package devices.configuration.util;

import java.time.Clock;
import java.time.Instant;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tombstone {

    Instant deletedAt;
    String deletedBy;

    public static Tombstone atTime(Clock clock) {
        return new Tombstone(Instant.now(clock), "-");
    }
}
