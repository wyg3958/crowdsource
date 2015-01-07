package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    public static final String PROJECT_ID = "projectId";
    public static final int INITIAL_USER_BUDGET = 100;

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @Mock
    private UserRepository userRepository;

    private UserEntity userEntity;

    @Before
    public void setUp() {
        userEntity = new UserEntity();
        userEntity.setBudget(INITIAL_USER_BUDGET);

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setPledgeGoal(500);

        projectEntity.setCreator(userEntity);
        when(projectRepository.findOne(PROJECT_ID)).thenReturn(projectEntity);
    }

    @Test
    public void testPledgeProject_rollbackOnError() throws Exception {
        Pledge pledge = new Pledge(10);

        RuntimeException thrownException = new RuntimeException("some internal db error");
        when(pledgeRepository.save(any(PledgeEntity.class))).thenThrow(thrownException);

        try {
            projectService.pledgeProject(PROJECT_ID, userEntity, pledge);
            Assert.fail("pledge project should fail if a db error occurs");
        }
        catch (RuntimeException e) {
            assertThat(e, is(thrownException));
        }

        verify(userRepository, times(2)).save(userEntity);
        assertThat(userEntity.getBudget(), is(INITIAL_USER_BUDGET));
    }

    // The remaining test cases are covered by ProjectControllerTest
}