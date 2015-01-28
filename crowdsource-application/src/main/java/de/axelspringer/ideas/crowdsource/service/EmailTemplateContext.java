package de.axelspringer.ideas.crowdsource.service;

import lombok.Data;

@Data
public class EmailTemplateContext {
    private String email;
    private String userName;
    private String faq;
    private String link;
}
