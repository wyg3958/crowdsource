package de.axelspringer.ideas.crowdsource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.subethamail.wiser.Wiser;

@SpringBootApplication
@Import(CrowdSource.class)
public class CrowdSourceExample {

    @Value("${de.axelspringer.ideas.crowdsource.mail.port:1025}")
    private Integer mailServerPort;

    public static void main(String[] args) {

        extractHerokuMongoLabsArgs();
        SpringApplication.run(CrowdSourceExample.class, args);
    }

    private static void extractHerokuMongoLabsArgs() {

        final String mongolab_uri = System.getenv("MONGOLAB_URI").replace("mongodb://", "");
        if (!StringUtils.isEmpty(mongolab_uri)) {
            System.setProperty("de.axelspringer.ideas.crowdsource.db.username", extractMongoUser(mongolab_uri));
            System.setProperty("de.axelspringer.ideas.crowdsource.db.password", extractMongoPass(mongolab_uri));
            System.setProperty("de.axelspringer.ideas.crowdsource.db.hosts", extractMongoHost(mongolab_uri));
            System.setProperty("de.axelspringer.ideas.crowdsource.db.port", extractMongoPort(mongolab_uri));
            System.setProperty("de.axelspringer.ideas.crowdsource.db.name", extractMongoDbName(mongolab_uri));
        }
    }

    static String extractMongoDbName(String mongolab_uri) {

        // heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn
        return mongolab_uri.substring(mongolab_uri.lastIndexOf("/") + 1);
    }

    static String extractMongoPort(String mongolab_uri) {

        // heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn
        return mongolab_uri.substring(mongolab_uri.lastIndexOf(":") + 1, mongolab_uri.lastIndexOf("/"));
    }

    static String extractMongoHost(String mongolab_uri) {

        // heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn
        return mongolab_uri.substring(mongolab_uri.indexOf("@") + 1, mongolab_uri.lastIndexOf(":"));
    }

    static String extractMongoPass(String mongolab_uri) {

        // heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn
        return mongolab_uri.substring(mongolab_uri.indexOf(":") + 1, mongolab_uri.indexOf("@"));
    }

    static String extractMongoUser(String mongolab_uri) {

        // heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn
        return mongolab_uri.substring(0, mongolab_uri.indexOf(":"));
    }

    @Bean
    public Wiser mailServer() {

        Wiser wiser = new Wiser(mailServerPort);
        wiser.start();
        return wiser;
    }
}
