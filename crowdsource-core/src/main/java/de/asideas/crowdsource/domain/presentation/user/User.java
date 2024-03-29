package de.asideas.crowdsource.domain.presentation.user;

import de.asideas.crowdsource.domain.model.UserEntity;
import de.asideas.crowdsource.util.UserHelper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

// required for serialization
public class User {

    private String email;
    private List<String> roles;
    private int budget;
    private String name;

    public User(final UserEntity userEntity) {
        this.email = userEntity.getEmail();
        this.budget = userEntity.getBudget();
        this.roles = userEntity.getRoles();
        this.name = UserHelper.determineNameFromEmail(email);
    }

    public User() {
    }

    public String getEmail() {
        return this.email;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public int getBudget() {
        return this.budget;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
