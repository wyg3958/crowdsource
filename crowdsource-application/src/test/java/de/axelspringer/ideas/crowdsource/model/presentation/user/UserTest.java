package de.axelspringer.ideas.crowdsource.model.presentation.user;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserTest {

    @Test
    public void testDetermineNameFromEmail() throws Exception {
        User user = new User(new UserEntity("foo.bar@domain.com"));

        assertThat(user.getName(), is("Foo Bar"));
    }

    @Test
    public void testDetermineNameFromEmail_singleName() throws Exception {
        User user = new User(new UserEntity("foo@domain.com"));

        assertThat(user.getName(), is("Foo"));
    }

    @Test
    public void testDetermineNameFromEmail_noEmail() throws Exception {
        User user = new User(new UserEntity());

        assertThat(user.getName(), is(nullValue()));
    }

    @Test
    public void testDetermineNameFromEmail_invalidEmail() throws Exception {
        User user = new User(new UserEntity("invalid-email"));

        assertThat(user.getName(), is(nullValue()));
    }

    @Test
    public void testDetermineNameFromEmail_noLocalPart() throws Exception {
        User user = new User(new UserEntity("@domain.com"));

        assertThat(user.getName(), is(nullValue()));
    }
}