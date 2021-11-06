package info.ejava.examples.db.jpa.songs;

import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
//@org.springframework.data.jpa.repository.config.EnableJpaRepositories
//@EnableJpaRepositories(basePackageClasses = {SongsRepository.class}, repositoryImplementationPostfix = "Impl")
//@EnableJpaRepositories(basePackageClasses = {SongsRepository.class})
//@EntityScan(value={"info.ejava.examples.db.repo.jpa.songs.bo"})
//@EntityScan(basePackageClasses = {Song.class})
@Slf4j
public class JPASongsApp {
    public static void main(String[] args) {
        SpringApplication.run(JPASongsApp.class, args);
    }

    @Component
    @ConditionalOnProperty(prefix = "db", name = "populate", havingValue = "true", matchIfMissing = true)
    public class Init implements CommandLineRunner {
        @Autowired
        private SongsRepository songsRepo;
        @Autowired
        private DataSource ds;
        @Autowired
        private EntityManagerFactory emf;
        @Autowired
        private EntityManager em;
        @Autowired
        private SongDTOFactory dtoFactory;
        @Autowired
        private SongsMapper mapper;

        @Override
        public void run(String... args) throws Exception {
            log.info("dbUrl={}", ds.getConnection().getMetaData().getURL());
            log.info("persistence unit={}", emf);
            log.info("persistence context={}", em);

            List<SongDTO> dtos = dtoFactory.listBuilder().songs(100,100);
            List<Song> songBOs = dtos.stream().map(dto->mapper.map(dto)).collect(Collectors.toList());
            songsRepo.saveAll(songBOs);

            int count=em.createQuery("select count(s) from Song s", Number.class).getSingleResult().intValue();
            log.info("we have {} songs", count);
        }
    }
}
