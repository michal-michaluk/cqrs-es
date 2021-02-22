package devices.configuration.features.catalogue;

import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONArray;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

@AllArgsConstructor
public class StationsResponseAssert {
    private final ResponseEntity<String> response;

    public static StationsResponseAssert assertThat(ResponseEntity<String> response) {
        return new StationsResponseAssert(response);
    }

    public StationsResponseAssert isOK() {
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return this;
    }

    public StationsResponseAssert isCreated() {
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return this;
    }

    public StationsResponseAssert isBadRequest() {
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        return this;
    }

    public StationsResponseAssert hasSize(int size) {
        JSONArray found = JsonPath.read(response.getBody(), "$.content");
        Assertions.assertThat(found).as("stations list").hasSize(size);
        return this;
    }

    public StationsResponseAssert hasStationAsBody(Consumer<StationJsonAssert> station) {
        var found = JsonPath.read(response.getBody(), "$");
        station.accept(StationJsonAssert.assertThat(found));
        return this;
    }

    public StationsResponseAssert hasStationWithNameInBody(String name, Consumer<StationJsonAssert> station) {
        JSONArray found = JsonPath.read(response.getBody(), "$.content[?(@.name=='" + name + "')]");
        Assertions.assertThat(found).as("found stations for name %s", name).hasSize(1);
        station.accept(StationJsonAssert.assertThat(found.get(0)));
        return this;
    }

    public StationsResponseAssert hasStationWithNameInBody(String name) {
        JSONArray found = JsonPath.read(response.getBody(), "$.content[?(@.name=='" + name + "')]");
        Assertions.assertThat(found).as("found stations for name %s", name).hasSize(1);
        return this;
    }

    public StationsResponseAssert hasStationInstallationResponseAsBody(int expectedInstalled, int expectedUpdated, int expectedFailed) {
        Assertions.assertThat(JsonPath.<Object>read(response.getBody(), "$"))
                .as("$ of station installation response")
                .isNotNull();
        Assertions.assertThat(JsonPath.<Integer>read(response.getBody(), "$.installed"))
                .as("$.installed of station installation response")
                .isEqualTo(expectedInstalled);
        Assertions.assertThat(JsonPath.<Integer>read(response.getBody(), "$.updated"))
                .as("$.updated of station installation response")
                .isEqualTo(expectedUpdated);
        Assertions.assertThat(JsonPath.<Integer>read(response.getBody(), "$.failed"))
                .as("$.failed of station installation response")
                .isEqualTo(expectedFailed);
        return this;
    }

    public StationsResponseAssert hasNoStationWithNameInBody(String name) {
        JSONArray found = JsonPath.read(response.getBody(), "$.content[?(@.name=='" + name + "')]");
        Assertions.assertThat(found).as("found stations for name %s", name).hasSize(0);
        return this;
    }
}
