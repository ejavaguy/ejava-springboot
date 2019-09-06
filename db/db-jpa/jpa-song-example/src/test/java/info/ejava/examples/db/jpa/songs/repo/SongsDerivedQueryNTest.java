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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.nextDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class})
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@DisplayName("Repository Derived Query Methods")
@Slf4j
public class SongsDerivedQueryNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    private static List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        if (savedSongs.isEmpty()) {
            songsRepository.deleteAll();
            IntStream.range(0, 3).forEach(i -> {
                Song song = mapper.map(dtoFactory.make(nextDate));
                if (i == 2) {
                    song.setTitle(null);
                }
                songsRepository.save(song);
                savedSongs.add(song);
                //log.info("{}", song);
            });
        }
    }

    @Test
    void optional_exists() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();

        //when
        Optional<Song> result = songsRepository.getByTitle(song.getTitle());
        //select ...
        // from reposongs_song song0_
        // where song0_.title=?
        log.info("title with '{}' {} find instance", song.getTitle(), result.isPresent()?"did":"did not");

        //then
        then(result.isPresent()).isTrue();
        Song foundSong = result.get();
        then(foundSong.getId()).isEqualTo(song.getId());
    }

    @Test
    void optional_does_not_exist() {
        //given
        String nonExistantTitle = "1234567890";

        //when
        Optional<Song> result = songsRepository.getByTitle(nonExistantTitle);
        log.info("title with '{}' {} find instance", nonExistantTitle, result.isPresent()?"did":"did not");

        //then
        then(result.isPresent()).isFalse();
        assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);

        //when
        Song foundSong = result.orElse(null);

        //then
        then(foundSong).isNull();
    }

    private Map.Entry<String, Long> getStartsWith() {
        Map<String, Long> counts = savedSongs.stream()
                .filter(s -> s.getTitle() != null)
                .collect(Collectors.groupingBy(s -> s.getTitle().substring(0, 1), Collectors.counting()));
        long maxCount = counts.values().stream().mapToLong(v->v).max().orElse(0);
        Map.Entry<String, Long> startsWith = counts.entrySet().stream()
                .filter(e -> maxCount == e.getValue())
                .findFirst().orElse(null);
        assertThat(startsWith).isNotNull().describedAs("no song found");
        assertThat(startsWith.getKey()).isNotNull();
        assertThat(startsWith.getValue()).isNotZero();
        return startsWith;
    }

    @Test
    void findBy_list() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();
        long expectedCount = startsWith.getValue();

        //when
        Sort sort = Sort.by("id").ascending();
        List<Song> songs = songsRepository.findByTitleStartingWith(startingWith, sort);
        //select ...
        // from reposongs_song song0_
        // where song0_.title like ? escape ?
        // order by song0_.id asc

        //then
        then(songs.size()).isEqualTo(expectedCount);
    }

    @Test
    void findBy_pagable_slice() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();

        //when
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Slice<Song> songsSlice = songsRepository.findByTitleStartingWith(startingWith, pageable);
        //select ...
        // from reposongs_song song0_
        // where song0_.title like ? escape ?
        // order by song0_.id asc limit ?

        //then
        then(songsSlice.getNumberOfElements()).isEqualTo(pageable.getPageSize());
    }

    @Test
    void findBy_pagable_page() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();
        long expectedCount = startsWith.getValue();

        //when
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Page<Song> songsPage = songsRepository.findPageByTitleStartingWith(startingWith, pageable);
        //select song0_.id as id1_0_, song0_.artist as artist2_0_, song0_.released as released3_0_, song0_.title as title4_0_
        //  from reposongs_song song0_
        //  where song0_.title like ? escape ?
        //  order by song0_.id asc
        //  limit ?
        //select count(song0_.id) as col_0_0_
        //  from reposongs_song song0_
        //  where song0_.title like ? escape ?

        //then
        then(songsPage.getNumberOfElements()).isEqualTo(pageable.getPageSize());
        then(songsPage.getTotalElements()).isEqualTo(expectedCount);
    }

    @Test
    void findBy_property_value() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();

        //when
        List<Song> foundSongs = songsRepository.findByTitle(song.getTitle());
        log.info("title with '{}' found {}", song.getTitle(), foundSongs);

        //then
        then(foundSongs).hasSize(1);
        Song foundSong = foundSongs.get(0);
        then(foundSong.getId()).isEqualTo(song.getId());
    }

    @Test
    void findBy_property_not_value() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && !song.getTitle().equals(s.getTitle()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleNot(song.getTitle());
        log.info("title not '{}' found {}", song.getTitle(), foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_property_null_value() {
        //given - a song with a null title
        Song song = savedSongs.stream().filter(s->s.getTitle()==null).findFirst().get();

        //when - query will look for "is null"
        List<Song> foundSongs = songsRepository.findByTitle(song.getTitle());
        log.info("title with '{}' found {}", song.getTitle(), foundSongs);

        //then - song with null title is found
        Song foundSong = foundSongs.get(0);
        then(foundSong.getId()).isEqualTo(song.getId());
    }

    @Test
    void findBy_contains() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();
        String substring = song.getTitle().substring(1,5);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && s.getTitle().contains(substring))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleContaining(substring);
        log.info("title containing '{}' found {}", substring, foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_not_contains() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();
        String substring = song.getTitle().substring(1,5);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && !s.getTitle().contains(substring))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleNotContaining(substring);
        log.info("title not containing '{}' found {}", substring, foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }


    @Test
    void findBy_like() {
        //given
        String title = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get().getTitle();
        String likePattern = title.substring(0,1) + "%" + title.substring(title.length()-1,title.length());
        String regexPattern = likePattern.replaceAll("%",".*");
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && s.getTitle().matches(regexPattern))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleLike(likePattern);
        log.info("title like '{}' found {}", likePattern, foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_not_like() {
        //given
        String title = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get().getTitle();
        String likePattern = title.substring(0,1) + "%" + title.substring(title.length()-1,title.length());
        String regexPattern = likePattern.replaceAll("%",".*");
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && !s.getTitle().matches(regexPattern))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleNotLike(likePattern);
        log.info("title not like '{}' found {}", likePattern, foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findByAfter() {
        //given - a middle song
        Song firstSong = savedSongs.get(1);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getReleased().isAfter(firstSong.getReleased()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByReleasedAfter(firstSong.getReleased());
        log.info("released after '{}' found {}", firstSong.getReleased(), foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findByGreaterThanEqual() {
        //given - a middle song
        Song firstSong = savedSongs.get(1);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->!s.getReleased().isBefore(firstSong.getReleased()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByReleasedGreaterThanEqual(firstSong.getReleased());
        log.info("released GE '{}' found {}", firstSong.getReleased(), foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }


    @Test
    void findByBetween() {
        //given
        Song firstSong = savedSongs.get(0);
        Song lastSong = savedSongs.get(savedSongs.size()-1);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->!(s.getReleased().isBefore(firstSong.getReleased()) || s.getReleased().isAfter(lastSong.getReleased())))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByReleasedBetween(firstSong.getReleased(), lastSong.getReleased());
        log.info("released between '{}' and '{}' found {}", firstSong.getReleased(), lastSong.getReleased(), foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }


    @Test
    void findBy_multiple_predicates() {
        Song firstSong = savedSongs.get(0);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()==null)
                .filter(s->s.getReleased().isAfter(firstSong.getReleased()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleNullAndReleasedAfter(firstSong.getReleased());
        //select ...
        // from reposongs_song song0_
        // where (song0_.title is null) and song0_.released>?
        log.info("title null and released after '{}' found {}", firstSong.getReleased(), foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

}
