package info.ejava.examples.common.web;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WebLoggingFilter {
    public static final List<String> DEFAULT_EXCLUDES= Collections.unmodifiableList(Arrays.asList(
            "upgrade-insecure-requests",
            "user-agent",
            "sec.*"));

    public static Filter logFilter() {
        return logFilter(DEFAULT_EXCLUDES);
    }
    public static Filter logFilter(List<String> excludes) {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        filter.setBeforeMessagePrefix(System.lineSeparator());
        filter.setAfterMessagePrefix(System.lineSeparator());
        filter.setIncludeHeaders(true);
        filter.setHeaderPredicate(h->{
            for (String pattern: excludes) {
                if (h.matches(pattern)) {
                    return false;
                }
            }
            return true;
        });
        return filter;
    }

}
