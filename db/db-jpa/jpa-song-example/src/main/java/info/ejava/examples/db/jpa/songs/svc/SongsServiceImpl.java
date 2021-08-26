package info.ejava.examples.db.jpa.songs.svc;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SongsServiceImpl implements SongsService {
    private final SongsMapper mapper;
    private final SongsRepository songsRepo;

    @Transactional(propagation = Propagation.REQUIRED)
    public SongDTO createSong(SongDTO songDTO) {
        Song songBO = mapper.map(songDTO);

        //manage instance
        songsRepo.save(songBO);

        return mapper.map(songBO);
    }

    @Override
    public Page<SongDTO> getSongs(Pageable pageable) {
        Page<Song> songs = songsRepo.findAll(pageable);
        return mapper.map(songs);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public SongDTO getSong(int id) {
        //this way leverages Optional features
        return songsRepo.findById(id)
                .map(songBO->mapper.map(songBO))
                .orElseThrow(()->new ClientErrorException.NotFoundException("Song id[%s] not found", id));
    }

    @Override
    public SongDTO getRandomSong() {
        //this way manually checks optional
        Optional<Song> songBO = songsRepo.random();
        if (!songBO.isPresent()) {
            throw new ClientErrorException.NotFoundException("No random song found");
        }
        return mapper.map(songBO.get());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSong(int id, SongDTO songDTO) {
        songDTO.setId(id);
        Song songBO=mapper.map(songDTO);

        songsRepo.save(songBO);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSong(int id) {
        songsRepo.deleteById(id);
    }

    @Override
    public Page<SongDTO> findReleasedAfter(LocalDate afterDate, Pageable pageable) {
        Page<Song> songs = songsRepo.findByReleasedAfter(afterDate, pageable);
        return mapper.map(songs);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAllSongs() {
        songsRepo.deleteAll();
    }



    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Page<SongDTO> findSongsMatchingAll(SongDTO probeDTO, Pageable pageable) {
        Song probe = mapper.map(probeDTO);
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnorePaths("id");
        Page<Song> songs = songsRepo.findAll(Example.of(probe, matcher), pageable);
        return mapper.map(songs);
    }
}
