package devices.configuration;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class DevRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AppRunner.class)
                .profiles("integration-test", "dev-run")
                .run(args);
    }
}
