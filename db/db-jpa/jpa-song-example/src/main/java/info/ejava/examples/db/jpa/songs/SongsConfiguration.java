package info.ejava.examples.db.jpa.songs;

import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {SongsRepository.class}, repositoryImplementationPostfix = "Impl")
@EntityScan(basePackageClasses = {Song.class})
public class SongsConfiguration {
    @Bean
    public SongDTOFactory songsDtoFactory() {
        return new SongDTOFactory();
    }
}
