package devices.configuration.remote


import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static devices.configuration.remote.Protocol.IOT16
import static devices.configuration.remote.Protocol.IOT20

class IntervalRulesTest extends Specification {

    IntervalRules rules = IntervalRulesFixture.specifiedRules()
    static def any = ""

    @Unroll
    def "should calculate interval rules based on device info"() {
        given:
        def device = new Deviceish(
                deviceId, vendor, model, protocol
        )
        expect:
        rules.calculateInterval(device) == interval

        where:
        deviceId       | vendor           | model                   | protocol || interval
        "EVB-P4562137" | any              | any                     | IOT16    || seconds(600)
        "t53_8264_019" | any              | any                     | IOT16    || seconds(2700)
        any            | "ChargeStorm AB" | "Chargestorm Connected" | IOT16    || seconds(60)
        any            | "EV-BOX"         | "G3-M5320E-F2.628s"     | IOT16    || seconds(120)
        any            | any              | any                     | IOT20    || seconds(600)
        any            | any              | any                     | IOT16    || seconds(1800)
    }

    private static Duration seconds(Integer seconds) {
        Duration.ofSeconds(seconds)
    }
}
