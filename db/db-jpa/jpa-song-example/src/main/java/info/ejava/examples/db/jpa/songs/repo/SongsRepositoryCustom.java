package info.ejava.examples.db.jpa.songs.repo;

import info.ejava.examples.db.jpa.songs.bo.Song;

import java.util.Optional;

public interface SongsRepositoryCustom {
    Optional<Song> random();
}
