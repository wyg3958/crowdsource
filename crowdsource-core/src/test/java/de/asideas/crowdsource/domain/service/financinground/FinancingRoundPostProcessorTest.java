package de.asideas.crowdsource.domain.service.financinground;


import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import de.asideas.crowdsource.domain.model.ProjectEntity;
import de.asideas.crowdsource.domain.model.UserEntity;
import de.asideas.crowdsource.domain.presentation.FinancingRound;
import de.asideas.crowdsource.domain.shared.ProjectStatus;
import de.asideas.crowdsource.repository.FinancingRoundRepository;
import de.asideas.crowdsource.repository.ProjectRepository;
import de.asideas.crowdsource.repository.UserRepository;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinancingRoundPostProcessorTest {

    private ProjectEntity project_0;
    private ProjectEntity project_1;
    private ProjectEntity project_2;

    @InjectMocks
    private FinancingRoundPostProcessor financingRoundPostProcessor;

    @Mock
    private FinancingRoundRepository financingRoundRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;


    @Before
    public void init(){
        project_0 = projectEntity(new UserEntity("test_email"), "test_projectId_0", "title", 44, "short description", "description", ProjectStatus.DEFERRED, null);
        project_1 = projectEntity(new UserEntity("test_email"), "test_projectId_1", "title", 44, "short description", "description", ProjectStatus.FULLY_PLEDGED, null);
        project_2 = projectEntity(new UserEntity("test_email"), "test_projectId_2", "title", 44, "short description", "description", ProjectStatus.DEFERRED, null);
        when(financingRoundRepository.save(any(FinancingRoundEntity.class))).then(i -> i.getArguments()[0]);
    }

    @Test
    public void postProcess_succeeds() throws Exception {
        final FinancingRoundEntity terminatedRound = prepareInactiveFinancingRound();
        project_0.setFinancingRound(terminatedRound);
        project_1.setFinancingRound(terminatedRound);
        project_2.setFinancingRound(null);
        when(projectRepository.findByFinancingRound(terminatedRound)).thenReturn(Arrays.asList(project_0, project_1, project_2));

        final FinancingRoundEntity res = financingRoundPostProcessor.postProcess(terminatedRound);

        verify(projectRepository).findByFinancingRound(terminatedRound);
        verify(financingRoundRepository).save(res);
        verify(projectRepository, times(1)).save(any(ProjectEntity.class));
        assertThat(project_0.getStatus(), is(ProjectStatus.PUBLISHED));
        assertThat(project_1.getStatus(), is(ProjectStatus.FULLY_PLEDGED));
        assertThat(project_2.getStatus(), is(ProjectStatus.DEFERRED));
        assertThat(terminatedRound.getTerminationPostProcessingDone(), is(true));
    }

    @Test
    public void postProcess_doesNothingOnActiveRound() throws Exception {
        final FinancingRoundEntity activeRound = prepareActiveFinanzingRound();
        project_0.setFinancingRound(activeRound);

        final FinancingRoundEntity res = financingRoundPostProcessor.postProcess(activeRound);

        verify(projectRepository, never()).findByFinancingRound(activeRound);
        verify(projectRepository, never()).save(any(ProjectEntity.class));
        verify(financingRoundRepository, never()).save(any(FinancingRoundEntity.class));
        assertThat(res, is(activeRound));
        assertThat(res.getTerminationPostProcessingDone(), is(false));
    }


    private ProjectEntity projectEntity(UserEntity userEntity, String id, String title, int pledgeGoal, String shortDescription, String description, ProjectStatus status, DateTime lastModifiedDate) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(id);
        projectEntity.setTitle(title);
        projectEntity.setPledgeGoal(pledgeGoal);
        projectEntity.setShortDescription(shortDescription);
        projectEntity.setDescription(description);
        projectEntity.setCreator(userEntity);
        projectEntity.setStatus(status);
        projectEntity.setLastModifiedDate(lastModifiedDate);
        when(projectRepository.findOne(id)).thenReturn(projectEntity);
        return projectEntity;
    }

    private FinancingRoundEntity prepareActiveFinanzingRound() {
        FinancingRoundEntity res = aFinancingRound(new DateTime().plusDays(1));
        res.setId(UUID.randomUUID().toString());

        when(financingRoundRepository.findActive(any())).thenReturn(res);
        Assert.assertThat(res.active(), Is.is(true));
        return res;
    }

    private FinancingRoundEntity prepareInactiveFinancingRound() {
        FinancingRoundEntity res = aFinancingRound(new DateTime().minusDays(1));

        when(financingRoundRepository.findActive(any())).thenReturn(res);
        Assert.assertThat(res.active(), Is.is(false));
        return res;
    }

    private FinancingRoundEntity aFinancingRound(DateTime endDate) {
        FinancingRound creationCmd = new FinancingRound();
        creationCmd.setEndDate(endDate);
        creationCmd.setBudget(100);
        FinancingRoundEntity res = FinancingRoundEntity.newFinancingRound(creationCmd, 7);
        res.setStartDate(new DateTime().minusDays(2));
        res.setId("test_finRoundId");
        return res;
    }
}