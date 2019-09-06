package info.ejava.examples.svc.content.quotes.dto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JsonbTest extends MarshallingTestBase {
    private Jsonb builder;
            
    @Override
    @BeforeEach
    public void init() {
        JsonbConfig config=new JsonbConfig()
                //.setProperty(JsonbConfig.DATE_FORMAT, "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S][XXX]")
                .setProperty(JsonbConfig.FORMATTING, true);
        builder = JsonbBuilder.create(config);
    }
    
    @Override
    public <T> String marshal(T object) {
        if (object==null) { return ""; }
        
        String buffer = builder.toJson(object);
        log.info("{} toJSON: {}", object, buffer);
        return buffer;        
    }

    @Override
    public <T> T unmarshal(Class<T> type, String buffer)  {
        T result = (T) builder.fromJson(buffer, type);
        log.info("{} fromJSON: {}", buffer, result);
        return result;
    }

    @Override
    protected String get_marshalled_adate(String dateText) {
        return String.format(DATES_JSON, dateText);
    }

    @Override
    protected String get_date(String marshalledQuote) {
        Pattern pattern = Pattern.compile(".*\"date\": \"(.+?)\",.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(marshalledQuote);

        if (matcher.matches()) {
            String date = matcher.group(1);
            return date;
        }
        return null;
    }

    @Override
    protected boolean canParseFormat(String format, ZoneOffset tz) {
        //JSONB cannot parse +05:00, but can parse +05 and Z
//        if (format.equals(ISO_8601_DATETIME5_FORMAT) && tz==null) {
//            return false;
//        }
        return true;
    }
}
