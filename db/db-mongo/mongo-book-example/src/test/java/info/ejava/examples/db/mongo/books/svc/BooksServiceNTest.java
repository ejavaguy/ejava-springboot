package info.ejava.examples.db.mongo.books.svc;

import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.dto.BookDTO;
import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.repo.BooksRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class})
@ActiveProfiles(profiles="test", resolver = TestProfileResolver.class)
@Tag("springboottest")
@Slf4j
@DisplayName("Songs Service NTest")
public class BooksServiceNTest {
    @Autowired
    private BooksRepository songsRepository;
    @Autowired
    private BooksService songsService;
    @Autowired
    private BookDTOFactory songDtoFactory;
    @Autowired
    private BooksMapper songsMapper;

    @BeforeEach
    void init() {
        songsRepository.deleteAll();
    }

    void thenSongsEqual(BookDTO result, BookDTO expected) {
        if (expected.getId()==null) { //we don't know what the value will be
            then(result.getId()).isNotNull(); //but it will be assigned
        } else {
            then(result.getId()).isEqualTo(expected.getId());
        }
        then(result.getAuthor()).isEqualTo(expected.getAuthor());
        then(result.getTitle()).isEqualTo(expected.getTitle());
        then(result.getPublished()).isEqualTo(expected.getPublished());
    }

    @Test
    void create_song() {
        //given
        BookDTO song = songDtoFactory.make();
        //when
        BookDTO result = songsService.createBook(song);
        //then
        then(result.getId()).isNotNull();
        log.info("{}", JsonUtil.instance().marshal(result));
        thenSongsEqual(result, song);
    }

    @Test
    void get_song_exists() {
        //given
        BookDTO song = songDtoFactory.make();
        BookDTO existingSong = songsService.createBook(song);
        //when
        BookDTO returnedSong = songsService.getBook(existingSong.getId());
        //then
        then(returnedSong).isNotNull();
        thenSongsEqual(returnedSong, existingSong);
    }

    @Test
    void get_song_not_exist() {
        //given
        String doesNotExist="1234";
        //when
        NotFoundException ex = catchThrowableOfType(() -> songsService.getBook(doesNotExist),
                NotFoundException.class);
        //then
        log.info("{}", ex.toString());
        then(ex).hasMessage("Book id[%s] not found", doesNotExist);
    }

    @Test
    void get_songs() {
        //given
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusDays(7);
        List<Book> songs = songsMapper.map(songDtoFactory.listBuilder().books(10,10));
        long modified=IntStream.range(0,songs.size())
                .peek(i->songs.set(i, songs.get(i).withPublished(lastWeek)))
                .filter(i->i%2==0)
                .peek(i->songs.set(i, songs.get(i).withPublished(today)))
                .count();
        songsRepository.saveAll(songs);
        //when
        Page<BookDTO> page = songsService.findPublishedAfter(yesterday,
                PageRequest.of(0, 3, Sort.by("id")));
        //then
        then(page.getNumberOfElements()).isEqualTo(3);
        then(page.getTotalElements()).isEqualTo(modified);
    }

    @Test
    void delete_song() {
        //given
        Book song = songsMapper.map(songDtoFactory.make());
        songsRepository.save(song);
        then(songsRepository.existsById(song.getId())).isTrue();
        //when
        songsService.deleteBook(song.getId());
        //then
        then(songsRepository.existsById(song.getId())).isFalse();
    }

}
