package devices.configuration.legacy;

import devices.configuration.features.catalogue.StationsFixture;
import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class EmobilityStationTopic {

    private static final String TOPIC_EMOBILITY_STATION_SNAPSHOT = "emobility-station-snapshot";
    private static final String TOPIC_EMOBILITY_STATION = "emobility-station";
    private static final String TOPIC_LOCATION_UPDATES = "location-updates";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public EmobilityStationTopic(@Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        this.kafkaTemplate = kafkaTemplate(bootstrapAddress);
    }

    private KafkaTemplate<String, String> kafkaTemplate(String bootstrapAddress) {
        return new KafkaTemplate<>(producerFactory(bootstrapAddress));
    }

    private ProducerFactory<String, String> producerFactory(String bootstrapAddress) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public void stationSnapshot(String json) {
        kafkaTemplate.send(TOPIC_EMOBILITY_STATION_SNAPSHOT, json);
    }

    public void stationMetadataUpdated(String stationName) {
        stationMetadataUpdated(stationName, StationsFixture.Locations.dusartstraatInAmsterdam());
    }

    public void stationMetadataUpdated(String stationName, Location location) {
        @Language("JSON") String json = "{ \n"
                + "  \"type\" : \"StationMetadataUpdated\",\n"
                + "  \"id\" : \"110ea1b6-73ad-45e0-8929-8f8f8c4dd24f\",\n"
                + "  \"occurrenceTime\" : 1.571139145968E9,\n"
                + "  \"lcpName\" : \"LCP emobilitylcppcu\",\n"
                + "  \"stationName\" : \"" + stationName + "\",\n"
                + "  \"location\" : "
                + nullableObject(location, () -> "{\n"
                + "    \"city\" : " + nullableString(location, Location::getCity) + ",\n"
                + "    \"houseNumber\" : " + nullableString(location, Location::getHouseNumber) + ",\n"
                + "    \"street\" : " + nullableString(location, Location::getStreet) + ",\n"
                + "    \"zipcode\" : " + nullableString(location, Location::getPostalCode) + ",\n"
                + "    \"country\" : " + nullableString(location, Location::getCountryISO) + ",\n"
                + "    \"longitude\" : " + nullableNumber(location.getCoordinates(), GeoLocation::getLongitude) + ",\n"
                + "    \"latitude\" : " + nullableNumber(location.getCoordinates(), GeoLocation::getLatitude) + "\n"
                + "  }") + ",\n"
                + "  \"billing\" : true,\n"
                + "  \"showOnMap\" : false,\n"
                + "  \"autoStart\" : false,\n"
                + "  \"remoteControl\" : false,\n"
                + "  \"reimbursement\" : true\n"
                + "}";
        kafkaTemplate.send(TOPIC_EMOBILITY_STATION, json);
    }

    public void locationUpdated(List<String> stations, Location oldLocation, Location newLocation) {
        @Language("JSON") String json = "{\n" +
                "   \"type\":\"LocationUpdated\",\n" +
                (stations == null ? "" : "\"stations\":[" + join(stations) + "],\n") +
                "   \"oldLocation\":{\n" +
                "      \"city\":" + nullableString(oldLocation, Location::getCity) + ",\n" +
                "      \"houseNumber\":" + nullableString(oldLocation, Location::getHouseNumber) + ",\n" +
                "      \"street\":" + nullableString(oldLocation, Location::getStreet) + ",\n" +
                "      \"zipcode\":" + nullableString(oldLocation, Location::getPostalCode) + ",\n" +
                "      \"country\":" + nullableString(oldLocation, Location::getCountryISO) + ",\n" +
                "      \"longitude\":" + nullableNumber(oldLocation.getCoordinates(), GeoLocation::getLongitude) + ",\n" +
                "      \"latitude\":" + nullableNumber(oldLocation.getCoordinates(), GeoLocation::getLatitude) + "\n" +
                "   },\n" +
                "   \"newLocation\":{\n" +
                "      \"city\":" + nullableString(newLocation, Location::getCity) + ",\n" +
                "      \"houseNumber\":" + nullableString(newLocation, Location::getHouseNumber) + ",\n" +
                "      \"street\":" + nullableString(newLocation, Location::getStreet) + ",\n" +
                "      \"zipcode\":" + nullableString(newLocation, Location::getPostalCode) + ",\n" +
                "      \"country\":" + nullableString(newLocation, Location::getCountryISO) + ",\n" +
                "      \"longitude\":" + nullableNumber(newLocation.getCoordinates(), GeoLocation::getLongitude) + ",\n" +
                "      \"latitude\":" + nullableNumber(newLocation.getCoordinates(), GeoLocation::getLatitude) + "\n" +
                "   },\n" +
                "   \"id\":\"5c4fd1ed-01bd-49dd-8047-3b43b7927794\",\n" +
                "   \"occurrenceTime\":1.598436928308E9\n" +
                "}";
        kafkaTemplate.send(TOPIC_LOCATION_UPDATES, json);
    }

    @NotNull
    private String join(List<String> stations) {
        return stations.stream().collect(Collectors.joining("\",\"", "\"", "\""));
    }

    private <T> String nullableObject(T object, Supplier<String> getter) {
        if (object == null) {
            return "null";
        }
        return getter.get();
    }

    private <T> String nullableString(T object, Function<T, Object> getter) {
        if (object == null) {
            return "null";
        }
        Object value = getter.apply(object);
        return value != null ? "\"" + value + "\"" : "null";
    }

    private <T> String nullableNumber(T object, Function<T, Object> getter) {
        if (object == null) {
            return "null";
        }
        Object value = getter.apply(object);
        return value != null ? "" + value : "null";
    }

    public void send(@Language("JSON") String json) {
        kafkaTemplate.send(TOPIC_EMOBILITY_STATION, json);
    }
}

