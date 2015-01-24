package de.axelspringer.ideas.crowdsource.config;

public interface AppProfiles {

    String NOT = "!";
    String DEV = "dev";
    String CONS = "cons";
    // for PROD consider using ProductionCondition (which is a negation to DEV+CONS) to make sure it works even when forgetting to set profile prod
}
