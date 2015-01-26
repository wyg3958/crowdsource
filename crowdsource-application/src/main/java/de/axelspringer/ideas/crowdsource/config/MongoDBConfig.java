package de.axelspringer.ideas.crowdsource.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.net.UnknownHostException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Configuration
@EnableMongoAuditing
public class MongoDBConfig extends AbstractMongoConfiguration {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("#{'${de.axelspringer.ideas.crowdsource.db.hosts:172.31.21.81,172.31.0.106,172.31.46.89}'.split(',')}")
    private List<String> hosts;

    @Value("${de.axelspringer.ideas.crowdsource.db.port:27017}")
    private int port;

    @Value("${de.axelspringer.ideas.crowdsource.db.name:crowdsource}")
    private String databaseName;

    @Value("${de.axelspringer.ideas.crowdsource.db.username:crowdsource}")
    private String username;

    @Value("${de.axelspringer.ideas.crowdsource.db.password:sRBfksXzltBgYHxvNUMoSKVHNLsIHHlI1B5Np2S8oyE=}")
    private String password;


    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Mongo mongo() throws Exception {

        List<ServerAddress> serverAddresses = hosts.stream()
                .map(this::createServerAddress)
                .collect(toList());

        log.debug("connecting to db hosts: {}...", serverAddresses);

        if (serverAddresses.size() == 1) {
            // create a mongo client that connects to a single database,
            // this is NOT the same as calling the constructor with a list of ServerAddresses with only one element!
            return new MongoClient(serverAddresses.get(0));
        }
        else {
            // create a mongo client that connects to a replicaset
            MongoClientOptions options = MongoClientOptions.builder()
                    .writeConcern(WriteConcern.REPLICA_ACKNOWLEDGED)
                    .build();
            return new MongoClient(serverAddresses, options);
        }
    }

    @Override
    protected UserCredentials getUserCredentials() {
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        return new UserCredentials(username, password);
    }

    private ServerAddress createServerAddress(String host) {
        try {
            return new ServerAddress(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
