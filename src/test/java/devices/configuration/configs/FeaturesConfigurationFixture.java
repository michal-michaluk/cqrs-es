package devices.configuration.configs;

import org.jetbrains.annotations.NotNull;

public class FeaturesConfigurationFixture {

    @NotNull
    public static FeaturesConfigurationEntity entity(String name, String configuration) {
        FeaturesConfigurationEntity entity = new FeaturesConfigurationEntity();
        entity.setName(name);
        entity.setConfiguration(configuration);
        return entity;
    }
}
