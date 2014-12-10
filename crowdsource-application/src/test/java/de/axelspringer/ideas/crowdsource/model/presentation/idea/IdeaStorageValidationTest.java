package de.axelspringer.ideas.crowdsource.model.presentation.idea;

import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IdeaStorageValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testAllFine() {
        IdeaStorage ideaStorage = new IdeaStorage();
        ideaStorage.setTitle("title");
        ideaStorage.setShortDescription("shortDescription");
        ideaStorage.setCurrentFunding(1);
        ideaStorage.setFullDescription("description");

        assertThat(validator.validate(ideaStorage).size(), is(0));
    }

    @Test
    public void testEverythingNull() {
        IdeaStorage ideaStorage = new IdeaStorage();
        assertThat(validator.validate(ideaStorage).size(), is(4));
    }

    @Test
    public void testEverythingEmpty() {
        IdeaStorage ideaStorage = new IdeaStorage();
        ideaStorage.setTitle("");
        ideaStorage.setShortDescription("");
        ideaStorage.setCurrentFunding(0);
        ideaStorage.setFullDescription("");
        assertThat(validator.validate(ideaStorage).size(), is(4));
    }

}
