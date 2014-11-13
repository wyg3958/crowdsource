package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.model.Hello;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@WebAppConfiguration
@ContextConfiguration(classes = CrowdSourceConfig.class)
public class MongoIntegrationIT {

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    public void testHello() {

        Hello hello = new Hello();
        hello.setMessage("hi from mongo!");

        mongoOperations.save(hello);

        final List<Hello> hellos = mongoOperations.findAll(Hello.class);

        assertEquals(1, hellos.size());
    }
}
