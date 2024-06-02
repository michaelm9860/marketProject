package michael.m.marketProject;

import michael.m.marketProject.config.FileStorageProperties;
import michael.m.marketProject.config.RSAKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {RSAKeyProperties.class, FileStorageProperties.class})
public class MarketProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketProjectApplication.class, args);
    }

}
