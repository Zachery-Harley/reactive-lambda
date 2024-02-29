package uk.co.zacheryharley.lambdalight.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import uk.co.zacheryharley.lambdalight.json.JsonUtils;

public class JsonContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(ObjectMapper.class, JsonUtils::buildObjectMapper);
    }

}
