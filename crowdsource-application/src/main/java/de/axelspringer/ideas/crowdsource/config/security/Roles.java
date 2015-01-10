package de.axelspringer.ideas.crowdsource.config.security;

public interface Roles {

    /**
     * Anonymous user. An user that is neither logged in nor in a trusted network.
     */
    String ROLE_UNTRUSTED_ANONYMOUS = "ROLE_UNTRUSTED_ANONYMOUS";

    /**
     * User that is not logged in but in a trusted network.
     */
    String ROLE_TRUSTED_ANONYMOUS = "ROLE_TRUSTED_ANONYMOUS";

    /**
     * User that is logged in.
     */
    String ROLE_USER = "ROLE_USER";

    /**
     * Admin
     */
    String ROLE_ADMIN = "ROLE_ADMIN";
}
