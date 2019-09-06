package info.ejava.examples.common.web.paging;

import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This class is used to encode and decode a Spring Data Sort for use
 * in API calls so that it can be sent in a single queryParameter and
 * returned in a single string property or XML attribute.
 */
public class SortEncoder {
    public static final String SORT_SEPARATOR =",";
    public static final String DIRECTION_SEPARATOR=":";
    public static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.ASC;

    private final Sort sort;

    public SortEncoder() {
        sort = Sort.unsorted();
    }
    public SortEncoder(Sort sort) {
        this.sort = sort!=null ? sort : Sort.unsorted();
    }

    public Sort getSort() {
        return sort;
    }

    public boolean isSorted() {
        return sort.isSorted();
    }

    public String getQueryString() {
        if (sort.isEmpty()) {
            return null;
        }
        return sort.stream()
                .map(s->s.getProperty() + DIRECTION_SEPARATOR + s.getDirection())
                .collect(Collectors.joining(SORT_SEPARATOR));
    }

    public static SortEncoder valueOf(String sortString) {
        Sort sort = Sort.unsorted();
        if (sortString!=null && !sortString.isBlank()) {
            sort = Arrays.stream(sortString.split(SORT_SEPARATOR))
                    //title:ASC
                    .map(sortTok-> parseIndividualOrder(sortTok))
                    //title:ASC,release:DESC
                    .reduce(Sort.unsorted(), (s1,s2)->s1.and(s2));
        }
        return new SortEncoder(sort);
    }

    //title:ASC
    private static Sort parseIndividualOrder(String orderString) {
        String[] orderTokens = orderString.split(DIRECTION_SEPARATOR);
        String property = orderTokens[0].trim();
        String directionString = orderTokens.length >= 2 ? orderTokens[1] : "";
        Sort.Direction direction = Sort.Direction
                .fromOptionalString(directionString.trim())
                .orElse(DEFAULT_DIRECTION);

        return property!=null && !property.isBlank() ?
                Sort.by(direction, property) : Sort.unsorted();
    }

    @Override
    public String toString() {
        return sort.toString();
    }
}
