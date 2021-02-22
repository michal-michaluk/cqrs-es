package devices.configuration;

import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;

public class JsonAssert {

    private final String actual;

    public JsonAssert(String actual) {
        this.actual = actual;
    }

    public static JsonAssert assertThat(Object object) {
        return new JsonAssert(json(object));
    }

    public static JsonAssert assertThat(String json) {
        return new JsonAssert(json);
    }

    @SneakyThrows
    public JsonAssert isExactlyLike(Object object) {
        JSONAssert.assertEquals(json(object), actual, true);
        return this;
    }

    @SneakyThrows
    public JsonAssert isExactlyLike(String json) {
        JSONAssert.assertEquals(json, actual, true);
        return this;
    }

    @SneakyThrows
    public JsonAssert hasFieldsLike(Object object) {
        JSONAssert.assertEquals(json(object), actual, false);
        return this;
    }

    @SneakyThrows
    public JsonAssert hasFieldsLike(String json) {
        JSONAssert.assertEquals(json, actual, false);
        return this;
    }


    @SneakyThrows
    public static String json(Object object) {
        return JsonConfiguration.OBJECT_MAPPER.writeValueAsString(object);
    }
}
