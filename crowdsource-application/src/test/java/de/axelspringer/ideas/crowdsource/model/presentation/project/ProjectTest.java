package de.axelspringer.ideas.crowdsource.model.presentation.project;

import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
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
        projectEntity = new ProjectEntity(creator, project);

        user1 = new UserEntity("user1@xyz.com");
        user2 = new UserEntity("user2@xyz.com");
    }

    @Test
    public void testGetPledgedAmount() throws Exception {
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10)),
                new PledgeEntity(projectEntity, user2, new Pledge(20)),
                new PledgeEntity(projectEntity, user1, new Pledge(30)));

        Project project = new Project(projectEntity, pledges);

        assertThat(project.getPledgedAmount(), is(60));
    }

    @Test
    public void testGetBackers() throws Exception {
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10)),
                new PledgeEntity(projectEntity, user2, new Pledge(20)),
                new PledgeEntity(projectEntity, user1, new Pledge(30)));

        Project project = new Project(projectEntity, pledges);

        assertThat(project.getBackers(), is(2L));
    }
}