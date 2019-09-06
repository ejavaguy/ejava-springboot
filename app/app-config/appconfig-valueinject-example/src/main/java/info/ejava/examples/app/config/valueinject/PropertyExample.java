package info.ejava.examples.app.config.valueinject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PropertyExample implements CommandLineRunner {
    private final String strVal;
    private final int intVal;
    private final boolean booleanVal;
    private final float floatVal;
    private final List<Integer> intList;
    private final List<Integer> intListDelimiter;
    private final int[] intArray;
    private final Set<Integer> intSet;
    private final Map<Integer,String> map;
    private final String mapValue;
    private final Map<String, String> systemProperties;

    public PropertyExample(
            @Value("${val.str:}") String strVal,
            @Value("${val.int:0}") int intVal,
            @Value("${val.boolean:false}") boolean booleanVal,
            @Value("${val.float:0.0}") float floatVal,
            @Value("${val.intList:}") List<Integer> intList,
            @Value("${val.intList:}") Set<Integer> intSet,
            @Value("${val.intList:}") int[] intArray,
            @Value("#{'${val.intListDelimiter:}'.split('!')}") List<Integer> intListDelimiter,
            @Value("#{${val.map:{}}}") Map<Integer,String> map,
            @Value("#{${val.map:{0:'',3:''}}[3]}") String mapValue,
            @Value("#{systemProperties}") Map<String, String> systemProperties) {
        this.strVal = strVal;
        this.intVal = intVal;
        this.booleanVal = booleanVal;
        this.floatVal = floatVal;
        this.intList = intList;
        this.intSet = intSet;
        this.intArray = intArray;
        this.intListDelimiter = intListDelimiter;
        this.map = map;
        this.systemProperties = systemProperties;
        this.mapValue = mapValue;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("strVal=" + strVal);
        System.out.println("intVal=" + intVal);
        System.out.println("booleanVal=" + booleanVal);
        System.out.println("floatVal=" + floatVal);
        System.out.println("intList=" + intList);
        System.out.println("intSet=" + intSet);
        System.out.println("intArray=" + Arrays.toString(intArray));
        System.out.println("intListDelimeter=" + intListDelimiter);
        System.out.println("map=" + map);
        System.out.println("systemProperties[user.timezone]=" + systemProperties.get("user.timezone"));
        System.out.println("mapValue=" + mapValue);
    }
}
