package info.ejava.examples.db.jpa.songs.jdbc;

import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dao.JdbcSongDAO;
import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class JdbcSongsNTest {
    @Autowired
    private JdbcSongDAO jdbcDao;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private DataSource dataSource;


    @BeforeEach
    void cleanup() throws SQLException {
        log.info("dbUrl={}", dataSource.getConnection().getMetaData().getURL());
        jdbcDao.deleteAll();
    }

    @Test
    void create() throws SQLException {
        //given an entity instance
        Song song = mapper.map(dtoFactory.make());

        //when persisting
        jdbcDao.create(song);

        //then entity is persisted
        then(song.getId()).isNotZero();
        then(jdbcDao.existsById(song.getId())).isTrue();
    }

    @Test
    void findById_exists() throws SQLException {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jdbcDao.create(song);

        //when finding
        Song dbSong = jdbcDao.findById(song.getId());

        //then entity is persisted
        then(dbSong.getId()).isEqualTo(song.getId());
        then(dbSong.getTitle()).isEqualTo(song.getTitle());
        then(dbSong.getArtist()).isEqualTo(song.getArtist());
        then(dbSong.getReleased()).isEqualTo(song.getReleased());
    }


    @Test
    void findById_does_not_exist() throws SQLException {
        //given a persisted instance
        int missingId = 12345;

        //when/then finding
        assertThatThrownBy(()->jdbcDao.findById(missingId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void update_exists() throws SQLException {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jdbcDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        //when - updating
        jdbcDao.update(updatedSong);

        //then - db has new state
        Song dbSong = jdbcDao.findById(originalSong.getId());
        then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    @Test
    void update_does_not_exist() throws SQLException {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jdbcDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        //when - updating
        jdbcDao.update(updatedSong);

        //then - db has new state
        Song dbSong = jdbcDao.findById(originalSong.getId());
        then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    @Test
    void delete_exists() throws SQLException {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jdbcDao.create(song);

        //when - deleting
        jdbcDao.deleteById(song.getId());

        //then - no long in DB
        then(jdbcDao.existsById(song.getId())).isFalse();
    }

    @Test
    void delete_does_not_exist() throws SQLException {
        //given a bad ID
        int missingID = 12345;

        //when - deleting missing ID, then - exception
        assertThatThrownBy(()->jdbcDao.deleteById(missingID))
                .isInstanceOf(NoSuchElementException.class);
    }
}
