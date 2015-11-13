package de.asideas.crowdsource.domain.model;

import de.asideas.crowdsource.domain.exception.InvalidRequestException;
import de.asideas.crowdsource.domain.presentation.FinancingRound;
import de.asideas.crowdsource.domain.presentation.Pledge;
import de.asideas.crowdsource.domain.presentation.project.Project;
import de.asideas.crowdsource.domain.shared.ProjectStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ProjectEntityTest {

    private static final int PLEDGE_GOAL = 200;

    private ProjectEntity projectEntity;
    private UserEntity user1;
    private UserEntity user2;
    private UserEntity user3;

    @Before
    public void setUp() {
        UserEntity creator = new UserEntity();
        creator.setId("id");

        Project project = new Project();
        projectEntity = new ProjectEntity(creator, project, anActiveFinancingRound());
        projectEntity.setPledgeGoal(PLEDGE_GOAL);

        user1 = new UserEntity("user1@xyz.com");
        user1.setId("test_id1");
        user1.setBudget(100);
        user2 = new UserEntity("user2@xyz.com");
        user2.setId("test_id2");
        user3 = new UserEntity("user3@xyz.com");
        user3.setId("test_id3");
    }

    /**
     * For a full integration test, checking all edge cases see {@link de.asideas.crowdsource.service.ProjectServiceTest}
     * @throws Exception
     */
    @Test
    public void pledge() throws Exception {
        final List<PledgeEntity> pledgesDoneBeforw = preparePledgesDone(projectEntity.getFinancingRound());
        final Pledge pledge = new Pledge(40);
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        int userBudgetBefore = user1.getBudget();

        final PledgeEntity pledgeRes = projectEntity.pledge(pledge, user1, pledgesDoneBeforw);

        assertThat(pledgeRes, is(new PledgeEntity(projectEntity, user1, pledge, projectEntity.getFinancingRound())));
        assertThat(user1.getBudget(), is(userBudgetBefore - pledge.getAmount()));
        assertThat(projectEntity.pledgeGoalAchieved(), is(true));
    }

    @Test
    public void pledge_reverse() throws Exception {
        final List<PledgeEntity> pledgesDoneBeforw = preparePledgesDone(projectEntity.getFinancingRound());
        final Pledge pledge = new Pledge(-10);
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        int userBudgetBefore = user1.getBudget();

        final PledgeEntity pledgeRes = projectEntity.pledge(pledge, user1, pledgesDoneBeforw);

        assertThat(pledgeRes, is(new PledgeEntity(projectEntity, user1, pledge, projectEntity.getFinancingRound())));
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

    @Test
    public void modifyStatus() throws Exception{
        boolean res = projectEntity.modifyStatus(ProjectStatus.PUBLISHED);

        MatcherAssert.assertThat(res, Matchers.is(true));
        MatcherAssert.assertThat(projectEntity.getStatus(), Matchers.is(ProjectStatus.PUBLISHED));
    }

    @Test
    public void modifyStatus_settingToPublishedAlthoughFullyPledgedThrowsIvalidRequestEx() throws Exception {
        projectEntity.setStatus(ProjectStatus.FULLY_PLEDGED);

        try {
            projectEntity.modifyStatus(ProjectStatus.PUBLISHED);
            fail("Expected InvalidRequestException was not thrown");
        } catch (InvalidRequestException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.is(InvalidRequestException.projectAlreadyFullyPledged().getMessage()));
        }
    }

    @Test
    public void modifyStatus_settingToDeferredThrowsExceptionWhenFinancingRoundAssignedAndActive() throws Exception {
        try {
            projectEntity.setFinancingRound(anActiveFinancingRound());
            projectEntity.modifyStatus(ProjectStatus.DEFERRED);
            fail("Expected InvalidRequestException was not thrown");
        } catch (InvalidRequestException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.is(InvalidRequestException.projectAlreadyInFinancingRound().getMessage()));
        }
    }

    @Test
    public void modifyStatus_settingToDeferredThrowsExceptionWhenAlreadyFullyPledged() throws Exception {
        projectEntity.setStatus(ProjectStatus.FULLY_PLEDGED);

        try {
            projectEntity.modifyStatus(ProjectStatus.DEFERRED);
            fail("Expected InvalidRequestException was not thrown");
        } catch (InvalidRequestException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.is(InvalidRequestException.projectAlreadyFullyPledged().getMessage()));
        }
    }

    @Test
    public void modifyStatus_settingToDeferredThrowsExceptionWhenRejected() throws Exception {
        projectEntity.setStatus(ProjectStatus.REJECTED);
        projectEntity.setFinancingRound(null);

        try{
            projectEntity.modifyStatus(ProjectStatus.DEFERRED);
            fail("Expected InvalidRequestException was not thrown");
        } catch (InvalidRequestException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.is(InvalidRequestException.setToDeferredNotPossibleOnRejected().getMessage()));
        }
    }

    @Test
    public void modifyStatus_settingToPublishedPossibleWhenDeferred() throws Exception {
        projectEntity.setStatus(ProjectStatus.DEFERRED);
        projectEntity.setFinancingRound(null);

        boolean res = projectEntity.modifyStatus(ProjectStatus.PUBLISHED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.PUBLISHED));
    }

    @Test
    public void modifyStatus_settingToDeferredPossibleWhenAlreadyPublished() throws Exception {
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        projectEntity.setFinancingRound(null);

        boolean res = projectEntity.modifyStatus(ProjectStatus.DEFERRED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.DEFERRED));
    }

    @Test
    public void modifyStatus_settingToDeferredPossibleWhenAssignedToNonActiveFinancingRound() throws Exception {
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        projectEntity.setFinancingRound(anInactiveFinancingRound());

        boolean res = projectEntity.modifyStatus(ProjectStatus.DEFERRED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.DEFERRED));
    }

    @Test
    public void modifyStatus_settingToProposedPossibleWhenAlreadyPublished() throws Exception {
        projectEntity.setStatus(ProjectStatus.PUBLISHED);
        projectEntity.setFinancingRound(null);

        boolean res = projectEntity.modifyStatus(ProjectStatus.PROPOSED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.PROPOSED));
    }

    @Test
    public void modifyStatus_settingToProposedPossibleWhenAlreadyDeferred() throws Exception {
        projectEntity.setStatus(ProjectStatus.DEFERRED);
        projectEntity.setFinancingRound(null);

        boolean res = projectEntity.modifyStatus(ProjectStatus.PROPOSED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.PROPOSED));
    }

    @Test
    public void modifyStatus_settingToProposedPossibleWhenAlreadyRejected() throws Exception {
        projectEntity.setStatus(ProjectStatus.REJECTED);
        projectEntity.setFinancingRound(null);

        boolean res = projectEntity.modifyStatus(ProjectStatus.PROPOSED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.PROPOSED));
    }

    @Test
    public void modifyStatus_settingToPublishedPossibleWhenAlreadyRejected() throws Exception {
        projectEntity.setStatus(ProjectStatus.REJECTED);
        projectEntity.setFinancingRound(null);

        boolean res = projectEntity.modifyStatus(ProjectStatus.PUBLISHED);

        assertThat(res, is(true));
        assertThat(projectEntity.getStatus(), is(ProjectStatus.PUBLISHED));
    }

    @Test
    public void onFinancingRoundTerminated_ProjectPublishedWhenDeferred() throws Exception{
        projectEntity.setStatus(ProjectStatus.DEFERRED);

        projectEntity.onFinancingRoundTerminated(projectEntity.getFinancingRound());

        assertThat(projectEntity.getStatus(), is(ProjectStatus.PUBLISHED));
        assertThat(projectEntity.getFinancingRound(), is(nullValue()));
    }
    @Test
    public void onFinancingRoundTerminated_ProjectUnchangedWhenDeferredButDifferentFinancingRound() throws Exception{
        projectEntity.setStatus(ProjectStatus.DEFERRED);
        projectEntity.onFinancingRoundTerminated(anInactiveFinancingRound());

        assertThat(projectEntity.getStatus(), is(ProjectStatus.DEFERRED));
        assertThat(projectEntity.getFinancingRound().getId(), is(anActiveFinancingRound().getId()));
    }

    private FinancingRoundEntity anInactiveFinancingRound() {
        FinancingRoundEntity res = aFinancingRound(new DateTime().minusDays(1));
        assertThat(res.active(), is(false));
        res.setId("test_IdInActive");
        return res;
    }

    private FinancingRoundEntity anActiveFinancingRound() {
        FinancingRoundEntity res = aFinancingRound(new DateTime().plusDays(1));
        assertThat(res.active(), is(true));
        res.setId("test_IdActive");
        return res;
    }

    private FinancingRoundEntity aFinancingRound(DateTime endDate) {
        FinancingRound creationCmd = new FinancingRound();
        creationCmd.setEndDate(endDate);
        creationCmd.setBudget(100);
        FinancingRoundEntity res = FinancingRoundEntity.newFinancingRound(creationCmd, 7);
        res.setStartDate(new DateTime().minusDays(2));
        return res;
    }

    private List<PledgeEntity> preparePledgesDone(FinancingRoundEntity activeFinancingRound) {
        List<PledgeEntity> res = new ArrayList<>();
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user2, new Pledge(60), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user3, new Pledge(180), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(20), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user2, new Pledge(70), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user1, new Pledge(-10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user3, new Pledge(-180), activeFinancingRound));

        res.add(new PledgeEntity(projectEntity, user3, new Pledge(-10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user3, new Pledge(110), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user3, new Pledge(+10), activeFinancingRound));
        res.add(new PledgeEntity(projectEntity, user3, new Pledge(-110), activeFinancingRound));
        return res;
    }
}