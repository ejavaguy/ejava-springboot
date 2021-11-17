package info.ejava.examples.db.mongo.books;

import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.dto.BookDTO;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.repo.BooksRepository;
import info.ejava.examples.db.mongo.books.svc.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
//@org.springframework.data.mongodb.repository.config.EnableMongoRepositories
//@EnableMongoRepositories(basePackageClasses = {BooksRepository.class})
@EnableMongoRepositories(basePackageClasses = {BooksRepository.class}, repositoryImplementationPostfix = "Impl")
@Slf4j
public class MongoDBBooksApp {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MongoDBBooksApp.class);
        app.setDefaultProperties(Collections.singletonMap("spring.profiles.default", "mongodb"));
        app.run(args);
    }

    @Component
    @ConditionalOnProperty(prefix = "db", name = "populate", havingValue = "true", matchIfMissing = true)
    public class Init implements CommandLineRunner {
        @Autowired
        private BooksRepository booksRepository;
        @Autowired
        private MongoTemplate mongoTemplate;
        @Autowired
        private BookDTOFactory dtoFactory;
        @Autowired
        private BooksMapper mapper;

        @Override
        public void run(String... args) throws Exception {
            List<BookDTO> dtos = dtoFactory.listBuilder().books(100,100);
            List<Book> songBOs = dtos.stream().map(dto->mapper.map(dto)).collect(Collectors.toList());
            booksRepository.saveAll(songBOs);

            long count = mongoTemplate.count(new Query(), Book.class);
            log.info("we have {} books", count);
        }
    }
}
