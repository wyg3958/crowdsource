package de.asideas.crowdsource.model.persistence;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PledgeEntityTest {

    @Test
    public void add_bothHavingSameProjectFinancingRoundAndUser() throws Exception {
        UserEntity user = user("usr0@la.de");
        FinancingRoundEntity financingRound = new FinancingRoundEntity();
        ProjectEntity project = new ProjectEntity();

        PledgeEntity p0 = pledgeEntity(7, "test_id0", project, user, financingRound);
        PledgeEntity p1 = pledgeEntity(3, "test_id0", project, user, financingRound);

        PledgeEntity res = p0.add(p1);

        assertThat(res.getAmount(), is(10));
        assertThat(res.getUser(), is(user));
        assertThat(res.getCreatedDate(), is(nullValue()));
        assertThat(res.getId(), is(nullValue()));
        assertThat(res.getLastModifiedDate(), is(nullValue()));
        assertThat(res.getFinancingRound(), is(financingRound));
        assertThat(res.getProject(), is(project));

    }
    @Test
    public void add_bothHavingDifferentProjectFinancingRoundAndUser() throws Exception {
        UserEntity user = user("usr0@la.de");
        FinancingRoundEntity financingRound0 = new FinancingRoundEntity();
        financingRound0.setId("test_id0");
        FinancingRoundEntity financingRound1 = new FinancingRoundEntity();
        financingRound1.setId("test_id1");

        ProjectEntity project0 = new ProjectEntity();
        project0.setId("test_id0");
        ProjectEntity project1 = new ProjectEntity();
        project0.setId("test_id1");

        PledgeEntity p0 = pledgeEntity(7, "test_id0", project0, user, financingRound0);
        PledgeEntity p1 = pledgeEntity(3, "test_id0", project1, user, financingRound1);

        PledgeEntity res = p0.add(p1);

        assertThat(res.getAmount(), is(10));
        assertThat(res.getUser(), is(user));
        assertThat(res.getCreatedDate(), is(nullValue()));
        assertThat(res.getLastModifiedDate(), is(nullValue()));
        assertThat(res.getFinancingRound(), is(nullValue()));
        assertThat(res.getProject(), is(nullValue()));

    }
    @Test
    public void add_otherIsNullShouldReturnThis() throws Exception {
        UserEntity user = user("usr0@la.de");
        FinancingRoundEntity financingRound0 = new FinancingRoundEntity();
        financingRound0.setId("test_id0");
        ProjectEntity project0 = new ProjectEntity();
        PledgeEntity p0 = pledgeEntity(7, "test_id0", project0, user, financingRound0);

        PledgeEntity res = p0.add(null);

        assertThat(res.getAmount(), is(7));
        assertThat(res.getUser(), is(user));
        assertThat(res.getCreatedDate(), is(nullValue()));
        assertThat(res.getLastModifiedDate(), is(nullValue()));
        assertThat(res.getFinancingRound(), is(financingRound0));
        assertThat(res.getProject(), is(project0));

    }

    private PledgeEntity pledgeEntity(int amount, String id, ProjectEntity project, UserEntity user, FinancingRoundEntity financingRound) {
        PledgeEntity res = new PledgeEntity();
        res.setAmount(amount);
        res.setFinancingRound(financingRound);
        res.setId(id);
        res.setProject(project);
        if(user != null){
            res.setUser(user);
        }
        return res;
    }

    private UserEntity user(String email) {
        UserEntity userEntity = new UserEntity(email);
        userEntity.setId("id_" + email);
        userEntity.setBudget(0);
        return userEntity;
    }
}