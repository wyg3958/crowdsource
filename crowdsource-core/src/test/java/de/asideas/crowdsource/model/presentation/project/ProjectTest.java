package de.asideas.crowdsource.model.presentation.project;

import de.asideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.asideas.crowdsource.model.persistence.PledgeEntity;
import de.asideas.crowdsource.model.persistence.ProjectEntity;
import de.asideas.crowdsource.model.persistence.UserEntity;
import de.asideas.crowdsource.model.presentation.Pledge;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProjectTest {

    private List<PledgeEntity> pledges;
    private ProjectEntity projectEntity;
    private UserEntity user1;
    private UserEntity user2;

    @Before
    public void setUp() {
        UserEntity creator = new UserEntity();
        creator.setId("id");

        Project project = new Project();
        projectEntity = new ProjectEntity(creator, project, new FinancingRoundEntity());

        user1 = new UserEntity("user1@xyz.com");
        user2 = new UserEntity("user2@xyz.com");
    }

    @Test
    public void testGetPledgedAmount() throws Exception {
        final FinancingRoundEntity activeFinancingRoundEntity = new FinancingRoundEntity();
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user2, new Pledge(20), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user1, new Pledge(30), activeFinancingRoundEntity));

        Project project = new Project(projectEntity, pledges);

        assertThat(project.getPledgedAmount(), is(60));
    }

    @Test
    public void testGetBackers() throws Exception {
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), new FinancingRoundEntity()),
                new PledgeEntity(projectEntity, user2, new Pledge(20), new FinancingRoundEntity()),
                new PledgeEntity(projectEntity, user1, new Pledge(30), new FinancingRoundEntity()));

        Project project = new Project(projectEntity, pledges);

        assertThat(project.getBackers(), is(2L));
    }
}