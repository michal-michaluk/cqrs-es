package devices.configuration.remote


import static devices.configuration.remote.IntervalRules.*
import static java.time.Duration.ofSeconds

class IntervalRulesFixture {
    static IntervalRules specifiedRules() {
        return new IntervalRules(
                List.of(
                        deviceId(ofSeconds(600), Set.of(
                                "EVB-P4562137", "ALF-9571445", "CS_7155_CGC100", "EVB-P9287312", "ALF-2844179")),
                        deviceId(ofSeconds(2700), Set.of(
                                "t53_8264_019", "EVB-P15079256", "EVB-P0984003", "EVB-P1515640", "EVB-P1515526"))
                ),
                List.of(
                        model(ofSeconds(60), "Alfen BV", "NG920-5250[6-9]"),
                        model(ofSeconds(60), "ChargeStorm AB", "Chargestorm Connected"),
                        model(ofSeconds(120), "EV-BOX", "G3-M5320E-F2.*")
                ),
                List.of(
                        protocol(ofSeconds(600), Protocol.IOT20)
                ),
                ofSeconds(1800)
        )
    }
}
