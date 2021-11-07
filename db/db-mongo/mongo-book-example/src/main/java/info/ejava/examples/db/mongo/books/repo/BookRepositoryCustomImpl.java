package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.bo.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final MongoTemplate mongoTemplate;
    private final SecureRandom random = new SecureRandom();
    @Autowired @Lazy
    private BooksRepository booksRepository;

    protected List<Book> books(int offset, int limit) {
        return mongoTemplate.find(new Query().skip(offset).limit(limit), Book.class);
    }

    @Override
    public Optional<Book> random() {
        Optional randomBook = Optional.empty();
        int count = (int) booksRepository.count();

        if (count!=0) {
            int offset = random.nextInt(count);
            List<Book> books = books(offset, 1);
            randomBook = books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
        }
        return randomBook;
    }


    @Override
    public List<Book> findByAuthorGESize(int length) {
        String expression = String.format("^.{%d,}$", length);

        Aggregation pipeline = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author").regex(expression)),
                Aggregation.match(Criteria.where("author").exists(true))
        );
        AggregationResults<Book> result = mongoTemplate.aggregate(pipeline, "books", Book.class);

        return result.getMappedResults();
    }

    public List<String> findByTitleGESizeAsString(int length) {
        String expression = String.format("^.{%d,}$", length);

        MatchOperation matchStage = Aggregation.match(new Criteria("title").regex(expression));
        ProjectionOperation projectStage = Aggregation.project("title").andExclude("_id");
        Aggregation pipline = Aggregation.newAggregation(matchStage, projectStage);

        AggregationResults<Book> result =
                mongoTemplate.aggregate(pipline, "books", Book.class);
        return result.getMappedResults().stream()
                .map(b->b.getTitle())
                .collect(Collectors.toList());


//        AggregationResults<BasicDBObject> result =
//                mongoTemplate.aggregate(pipline, "books", BasicDBObject.class);
//        return result.getRawResults().getList("results", Document.class).stream()
//            .map(d->d.getString("title"))
//            .collect(Collectors.toList());
    }
}
