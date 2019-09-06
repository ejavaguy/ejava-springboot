package info.ejava.examples.svc.springmvc.todos.dto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

@Slf4j
public class JsonbTest extends MarshallingTest {
    private Jsonb builder;
            
    @BeforeEach
    public void setupJsonb() {
        JsonbConfig config=new JsonbConfig();
        //config.setProperty(JsonbConfig.FORMATTING, true);
        //config.setProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY, PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        //config.setProperty(JsonbConfig.NULL_VALUES, true); //helps us spot fields we don't want
        builder = JsonbBuilder.create(config);
    }
    
    @Override
    protected <T> String marshal(T object) {
        if (object==null) { return ""; }
        
        String buffer = builder.toJson(object);
        log.info("{} toJSON: {}", object, buffer);
        return buffer;        
    }

    @Override
    protected <T> T demarshal(Class<T> type, String buffer)  {
        T result = (T) builder.fromJson(buffer, type);
        log.info("{} fromJSON: {}", buffer, result);
        return result;
    }
}
