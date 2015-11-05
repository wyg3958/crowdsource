package de.asideas.crowdsource.model.persistence;

import de.asideas.crowdsource.model.presentation.Pledge;
import de.asideas.crowdsource.security.Roles;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

// needed for serialization
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String activationToken;

    private List<String> roles = Arrays.asList(Roles.ROLE_USER);

    private boolean activated = false;

    private int budget = 0;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public UserEntity(String email) {
        this(email, null);
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserEntity() {
    }

    public void accountPledge(Pledge pledge) {
        if ((budget - pledge.getAmount()) < 0) {
            throw new IllegalArgumentException("User budget may not drop below 0");
        }

        budget -= pledge.getAmount();
    }

    public String getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getActivationToken() {
        return this.activationToken;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public int getBudget() {
        return this.budget;
    }

    public DateTime getCreatedDate() {
        return this.createdDate;
    }

    public DateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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
