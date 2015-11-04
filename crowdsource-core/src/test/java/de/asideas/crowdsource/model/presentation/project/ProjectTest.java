package de.asideas.crowdsource.model.presentation.project;

import de.asideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.asideas.crowdsource.model.persistence.PledgeEntity;
import de.asideas.crowdsource.model.persistence.ProjectEntity;
import de.asideas.crowdsource.model.persistence.UserEntity;
import de.asideas.crowdsource.model.presentation.Pledge;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
        user1.setId("test_id1");
        user2 = new UserEntity("user2@xyz.com");
        user2.setId("test_id2");
    }

    @Test
    public void getPledgedAmount() throws Exception {
        final FinancingRoundEntity activeFinancingRoundEntity = new FinancingRoundEntity();
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user2, new Pledge(20), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user1, new Pledge(30), activeFinancingRoundEntity));

        Project project = new Project(projectEntity, pledges, user1);

        assertThat(project.getPledgedAmount(), is(60));
    }
    @Test
    public void getPledgedAmount_isZeroOnEmptyPledges() throws Exception {
        Project project = new Project(projectEntity, new ArrayList<>(), user1);

        assertThat(project.getPledgedAmount(), is(0));
    }

    @Test
    public void getBackers() throws Exception {
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), new FinancingRoundEntity()),
                new PledgeEntity(projectEntity, user2, new Pledge(20), new FinancingRoundEntity()),
                new PledgeEntity(projectEntity, user1, new Pledge(30), new FinancingRoundEntity()));

        Project project = new Project(projectEntity, pledges, user2);

        assertThat(project.getBackers(), is(2L));
    }
    @Test
    public void getPledgedAmountByRequestingUser() throws Exception {
        final FinancingRoundEntity activeFinancingRoundEntity = new FinancingRoundEntity();
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user2, new Pledge(20), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user1, new Pledge(30), activeFinancingRoundEntity));

        Project project = new Project(projectEntity, pledges, user1);

        assertThat(project.getPledgedAmountByRequestingUser(), is(40));
    }
    @Test
    public void getPledgedAmountByRequestingUser_ReturnsZeroOnNullUser() throws Exception {
        final FinancingRoundEntity activeFinancingRoundEntity = new FinancingRoundEntity();
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user2, new Pledge(20), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user1, new Pledge(30), activeFinancingRoundEntity));

        Project project = new Project(projectEntity, pledges, null);

        assertThat(project.getPledgedAmountByRequestingUser(), is(0));
    }
    @Test
    public void getPledgedAmountByRequestingUser_ReturnsZeroOnEmptyPledges() throws Exception {
        final FinancingRoundEntity activeFinancingRoundEntity = new FinancingRoundEntity();
        pledges = Arrays.asList(
                new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user2, new Pledge(20), activeFinancingRoundEntity),
                new PledgeEntity(projectEntity, user1, new Pledge(30), activeFinancingRoundEntity));

        Project project = new Project(projectEntity, new ArrayList<>(), user1);
        assertThat(project.getPledgedAmountByRequestingUser(), is(0));
    }

}