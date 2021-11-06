package info.ejava.examples.db.jpa.songs.repo;

import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.oneUpId;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class})
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
@DisplayName("Repository Crud Methods")
class SongsCrudRepositoryMethodsNTest {
    @Autowired
    private SongsRepository songsRepo;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Value("${spring.datasource.url}")
    private String dbUrl;


    @BeforeEach
    void setUp() {
        log.info("dbUrl={}", dbUrl);
        songsRepo.deleteAll();
    }

    @Test
    void save_new() {
        //given an entity instance
        Song song = mapper.map(dtoFactory.make());
        assertThat(song.getId()).isZero();

        //when persisting
        songsRepo.save(song);
        //Hibernate: call next value for hibernate_sequence
        //Hibernate: insert into reposongs_song (artist, released, title, id) values (?, ?, ?, ?)

        //then entity is persisted
        then(song.getId()).isNotZero();
    }

    @Test
    void save_update() {
        //given an entity instance
        Song song = mapper.map(dtoFactory.make());
        songsRepo.save(song);
        songsRepo.flush();
        Song updatedSong = Song.builder()
                .id(song.getId())
                .title("new title")
                .artist(song.getArtist())
                .released(song.getReleased())
                .build();

        //when persisting update
        songsRepo.save(updatedSong);

        //then entity is persisted
        then(songsRepo.findOne(Example.of(updatedSong))).isPresent();
    }

    @Test
    @Transactional
    void exists() {
        //given a persisted entity instance
        Song pojoSong = mapper.map(dtoFactory.make());
        songsRepo.save(pojoSong);

        //when - determining if entity exists
        boolean exists = songsRepo.existsById(pojoSong.getId());
        //select count(*) as col_0_0_ from reposongs_song song0_ where song0_.id=?

        //then
        then(exists).isTrue();
    }

    @Test
    void findById_found() {
        //given a persisted entity instance
        Song pojoSong = mapper.map(dtoFactory.make());
        songsRepo.save(pojoSong);

        //when - finding the existing entity
        Optional<Song> result = songsRepo.findById(pojoSong.getId());
        //select ...
        //from reposongs_song song0_
        //where song0_.id=?

        //then
        then(result).isPresent();
        then(result.isPresent()).isTrue();

        //when - obtaining the instance
        Song dbSong = result.get();

        //then - database copy matches initial POJO
        then(dbSong).isNotNull();
        then(dbSong.getArtist()).isEqualTo(pojoSong.getArtist());
        then(dbSong.getTitle()).isEqualTo(pojoSong.getTitle());
        //the dbSong instance is coming from DB
        //comparing SQL Timestamp to java.util.Date

        then(pojoSong.getReleased()).isEqualTo(dbSong.getReleased());
    }

    @Test
    void findById_not_found() {
        //given - an ID that does not exist
        int missingId = 123456;

        //when - using find for a missing ID
        Optional<Song> result = songsRepo.findById(missingId);

        //then - the optional can be benignly tested
        then(result).isNotPresent();

        //then - the optional is asserted during the get()
        assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveAll_entities() {
        //given - several songs persisted
        Collection<Song> songs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());

        //when
        songsRepo.saveAll(songs);

        //then - each will exist in the DB
        songs.stream().forEach(s->{
            songsRepo.existsById(s.getId());
        });
    }


    @Test
    void findAll_entities() {
        //given - several songs persisted
        Collection<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        songsRepo.saveAll(pojoSongs);
        Map<Integer, Song> pojoSongsMap = pojoSongs.stream()
                .collect(Collectors.toMap(s->s.getId(), s->s));

        //when
        Iterable<Song> result = songsRepo.findAll();

        //then - we can find each instance
        then(result).hasSameSizeAs(pojoSongs);
        then(result).allMatch(s->pojoSongsMap.containsKey(s.getId()));
    }

