package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.User;

public interface UserService {

    void sendActivationMail(User user);
    String generateActivationToken();

}
