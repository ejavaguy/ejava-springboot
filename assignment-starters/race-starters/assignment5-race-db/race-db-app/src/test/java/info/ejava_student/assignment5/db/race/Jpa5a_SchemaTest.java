package info.ejava_student.assignment5.db.race;

import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={DbRaceApp.class})
@Slf4j
@ActiveProfiles(profiles={"test"}, resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles={"test", "postgres"})
@Disabled("TODO:enable")
public class Jpa5a_SchemaTest {
    @Autowired
    DataSource dataSource;
    String tableName = RacerRegistrationBO.TABLE_NAME;

    @Test
    void has_DataSource() throws SQLException {
        //given
        String url = dataSource.getConnection().getMetaData().getURL();
        log.info("dbUrl={}", url);
        //then
        then(url).startsWith("jdbc");
    }

    @Test
    void has_racer_registration_table() throws SQLException {
        int rows=0;
        //when
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("select count(*) from " + tableName)) {
            ResultSet rs = statement.executeQuery();
            rs.next();
            rows = rs.getInt(1);
            log.info("table[{}] exists with {} rows", tableName, rows);
            rs.close();
        }
    }
}
