package info.ejava.examples.db.mongo.books.bo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document(collection = "books")
@Builder
@With
@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@CompoundIndex(def="{'author':1, 'published':-1}", unique = true, background = false, sparse = false)
public class Book {
    @Id
    private String id;
    @Setter
    @Field(name="title")
    @Indexed(unique = true, sparse = true, direction = IndexDirection.ASCENDING, background = false)
    private String title;
    @Setter
    private String author;
    @Setter
    private LocalDate published;
}
