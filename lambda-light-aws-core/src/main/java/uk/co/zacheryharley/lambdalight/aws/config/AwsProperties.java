package uk.co.zacheryharley.lambdalight.aws.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

@Validated
@ConfigurationProperties("aws")
public class AwsProperties {
    @NotNull(message = "Please define AWS region")
    private Region region;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
