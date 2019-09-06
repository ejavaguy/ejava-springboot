package info.ejava.examples.db.jpa.songs.svc;

import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SongsService {
    SongDTO createSong(SongDTO songDTO);
    SongDTO getSong(int id);
    SongDTO getRandomSong();
    void updateSong(int id, SongDTO songDTO);
    void deleteSong(int id);
    void deleteAllSongs();

    Page<SongDTO> findReleasedAfter(LocalDate exclusive, Pageable pageable);
    Page<SongDTO> findSongsMatchingAll(SongDTO probe, Pageable pageable);

}
