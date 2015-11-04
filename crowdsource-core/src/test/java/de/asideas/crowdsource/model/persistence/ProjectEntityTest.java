package de.asideas.crowdsource.model.persistence;

import de.asideas.crowdsource.enums.ProjectStatus;
import de.asideas.crowdsource.model.presentation.Pledge;
import de.asideas.crowdsource.model.presentation.project.Project;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProjectEntityTest {

    private static final int PLEDGE_GOAL = 200;

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
        projectEntity.setPledgeGoal(PLEDGE_GOAL);

        user1 = new UserEntity("user1@xyz.com");
        user1.setId("test_id1");
        user1.setBudget(100);
        user2 = new UserEntity("user2@xyz.com");
        user2.setId("test_id2");
    }

    /**
     * For a full integration test, checking all edge cases see {@link de.asideas.crowdsource.service.ProjectServiceTest}
     * @throws Exception
     */
    @Test
    public void pledge() throws Exception {
        final FinancingRoundEntity activeFinancingRound = new FinancingRoundEntity();
        final List<PledgeEntity> pledgesDoneBeforw = preparePledgesDone(activeFinancingRound);
        final Pledge pledge = new Pledge(40);
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        int userBudgetBefore = user1.getBudget();

        final PledgeEntity pledgeRes = projectEntity.pledge(pledge, activeFinancingRound, user1, pledgesDoneBeforw);

        assertThat(pledgeRes, is(new PledgeEntity(projectEntity, user1, pledge, activeFinancingRound)));
        assertThat(user1.getBudget(), is(userBudgetBefore - pledge.getAmount()));
        assertThat(projectEntity.pledgeGoalAchieved(), is(true));
    }

    @Test
    public void pledge_reverse() throws Exception {
        final FinancingRoundEntity activeFinancingRound = new FinancingRoundEntity();
        final List<PledgeEntity> pledgesDoneBeforw = preparePledgesDone(activeFinancingRound);
        final Pledge pledge = new Pledge(-10);
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        int userBudgetBefore = user1.getBudget();

        final PledgeEntity pledgeRes = projectEntity.pledge(pledge, activeFinancingRound, user1, pledgesDoneBeforw);

        assertThat(pledgeRes, is(new PledgeEntity(projectEntity, user1, pledge, activeFinancingRound)));
        assertThat(user1.getBudget(), is(userBudgetBefore + Math.abs(pledge.getAmount())));
    }

    @Test
    public void pledgeGoalAchieved_ReturnsFalseIfNotFullyPledged() throws Exception {
        assertThat(projectEntity.pledgeGoalAchieved(), is(false));
    }
    @Test
    public void pledgeGoalAchieved_returnsTrueWhenFullyPledged() throws Exception {
        projectEntity.setStatus(ProjectStatus.FULLY_PLEDGED);
        assertThat(projectEntity.pledgeGoalAchieved(), is(true));
    }

    @Test
    public void pledgedAmount() throws Exception {
        final FinancingRoundEntity activeFinancingRound = new FinancingRoundEntity();
        assertThat(projectEntity.pledgedAmount(preparePledgesDone(activeFinancingRound)), is(160));
    }

    @Test
    public void pledgedAmount_isZeroOnEmptyPledges() throws Exception {
        assertThat(projectEntity.pledgedAmount(new ArrayList<>()), is(0));
    }

    @Test
    public void countBackers() throws Exception {
        final FinancingRoundEntity activeFinancingRound = new FinancingRoundEntity();
        assertThat(projectEntity.countBackers(preparePledgesDone(activeFinancingRound)), is(2L));
    }

    @Test
    public void countBackers_isZeroOnEmptyPledges() throws Exception {
        assertThat(projectEntity.countBackers(new ArrayList<>()), is(0L));
    }

    @Test
    public void pledgedAmountOfUser() throws Exception {
        final FinancingRoundEntity activeFinancingRound = new FinancingRoundEntity();
        assertThat(projectEntity.pledgedAmountOfUser(preparePledgesDone(activeFinancingRound), user1), is(30));
    }

    @Test
    public void pledgedAmountOfUser_ReturnsZeroOnNullUser() throws Exception {
        final FinancingRoundEntity activeFinancingRound = new FinancingRoundEntity();
        assertThat(projectEntity.pledgedAmountOfUser(preparePledgesDone(activeFinancingRound), null), is(0));
    }

    @Test
    public void pledgedAmountOfUser_ReturnsZeroOnEmptyPledges() throws Exception {
        assertThat(projectEntity.pledgedAmountOfUser(new ArrayList<>(), user1), is(0));
    }


    private List<PledgeEntity> preparePledgesDone(FinancingRoundEntity activeFinancingRound) {
        List<PledgeEntity> res = new ArrayList<>();
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user2, new Pledge(60), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(20), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user2, new Pledge(70), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(-10), activeFinancingRound));
        return res;
    }
}