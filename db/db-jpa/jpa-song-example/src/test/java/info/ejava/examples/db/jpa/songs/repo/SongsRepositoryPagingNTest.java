package info.ejava.examples.db.jpa.songs.repo;

import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
@DisplayName("Repository Sorting/Paging")
@Slf4j
public class SongsRepositoryPagingNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    private String titlePrefix = "123";
    private UnaryOperator<SongDTO> addTitlePrefix = s->{
        s.setTitle(titlePrefix + s.getTitle());
        return s;
    };
    private List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        songsRepository.deleteAll();
        IntStream.range(0,10).forEach(i->{
            Song song = mapper.map(dtoFactory.make(nextDate, addTitlePrefix));
            savedSongs.add(song);
        });
        songsRepository.saveAll(savedSongs);
    }

    @Test
    void findAll_sorted() {
        //when
        List<Song> byReleased = songsRepository.findAll(
                Sort.by("released").descending().and(Sort.by("id").ascending()));
        //select ... from reposongs_song song0_ order by song0_.released desc, song0_.id asc
        //log.info("ordered by released date DSC found {}", byReleased);

        //then
        LocalDate previous = null;
        for (Song s: byReleased) {
            if (previous!=null) {
                then(previous).isAfterOrEqualTo(s.getReleased()); //DESC order
            }
            previous=s.getReleased();
        }
    }

    @Test
    void findAll_sorted_and_paged() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("released"));
        //Pageable next = pageable.next();
        //Pageable previous = pageable.previousOrFirst();
        //Pageable first = pageable.first();

        //when
        Page<Song> songPage = songsRepository.findAll(pageable);

        //then
        Slice songSlice = songPage;
        then(songSlice).isNotNull();
        then(songSlice.isEmpty()).isFalse();
        then(songSlice.getNumber()).isEqualTo(0);
        then(songSlice.getSize()).isEqualTo(pageSize);
        then(songSlice.getNumberOfElements()).isEqualTo(pageSize);

        then(songPage.getTotalElements()).isEqualTo(savedSongs.size()); //unique to Page

        List<Song> songsList = songSlice.getContent();
        then(songsList).hasSize(pageSize);

        for (int i=1; songSlice.hasNext(); i++) {
            pageable = pageable.next();
            songSlice = songsRepository.findAll(pageable);
            songsList = songSlice.getContent();
            then(songSlice).isNotNull();
            then(songSlice.getNumber()).isEqualTo(i);
            then(songSlice.getSize()).isLessThanOrEqualTo(pageSize);
            then(songSlice.getNumberOfElements()).isLessThanOrEqualTo(pageSize);
            then(((Page)songSlice).getTotalElements()).isEqualTo(savedSongs.size()); //unique to Page
        }
        then(songSlice.hasNext()).isFalse();
        then(songSlice.getNumber()).isEqualTo(songsRepository.count() / pageSize);
    }

    @Test
    void sorting() {
        //when
        List<Integer> dbIdsByTitleASC = songsRepository.findByTitleStartingWith(titlePrefix, Sort.by("released").ascending())
                .stream()
                .map(s->s.getId())
                .collect(Collectors.toList());
        log.info("ordered by released date ASC found {}", dbIdsByTitleASC);

        //then
        List<Integer> idByTitleASC = savedSongs.stream()
                .sorted(Comparator.comparing(Song::getReleased))
                .map(s->s.getId())
                .collect(Collectors.toList());
        then(dbIdsByTitleASC).isEqualTo(idByTitleASC);

        //when
        List<Integer> dbIdsByTitleDSC = songsRepository.findByTitleStartingWith(titlePrefix, Sort.by("released").descending())
                .stream()
                .map(s->s.getId())
                .collect(Collectors.toList());
        log.info("ordered by released date DSC found {}", dbIdsByTitleDSC);

        //then
        List<Integer> idByTitleDSC = savedSongs.stream()
                .sorted(Comparator.comparing(Song::getReleased, Comparator.reverseOrder()))
                .map(s->s.getId())
                .collect(Collectors.toList());
        then(dbIdsByTitleDSC).isEqualTo(idByTitleDSC);
    }


    @Test
    void paging_slice() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("released"));
        Set<Integer> ids = savedSongs.stream().map(s -> s.getId()).collect(Collectors.toSet());

        //when
        Slice<Song> songPage = songsRepository.findByTitleStartingWith(titlePrefix, pageable);

        //then
        then(songPage).isNotNull();
        then(songPage.isEmpty()).isFalse();
        then(songPage.getNumber()).isEqualTo(0);
        then(songPage.getSize()).isEqualTo(pageSize);
        then(songPage.getNumberOfElements()).isEqualTo(pageSize);

        List<Song> songsList = songPage.getContent();
        then(songsList.size()).isEqualTo(pageSize);
        then(songsList).allMatch(s->ids.remove(s.getId()));

        for (int i=1; songPage.hasNext(); i++) {
            pageable = pageable.next();
            songPage = songsRepository.findByTitleStartingWith(titlePrefix, pageable);
            songsList = songPage.getContent();
            then(songPage).isNotNull();
            then(songPage.getNumber()).isEqualTo(i);
            then(songPage.getSize()).isLessThanOrEqualTo(pageSize);
            then(songPage.getNumberOfElements()).isLessThanOrEqualTo(pageSize);
            then(songsList).allMatch(s->ids.remove(s.getId()));
        }
        then(songPage.hasNext()).isFalse();
        then(songPage.getNumber()).isEqualTo(songsRepository.count() / pageSize);
        then(ids).isEmpty();
    }

    @Test
    void paging_pageable() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("released"));
        Set<Integer> ids = savedSongs.stream().map(s -> s.getId()).collect(Collectors.toSet());

        //when
        Page<Song> songPage = songsRepository.findPageByTitleStartingWith(titlePrefix, pageable);

        //then
        then(songPage).isNotNull();
        then(songPage.isEmpty()).isFalse();
        then(songPage.getNumber()).isEqualTo(0);
        then(songPage.getSize()).isEqualTo(pageSize);
        then(songPage.getNumberOfElements()).isEqualTo(pageSize);
        then(songPage.hasNext()).isTrue();

        then(songPage.getTotalElements()).isEqualTo(savedSongs.size());
        then(songPage.getTotalPages()).isEqualTo(savedSongs.size() / pageSize + (savedSongs.size() % pageSize==0?0:1));
    }
}
