package info.ejava.examples.db.jpa.songs.repo;

import info.ejava.examples.db.jpa.songs.bo.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SongsRepository extends JpaRepository<Song, Integer>, SongsRepositoryCustom {
    Optional<Song> getByTitle(String title);

    List<Song> findByTitle(String title);
    List<Song> findByTitleNot(String title);
    List<Song> findByTitleContaining(String string);
    List<Song> findByTitleNotContaining(String title);
    List<Song> findByTitleLike(String string);
    List<Song> findByTitleNotLike(String title);

    List<Song> findByReleasedAfter(LocalDate date);
    List<Song> findByReleasedGreaterThanEqual(LocalDate date);
    List<Song> findByReleasedBetween(LocalDate starting, LocalDate ending);

    Page<Song> findByReleasedAfter(LocalDate date, Pageable pageable);

    List<Song> findByTitleNullAndReleasedAfter(LocalDate date);
    Slice<Song> findByTitleNullAndReleasedAfter(LocalDate date, Pageable pageable);
    Page<Song> findPageByTitleNullAndReleasedAfter(LocalDate date, Pageable pageable);

    @Query(value = "select s.title from REPOSONGS_SONG s where length(s.title) >= :length", nativeQuery = true)
    List<String> getTitlesGESizeNative(@Param("length") int length);

    @Query("select s from Song s where length(s.title) >= :length")
    List<Song> findByTitleGESize(@Param("length") int length);

    //see @NamedQuery(name="Song.findByArtistGESize" in Song class
    List<Song> findByArtistGESize(@Param("length") int length);

    List<Song> findByTitleStartingWith(String string, Sort sort);
    Slice<Song> findByTitleStartingWith(String string, Pageable pageable);
    Page<Song> findPageByTitleStartingWith(String string, Pageable pageable);

}
