package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.model.Hello;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoIntegrationIT.MongoITConfig.class)
public class MongoIntegrationIT {

    @Autowired
    private MongoOperations mongoOperations;

    @Before
    public void initDB() {
        // try to remove all Hellos first (which is also a connection check)
        try {
            mongoOperations.findAllAndRemove(null, Hello.class);
        } catch (DataAccessResourceFailureException e) {
            Assert.fail("MongoDB not available. Did you start and configure a valid Mongo instance?");
        }
    }

    @Test
    public void shouldConnectToMongoDBAndFindOneTestObject() {

        // new hello instance
        Hello hello = new Hello();
        hello.setMessage("hi from mongo!");

        // save to db
        mongoOperations.save(hello);

        // query db
        final List<Hello> hellos = mongoOperations.findAll(Hello.class);

        // assert hello instance is found
        assertEquals("Test message count != 1.", 1, hellos.size());
    }

    @Import({CrowdSourceTestConfig.class, MongoDBConfig.class})
    @Configuration
    static class MongoITConfig {
    }
}
