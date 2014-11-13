package de.axelspringer.ideas.crowdsource.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoDBConfig extends AbstractMongoConfiguration {

    @Value("${de.axelspringer.ideas.crowdsource.db.host:localhost}")
    private String DB_HOST;

    @Value("${de.axelspringer.ideas.crowdsource.db.port:27017}")
    private int DB_PORT;

    @Value("${de.axelspringer.ideas.crowdsource.db.name:crowdsource}")
    private String DB_NAME;

    @Override
    protected String getDatabaseName() {
        return DB_NAME;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(DB_HOST, DB_PORT);
    }
}
