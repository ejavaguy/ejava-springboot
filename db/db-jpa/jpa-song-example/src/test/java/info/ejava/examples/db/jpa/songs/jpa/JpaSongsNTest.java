package info.ejava.examples.db.jpa.songs.jpa;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dao.JpaSongDAO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class JpaSongsNTest {
    @Autowired
    private JpaSongDAO jpaDao;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void cleanup() {
        jpaDao.deleteAll();
    }

    @Test
    void create() {
        //given
        Song song = mapper.map(dtoFactory.make());
        
        //when
        jpaDao.create(song);

        //then
        then(jpaDao.existsById(song.getId())).isTrue();
        then(song.getId()).isNotZero();
    }

    @Test
    void findById_exists() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song);

        //when finding
        Song dbSong = jpaDao.findById(song.getId());

        //then entity is persisted
        then(dbSong.getId()).isEqualTo(song.getId());
        then(dbSong.getTitle()).isEqualTo(song.getTitle());
        then(dbSong.getArtist()).isEqualTo(song.getArtist());
        then(dbSong.getReleased()).isEqualTo(song.getReleased());
    }


    @Test
    void findById_does_not_exist() {
        //given a persisted instance
        int missingId = 12345;

        //when
        Song dbSong = jpaDao.findById(missingId);

        //then
        then(dbSong).isNull();
    }

    @Test
    @Transactional
    void update_entity() {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        String newTitle = dtoFactory.title();

        //when - updating
        originalSong.setTitle(newTitle);
        jpaDao.flush();

        //then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        then(dbSong.getTitle()).isEqualTo(newTitle);
    }

    @Test
    void update_exists() {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        //when - updating
        updatedSong = jpaDao.update(updatedSong);

        //then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    @Test
    void update_does_not_exist() {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        //when
        updatedSong = jpaDao.update(updatedSong);

        //then - update missing will create
        Song dbSong = jpaDao.findById(originalSong.getId());
        then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    @Test
    @Transactional
    void delete_exists() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song);
        jpaDao.flush();

        //when - deleting
        jpaDao.delete(song);

        //then - no long in DB
        jpaDao.flush();
        then(jpaDao.findById(song.getId())).isNull();
    }

    @Test
    void delete_does_not_exist() {
        //given a bad ID
        int missingId = 12345;

        //when - deleting
        jpaDao.deleteById(missingId);
    }

    @Test
    void transaction_missing() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when - persist is called without a tx, an exception is thrown
        //em.persist(song);
        assertThatThrownBy(()->em.persist(song))
                .isInstanceOf(TransactionRequiredException.class);
    }

    @Test
    @Transactional
    void transaction_present_in_caller() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when  - persist called within caller transaction, no exception thrown
        em.persist(song);
        em.flush(); //force DB interaction

        //then
        then(em.find(Song.class, song.getId())).isNotNull();
    }

    @Test
    void transaction_present_in_component() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when  - persist called within component transaction, no exception thrown
        jpaDao.create(song);

        //then
        then(jpaDao.findById(song.getId())).isNotNull();
    }


    @Test
    void transaction_common_needed() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song); //song is detached at this point

        //when - removing detached entity we get an exception
        //jpaDao.delete(song);
        assertThatThrownBy(()->jpaDao.delete(song))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    void transaction_common_present() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song); //song is detached at this point

        //when - removing managed entity, it works
        jpaDao.delete(song);

        //then
        then(jpaDao.findById(song.getId())).isNull();
    }
}
