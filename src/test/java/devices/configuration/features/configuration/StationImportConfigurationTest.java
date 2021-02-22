package devices.configuration.features.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static devices.configuration.features.configuration.FeaturesConfigurationProvider.STATIONS_FILE_IMPORT;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class StationImportConfigurationTest {

    @Autowired
    private FeaturesConfigurationRepository repo;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void verify_configuration_can_be_loaded() {
        //given
        FeaturesConfigurationEntity input = getFeaturesConfigurationEntity();

        // when
        repo.save(input);
        var result = repo.findByName(STATIONS_FILE_IMPORT);

        // then
        assertThat(result).hasValue(input);
    }

    @Test
    public void load_from_json_with_correct_type() throws IOException {
        //given
        FeaturesConfigurationEntity input = getFeaturesConfigurationEntity();
        FeatureConfiguration configuration = objectMapper.readValue(
                StationImportConfigurationTest.class.getResourceAsStream("/station-import-configuration.json"),
                FeatureConfiguration.class
        );
        assertThat(configuration)
                .isNotNull()
                .isInstanceOf(StationImportConfiguration.class)
                .isEqualTo(input.getConfiguration());
    }

    @Test
    public void write_type_attribute_to_json() throws IOException {
        FeaturesConfigurationEntity given = getFeaturesConfigurationEntity();

        String actual = objectMapper.writeValueAsString(given.getConfiguration());

        Assertions.assertThat(actual)
                .contains("\"type\":\"StationImportConfiguration\"");
    }

    @NotNull
    private FeaturesConfigurationEntity getFeaturesConfigurationEntity() {
        List<ChargingStationType> stationTypes = new ArrayList<>();
        stationTypes.add(new ChargingStationType("ABB", "Terra 53"));
        stationTypes.add(new ChargingStationType("ABB", "Terra 54"));
        stationTypes.add(new ChargingStationType("ABB", "Terra DC wallbox"));
        stationTypes.add(new ChargingStationType("ABB", "Terra HP High Power"));
        stationTypes.add(new ChargingStationType("Alfen", "Eve Double Pro-line"));
        stationTypes.add(new ChargingStationType("Alfen", "Eve Single Pro-line"));
        stationTypes.add(new ChargingStationType("Alfen", "Eve Single S-line"));
        stationTypes.add(new ChargingStationType("Alfen", "ICU EVe"));
        stationTypes.add(new ChargingStationType("Alfen", "ICU EVE Mini"));
        stationTypes.add(new ChargingStationType("Alfen", "Twin"));
        stationTypes.add(new ChargingStationType("Charge Amps", "HaloWallbox"));
        stationTypes.add(new ChargingStationType("ChargeStorm", "EVA"));
        stationTypes.add(new ChargingStationType("ChargeStorm", "Grindcontroller"));
        stationTypes.add(new ChargingStationType("CTEK", "Chargestorm Connected"));

        List<String> chargingPointTypes = new ArrayList<>();
        chargingPointTypes.add("Fast Charger CCS");
        chargingPointTypes.add("Fast charger CCS HP");
        chargingPointTypes.add("Fast charger CHAdeMO");
        chargingPointTypes.add("Fast charger CHAdeMO HP");
        chargingPointTypes.add("Fast Charging Typ2");
        chargingPointTypes.add("Type 1 (1phase AC 3,7kW Type2 socket)");
        chargingPointTypes.add("Type 2 (1phase AC 3,7kW Type1 attached cable)");
        chargingPointTypes.add("Type 3 (1phase AC 3,7kW Type2 attached cable)");
        chargingPointTypes.add("Type 4 (1phase AC 7,4kW Type2 socket)");
        chargingPointTypes.add("Type 5 (1phase AC 7,4kW Type1 attached cable)");
        chargingPointTypes.add("Type 6 (3phase AC 11kW Type2 socket)");
        chargingPointTypes.add("Type 7 (3phase AC 11kW Type2 attached cable)");
        chargingPointTypes.add("Type 8 (3phase AC 22kW Type2 socket)");
        chargingPointTypes.add("Type 9 (3phase AC 22kW Type2 attached cable)");
        chargingPointTypes.add("Type 10 (3phase AC 43kW Type2 socket)");
        chargingPointTypes.add("Type 11 (3phase AC 11kW Type2 socket, Schuko)");
        chargingPointTypes.add("Type 12 (3phase AC 22kW Type2 socket, Schuko)");

        List<String> cpos = new ArrayList<>();
        cpos.add("ACS Group");
        cpos.add("Allego");
        cpos.add("Allego(BE)");
        cpos.add("Allego(DE)");
        cpos.add("Allego(NL)");
        cpos.add("Allego GmbH");
        cpos.add("Alperia Smart Mobility(CPO)");
        cpos.add("Axpo Energy Solutions Italia S.p.A.");
        cpos.add("BMW(Schweiz) AG");
        cpos.add("BP DE");
        cpos.add("BP NL");
        cpos.add("Bee Charging Solutions");
        cpos.add("Blue Corner");
        cpos.add("BlueCorner");
        cpos.add("BlueCurrent");
        cpos.add("CPO - App");
        cpos.add("Charge Tech GmbH");
        cpos.add("ChargeNet NZ");
        cpos.add("Chargepartner Network GmbH");
        cpos.add("CleanCharge Solutions");
        cpos.add("Clever A/S");
        cpos.add("Comfortcharge GmbH");
        cpos.add("Commercial Charger GmbH");
        cpos.add("DB Energie GmbH");
        cpos.add("Digital Energy Solutions GmbH &Co.KG");
        cpos.add("Dios Fastigheter");
        cpos.add("Dr.Ing.h.c.F.Porsche AG");
        cpos.add("Duferco Energia Spa");
        cpos.add("E - Flux B.V");
        cpos.add("E - Flux B.V.");
        cpos.add("E - Wald");
        cpos.add("E.ON Drive DE * EDR");
        cpos.add("E.ON Drive DE * EON");
        cpos.add("E2E_A_Team_Monitoring");
        cpos.add("EDP Comercializadora, SAU");
        cpos.add("ELMŰ Nyrt");
        cpos.add("EMOBITALY");
        cpos.add("ENECO");
        cpos.add("EVBOX");
        cpos.add("EVBOX Supplier");
        cpos.add("EVH GmbH");
        cpos.add("EVN(CPO) EVN Energievertrieb GmbH & Co KG");
        cpos.add("EVnet.NL");
        cpos.add("Ebee Smart Technologies GmbH");
        cpos.add("EnBW Energie Baden - Württemberg AG");
        cpos.add("Endesa CPO 2");
        cpos.add("Eneco eMobility B.V.");
        cpos.add("Energie AG OberAustria Power Solution");
        cpos.add("Energie AG OÖ Vertrieb GmbH");
        cpos.add("Energie Burgenland_BEÖ");
        cpos.add("Energie Steiermark Kunden GmbH");
        cpos.add("Enio GmbH");
        cpos.add("Eniwa");
        cpos.add("Fastned");
        cpos.add("FlowCharging");
        cpos.add("Freshmile Services");
        cpos.add("GREENFLUX");
        cpos.add("Goteborg");
        cpos.add("Green Motion SA");
        cpos.add("GreenFlux Assets B.V.");
        cpos.add("HUJ - Nobil");
        cpos.add("Heldele GmbH CPO");
        cpos.add("Hrvatski Telekom d.d.");
        cpos.add("IBERDROLA CLIENTES");
        cpos.add("IBIL Gestor de Carga de VE S.A.");
        cpos.add("IONITY GmbH");
        cpos.add("InCharge NO");
        cpos.add("InCharge SE");
        cpos.add("InCharge TEST");
        cpos.add("InCharge UK");
        cpos.add("JOINON");
        cpos.add("KELAG - Kärntner Elektrizitäts - AG(CPO)");
        cpos.add("Klovern");
        cpos.add("LINZ STROM GmbH");
        cpos.add("LastMileSolution");
        cpos.add("Leipziger Stadtwerke");
        cpos.add("Liechtensteinische Kraftwerke");
        cpos.add("MAINGAU Energie GmbH");
        cpos.add("MOVE Mobility AG");
        cpos.add("MOVE Mobility AG(CCC)");
        cpos.add("MOVE Mobility AG(CCI)");
        cpos.add("Moixa");
        cpos.add("Moixa Germany");
        cpos.add("NeMO - CPO1");
        cpos.add("Nuon Alfen");
        cpos.add("Nuon NL");
        cpos.add("PLUG 'n CHARGE GmbH");
        cpos.add("Plan - net solar d.o.o.");
        cpos.add("Plug 'n Roll");
        cpos.add("Powerdale");
        cpos.add("QMX");
        cpos.add("ROUTE220");
        cpos.add("Renovatio Asset Management");
        cpos.add("Rev");
        cpos.add("Robert Bosch GmbH");
        cpos.add("SMATRICS GmbH &Co KG (CPO)");
        cpos.add("SPIE CityNetworks");
        cpos.add("Salzburg AG");
        cpos.add("Spin8 srl (CPO)");
        cpos.add("Stadtwerke Lindau(B) GmbH & Co.KG");
        cpos.add("Stromnetz Hamburg");
        cpos.add("Stromnetz Hamburg GmbH");
        cpos.add("Swarco Traffic Austria");
        cpos.add("Taubert Consulting GmbH");
        cpos.add("The New Motion");
        cpos.add("Threeforce B.V. (CPO)");
        cpos.add("VW Kraftwerk GmbH");
        cpos.add("VandebronEnergieB.V.");
        cpos.add("Vattenfall DE");
        cpos.add("Virta Ltd (CPO)");
        cpos.add("Vorarlberger Kraftwerke AG (AT)");
        cpos.add("Vorarlberger Kraftwerke AG (DE)");
        cpos.add("Vorarlberger Kraftwerke AG (DE * MOO)");
        cpos.add("Vorarlberger Kraftwerke AG(MOO)");
        cpos.add("Wien Energie");
        cpos.add("chargeIT mobility");
        cpos.add("chargecloud GmbH");
        cpos.add("eCarUp AG");
        cpos.add("easy4you");
        cpos.add("has.to.be gmbh");
        cpos.add("inno2grid GmbH");
        cpos.add("innogy eMobility Solutions GmbH");
        cpos.add("wallbe GmbH");

        StationImportConfiguration config = new StationImportConfiguration(cpos, stationTypes, chargingPointTypes);

        FeaturesConfigurationEntity entity = new FeaturesConfigurationEntity();
        entity.setName(STATIONS_FILE_IMPORT);
        entity.setConfiguration(config);
        return entity;
    }
}