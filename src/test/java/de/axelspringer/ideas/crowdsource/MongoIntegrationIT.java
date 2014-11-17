package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.model.Hello;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@WebAppConfiguration
@Import(MongoDBConfig.class)
@SpringApplicationConfiguration(classes = CrowdSourceConfig.class)
public class MongoIntegrationIT {

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    public void shouldConnectToMongoDBAndFindOneTestObject() {

        Hello hello = new Hello();
        hello.setMessage("hi from mongo!");

        try {
            mongoOperations.findAllAndRemove(new Query(), Hello.class);
            mongoOperations.save(hello);
        } catch (DataAccessResourceFailureException e) {
            Assert.fail("MongoDB not available. Did you start and configure a valid Mongo instance?");
        }

        final List<Hello> hellos = mongoOperations.findAll(Hello.class);

        assertEquals("Test message not found in given MongoDB.", 1, hellos.size());
    }
}
