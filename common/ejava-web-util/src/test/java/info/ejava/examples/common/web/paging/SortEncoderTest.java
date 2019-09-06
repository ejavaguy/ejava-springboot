package info.ejava.examples.common.web.paging;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.data.domain.Sort.*;

@Slf4j
public class SortEncoderTest {
    private Sort sort = by(Direction.ASC, "title", "artist")
            .and(by(Direction.DESC, "release"))
            .and(by(Direction.ASC, "id"));

    @Test
    void marshal_into_query_string() {
        //given
        log.info("sort={}", sort);
        String expectedString="title:ASC,artist:ASC,release:DESC,id:ASC";
        //when
        String sortString = new SortEncoder(sort).getQueryString();
        //then
        then(sortString).isEqualTo(expectedString);
    }

    @Test
    void marshal_unsorted_into_query_string() {
        //given
        Sort nosort = Sort.unsorted();
        log.info("sort={}", nosort);
        String expectedString=null;
        //when
        String sortString = new SortEncoder(nosort).getQueryString();
        //then
        log.info("sortString={}", sortString);
        then(sortString).isEqualTo(expectedString);
    }

    @Test
    void valueOf_from_query_string() {
        //given
        String sortString="title:DESC,artist:ASC,id:DESC";
        Sort expectedSort = by(Order.desc("title"),Order.asc("artist"),Order.desc("id"));

        //when
        SortEncoder resultSortSpec = SortEncoder.valueOf(sortString);

        //then
        log.info("sort={}", resultSortSpec);
        Sort resultSort = resultSortSpec.getSort();
        then(resultSort).isEqualTo(expectedSort);
    }

    private static Stream<Arguments> valueOf_query_string() {
        return Stream.of(
                Arguments.of("", Sort.unsorted()),
                Arguments.of("  ", Sort.unsorted()),
                Arguments.of("\t", Sort.unsorted()),
                Arguments.of("\n", Sort.unsorted()),
                Arguments.of(null, Sort.unsorted())
        );
    }

    @ParameterizedTest()
    @MethodSource
    void valueOf_query_string(String sortString, Sort expectedSort) {
        //when
        SortEncoder resultSortSpec = SortEncoder.valueOf(sortString);
        //then
        log.info("sort={}", resultSortSpec);
        Sort resultSort = resultSortSpec.getSort();
        then(resultSort).isEqualTo(expectedSort);
    }

    @Test
    void marshal_unmarshal_query_string() {
        //given
        log.info("sort={}", sort);
        //when
        String sortString = new SortEncoder(sort).getQueryString();
        Sort resultSort = SortEncoder.valueOf(sortString).getSort();
        //then
        then(resultSort).isEqualTo(resultSort);
    }
}
