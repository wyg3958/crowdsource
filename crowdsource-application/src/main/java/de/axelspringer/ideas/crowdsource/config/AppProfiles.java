package de.axelspringer.ideas.crowdsource.config;

import de.axelspringer.ideas.crowdsource.config.security.MongoUserDetailsService;

public interface AppProfiles {

    String NOT = "!";

    /**
     * Will disable enforcement of https
     */
    String ALLOW_HTTP = "ALLOW_HTTP";

    /**
     * Will create default users ({@link MongoUserDetailsService#DEFAULT_USER_EMAIL}, {@link MongoUserDetailsService#DEFAULT_ADMIN_EMAIL}) on application startup
     */
    String CREATE_USERS = "CREATE_USERS";
}
