package info.ejava_student.assignment5.db.race;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.management.Query;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes={DbRaceApp.class})
@ActiveProfiles(profiles = "test", resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles = {"test", "mongodb"}, resolver = TestProfileResolver.class)
@Disabled("TODO: uncomment and declare dependencies")
public class Mongo5a_ClientTest {
/*
    @Autowired
    private MongoOperations mongoOperations;
*/

    @Test
    void has_mongo_template_injected() {
/*
        long count = mongoOperations.count(new Query(), "some_collection");
        then(count).isEqualTo(0);
*/
        fail("uncomment test");
    }
}
