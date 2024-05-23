package uk.co.zacheryharley.lambdalight.reactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.zacheryharley.lambdalight.json.JsonUtils;

public class ReactiveJson {
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();

    private ReactiveJson() {
        // Static only
    }

    public static <T> TransformerUtils.MonoToMono<T, String> asString() {
        return t -> t
            .handle((obj, sink) -> {
                try {
                    sink.next(MAPPER.writeValueAsString(obj));
                } catch (JsonProcessingException e) {
                    sink.error(e);
                }
            });
    }

    public static <T> TransformerUtils.MonoToMono<String, T> asObject(Class<T> clazz) {
        return json -> json
            .handle((obj, sink) -> {
                try {
                    sink.next(MAPPER.readValue(obj, clazz));
                } catch (JsonProcessingException e) {
                    sink.error(e);
                }
            });
    }

    public static <T> TransformerUtils.MonoToMono<String, T> asObject(TypeReference<T> clazz) {
        return json -> json
            .handle((obj, sink) -> {
                try {
                    sink.next(MAPPER.readValue(obj, clazz));
                } catch (JsonProcessingException e) {
                    sink.error(e);
                }
            });
    }

}
