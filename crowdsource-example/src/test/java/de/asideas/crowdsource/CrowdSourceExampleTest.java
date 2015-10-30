package de.asideas.crowdsource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CrowdSourceExampleTest {

    @Test
    public void testExtractMongoDbName() throws Exception {
        assertEquals("heroku_zjfpkkgn", CrowdSourceExample.extractMongoDbName("heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn"));
    }

    @Test
    public void testExtractMongoPort() throws Exception {
        assertEquals("27491", CrowdSourceExample.extractMongoPort("heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn"));
    }

    @Test
    public void testExtractMongoHost() throws Exception {
        assertEquals("ds027491.mongolab.com", CrowdSourceExample.extractMongoHost("heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn"));
    }

    @Test
    public void testExtractMongoPass() throws Exception {
        assertEquals("nga79pk8lffffffku2p62mkbo", CrowdSourceExample.extractMongoPass("heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn"));
    }

    @Test
    public void testExtractMongoUser() throws Exception {
        assertEquals("heroku_zjfpkkgn", CrowdSourceExample.extractMongoUser("heroku_zjfpkkgn:nga79pk8lffffffku2p62mkbo@ds027491.mongolab.com:27491/heroku_zjfpkkgn"));
    }
}