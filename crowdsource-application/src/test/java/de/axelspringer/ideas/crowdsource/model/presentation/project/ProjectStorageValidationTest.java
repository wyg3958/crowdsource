package de.axelspringer.ideas.crowdsource.model.presentation.project;

import de.axelspringer.ideas.crowdsource.model.presentation.idea.ProjectStorage;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProjectStorageValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testAllFine() {
        ProjectStorage projectStorage = new ProjectStorage();
        projectStorage.setTitle("title");
        projectStorage.setShortDescription("shortDescription");
        projectStorage.setPledgeGoal(1);
        projectStorage.setDescription("description");

        assertThat(validator.validate(projectStorage).size(), is(0));
    }

    @Test
    public void testEverythingNull() {
        ProjectStorage projectStorage = new ProjectStorage();
        assertThat(validator.validate(projectStorage).size(), is(4));
    }

    @Test
    public void testEverythingEmpty() {
        ProjectStorage projectStorage = new ProjectStorage();
        projectStorage.setTitle("");
        projectStorage.setShortDescription("");
        projectStorage.setPledgeGoal(0);
        projectStorage.setDescription("");
        assertThat(validator.validate(projectStorage).size(), is(4));
    }

}