    @Test
    //@Transactional - without this, the entity will get loaded
    void delete_exists() {
        //given - a persisted entity instance
        Song existingSong = mapper.map(dtoFactory.make());
        songsRepo.save(existingSong);

        //when - deleting an existing instance
        songsRepo.delete(existingSong);
        //select ... as title4_0_0_ from reposongs_song song0_ where song0_.id=?
        //delete from reposongs_song where id=?

        //then - instance will be removed from DB
        then(songsRepo.existsById(existingSong.getId())).isFalse();
    }

    @Test
    void delete_not_exists() {
        //given - a persisted entity instance
        Song doesNotExist = mapper.map(dtoFactory.make(oneUpId));
        then(songsRepo.existsById(doesNotExist.getId())).isFalse();

        //when - deleting a non-existing instance
        songsRepo.delete(doesNotExist);
        //select ... as title4_0_0_ from reposongs_song song0_ where song0_.id=?
        //no exception was thrown
    }

    @Test
    //@Transactional
    void deleteById_exists() {
        //given - a persisted entity instance
        Song existingSong = mapper.map(dtoFactory.make());
        songsRepo.save(existingSong);

        //when - deleting an existing instance
        songsRepo.deleteById(existingSong.getId());

        //then - instance will be removed from DB
        then(songsRepo.existsById(existingSong.getId())).isFalse();
    }

    @Test
    void deleteById_not_exists() {
        //given - an ID that does not exist
        int missingId = 123456;

        //when - deleting a non-existant instance
        Throwable ex= catchThrowable(()->{
            songsRepo.deleteById(missingId);
        });

        //then -- exception is thrown
        then(ex).isInstanceOf(EmptyResultDataAccessException.class);
        log.info("{}", ex.toString());
    }

    @Test
    void deleteAll_every() {
        //given
        Collection<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        songsRepo.saveAll(pojoSongs);

        //when
        songsRepo.deleteAll();

        //then
        then(pojoSongs).allSatisfy(s-> then(songsRepo.existsById(s.getId())).isFalse() );
    }

    @Test
    void deleteAll_some() {
        //given
        List<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        songsRepo.saveAll(pojoSongs);
        List<Song> toDelete = IntStream.range(0,2)
                .mapToObj(i->pojoSongs.get(i))
                .collect(Collectors.toList());

        //when - deleting a subset
        songsRepo.deleteAll(toDelete);

        //then
        then(songsRepo.existsById(pojoSongs.get(0).getId())).isFalse();
        then(songsRepo.existsById(pojoSongs.get(1).getId())).isFalse();
        then(songsRepo.existsById(pojoSongs.get(2).getId())).isTrue();
    }

    @Test
    void count() {
        //given
        List<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        songsRepo.saveAll(pojoSongs);

        //when
        long songCount = songsRepo.count();

        //then
        then(songCount).isEqualTo(pojoSongs.size());
    }

    @Test
    @Transactional
    void save_modify_existing() {
        //given - a persisted entity instance
        Song song = mapper.map(dtoFactory.make());
        songsRepo.save(song);
        String originalTitle = song.getTitle();
        String modifiedTitle = dtoFactory.title();
        assertThat(originalTitle).isNotEqualTo(modifiedTitle);

        //when - modifying song instance without saving
        song.setTitle(modifiedTitle);

        //then - DB is modified because we have a transaction active on @Test method
        Song dbSong = songsRepo.findById(song.getId()).get();
        then(dbSong.getTitle()).isEqualTo(modifiedTitle);
    }

    @Test
    void timestamp_date_compare() {
        java.util.Date utilDate = new Date();
        java.sql.Timestamp sqlTimestamp = new Timestamp(utilDate.getTime());

        //Timestamp is-a Date, but Date is-not-a Timestamp
        then(utilDate.equals(sqlTimestamp)).isTrue();
        then(sqlTimestamp.equals(utilDate)).isFalse();
        then(utilDate).isEqualTo(sqlTimestamp);
        then(sqlTimestamp).isNotEqualTo(utilDate);

        //Timestamp and Date represent same time
        then(sqlTimestamp).hasSameTimeAs(utilDate);
        then(utilDate).hasSameTimeAs(sqlTimestamp);
    }
}
