package uk.co.zacheryharley.lambdalight.json;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class JsonUtils {

    private JsonUtils() {
        //Static util class
    }

    public static ObjectMapper buildObjectMapper() {
        return JsonMapper.builder()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .addModule(new Jdk8Module())
            .build();
    }

}
