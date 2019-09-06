package info.ejava.examples.db.mongo.books.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import info.ejava.examples.db.mongo.books.bo.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MongoOpsBookDAO {
    private final MongoTemplate mongoTemplate;
    private final SecureRandom random = new SecureRandom();

    public Book create(Book book) {
        //return mongoTemplate.save(book);
        return mongoTemplate.insert(book);
    }

    public boolean existsById(String id) {
        //Criteria filter = Criteria.where("field1").is("value1").and("field2").not().is("value2");
        Query filter = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.exists(filter,Book.class);
    }

    public Book findById(String id) {
        return mongoTemplate.findById(id, Book.class);
    }

    public Book update(Book book) {
        return mongoTemplate.save(book);
    }

    public long updateTitle(String id, String title) {
        //when using Book.class
        // Query filter = Query.query(Criteria.where("id").is(id));
        //when using "books" collection name
        Query filter = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update();
        update.set("title", title);
        //UpdateResult result = mongoTemplate.upsert(filter, update, "books");
        UpdateResult result = mongoTemplate.updateFirst(filter, update, Book.class);
        return result.getMatchedCount();
    }

    public long delete(Book book) {
        DeleteResult result = mongoTemplate.remove(book);
        return result.getDeletedCount();
    }

    public long deleteById(String id) {
        Query filter = Query.query(Criteria.where("id").is(id));
        DeleteResult result = mongoTemplate.remove(filter, Book.class);
        return result.getDeletedCount();
    }

    public long deleteAll() {
        return mongoTemplate.remove(new Query(), Book.class).getDeletedCount();
    }

    public long count() {
        return mongoTemplate.count(new Query(), "books");
    }

    protected List<Book> books(int offset, int limit) {
        return mongoTemplate.find(new Query().skip(offset).limit(limit), Book.class);
    }
    protected Book book(int offset) {
        return mongoTemplate.findOne(new Query().skip(offset), Book.class);
    }

    public Optional<Book> random() {
        Optional randomSong = Optional.empty();
        long count = count();

        if (count!=0) {
            int offset = random.nextInt((int)count);
            Book book = book(offset);
            randomSong = book==null ? Optional.empty() : Optional.of(book);
        }
        return randomSong;
    }


    public List<String> findByTitleGESizeAsString(int length) {
        String expression = String.format("^.{%d,}$", length);

        Query query = Query.query(Criteria.where("title").regex(expression));
        query.fields().include("title").exclude("id");

        return mongoTemplate.find(query, Book.class, "books").stream()
                .map(b->b.getTitle())
                .collect(Collectors.toList());
    }


    public List<Book> findByAuthorGESize(int length) {
        String expression = String.format("^.{%d,}$", length);

        Aggregation pipeline = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author").regex(expression)),
                Aggregation.match(Criteria.where("author").exists(true))
        );
        AggregationResults<Book> result = mongoTemplate.aggregate(pipeline, "books", Book.class);

        return result.getMappedResults();
    }

    public List<Book> find(List<String> order, int offset, int limit) {
        Query query = new Query();
        query.with( Sort.by(order.toArray(new String[0])));
        query.skip(offset);
        query.limit(limit);
        return mongoTemplate.find(query, Book.class);
    }

    public List<Book> find(Pageable pageable) {
        return mongoTemplate.find(new Query().with(pageable), Book.class);
    }
}
