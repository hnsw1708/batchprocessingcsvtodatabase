package com.example.batchprocessingcsvtodatabase.listener;

import com.example.batchprocessingcsvtodatabase.model.User;
import com.example.batchprocessingcsvtodatabase.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJobExecutionNotificationListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private UserJobExecutionNotificationListener listener;

    // ---------------------------------------------------------------------------
    // beforeJob tests
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("beforeJob should log job id without throwing any exception")
    void beforeJob_shouldLogJobId_withoutException() {
        when(jobExecution.getJobId()).thenReturn(42L);

        listener.beforeJob(jobExecution);

        verify(jobExecution, times(1)).getJobId();
        // userRepository must NOT be touched in beforeJob
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("beforeJob should handle null job id gracefully")
    void beforeJob_shouldHandleNullJobId() {
        when(jobExecution.getJobId()).thenReturn(null);

        listener.beforeJob(jobExecution);

        verify(jobExecution, times(1)).getJobId();
        verifyNoInteractions(userRepository);
    }

    // ---------------------------------------------------------------------------
    // afterJob – COMPLETED status tests
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("afterJob with COMPLETED status should call findAll and log each user")
    void afterJob_whenStatusCompleted_shouldCallFindAll() {
        when(jobExecution.getJobId()).thenReturn(1L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        var user1 = new User();
        var user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        listener.afterJob(jobExecution);

        verify(jobExecution, times(1)).getJobId();
        verify(jobExecution, times(1)).getStatus();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("afterJob with COMPLETED status and empty repository should not throw")
    void afterJob_whenStatusCompleted_andRepositoryEmpty_shouldNotThrow() {
        when(jobExecution.getJobId()).thenReturn(2L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        listener.afterJob(jobExecution);

        verify(userRepository, times(1)).findAll();
    }

    // ---------------------------------------------------------------------------
    // afterJob – non-COMPLETED status tests
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("afterJob with FAILED status should still call findAll")
    void afterJob_whenStatusFailed_shouldStillCallFindAll() {
        when(jobExecution.getJobId()).thenReturn(3L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        listener.afterJob(jobExecution);

        verify(jobExecution, times(1)).getStatus();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("afterJob with STARTED status should still call findAll")
    void afterJob_whenStatusStarted_shouldStillCallFindAll() {
        when(jobExecution.getJobId()).thenReturn(4L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTED);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        listener.afterJob(jobExecution);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("afterJob with STOPPED status should still call findAll")
    void afterJob_whenStatusStopped_shouldStillCallFindAll() {
        when(jobExecution.getJobId()).thenReturn(5L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.STOPPED);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        listener.afterJob(jobExecution);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("afterJob with ABANDONED status should still call findAll")
    void afterJob_whenStatusAbandoned_shouldStillCallFindAll() {
        when(jobExecution.getJobId()).thenReturn(6L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.ABANDONED);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        listener.afterJob(jobExecution);

        verify(userRepository, times(1)).findAll();
    }

    // ---------------------------------------------------------------------------
    // afterJob – multiple users returned
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("afterJob should iterate over every user returned by findAll")
    void afterJob_shouldIterateOverAllUsersReturnedByRepository() {
        when(jobExecution.getJobId()).thenReturn(7L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        var user1 = new User();
        var user2 = new User();
        var user3 = new User();

        var users = List.of(user1, user2, user3);

        when(userRepository.findAll()).thenReturn(users);

        listener.afterJob(jobExecution);

        // findAll must have been called exactly once regardless of user count
        verify(userRepository, times(1)).findAll();
    }

    // ---------------------------------------------------------------------------
    // afterJob – null job id (edge case)
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("afterJob should handle null job id without throwing")
    void afterJob_shouldHandleNullJobId() {
        when(jobExecution.getJobId()).thenReturn(null);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        listener.afterJob(jobExecution);

        verify(userRepository, times(1)).findAll();
    }

    // ---------------------------------------------------------------------------
    // Verify userRepository is never touched in beforeJob
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("beforeJob must not interact with userRepository under any status")
    void beforeJob_mustNotInteractWithRepository() {
        when(jobExecution.getJobId()).thenReturn(99L);

        listener.beforeJob(jobExecution);

        verifyNoInteractions(userRepository);
    }
}