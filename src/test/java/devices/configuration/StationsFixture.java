package devices.configuration;

import java.util.Random;

public class StationsFixture {

    private static final Random random = new Random();

    public static String randomStationName() {
        return "station-" + random.nextInt(999);
    }

}
