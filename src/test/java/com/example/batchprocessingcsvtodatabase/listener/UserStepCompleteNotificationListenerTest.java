package com.example.batchprocessingcsvtodatabase.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserStepCompleteNotificationListener Tests")
class UserStepCompleteNotificationListenerTest {

    private UserStepCompleteNotificationListener listener;

    @Mock
    private StepExecution stepExecution;

    @BeforeEach
    void setUp() {
        listener = new UserStepCompleteNotificationListener();
    }

    @Nested
    @DisplayName("beforeStep tests")
    class BeforeStepTests {

        @Test
        @DisplayName("beforeStep should not throw exception with valid StepExecution")
        void beforeStep_withValidStepExecution_shouldNotThrow() {
            // Arrange
            when(stepExecution.getId()).thenReturn(42L);

            // Act & Assert — no exception thrown
            listener.beforeStep(stepExecution);

            verify(stepExecution, atLeastOnce()).getId();
        }

        @Test
        @DisplayName("beforeStep should call getId() on the StepExecution for logging")
        void beforeStep_shouldInvokeGetId() {
            // Arrange
            when(stepExecution.getId()).thenReturn(1L);

            // Act
            listener.beforeStep(stepExecution);

            // Assert
            verify(stepExecution, atLeastOnce()).getId();
        }

        @Test
        @DisplayName("beforeStep should handle null id without throwing exception")
        void beforeStep_withNullId_shouldNotThrow() {
            // Arrange
            when(stepExecution.getId()).thenReturn(null);

            // Act & Assert — no exception thrown
            listener.beforeStep(stepExecution);

            verify(stepExecution, atLeastOnce()).getId();
        }

        @Test
        @DisplayName("beforeStep should handle large step execution id")
        void beforeStep_withLargeId_shouldNotThrow() {
            // Arrange
            when(stepExecution.getId()).thenReturn(Long.MAX_VALUE);

            // Act & Assert — no exception thrown
            listener.beforeStep(stepExecution);

            verify(stepExecution, atLeastOnce()).getId();
        }
    }

    @Nested
    @DisplayName("afterStep tests")
    class AfterStepTests {

        @Test
        @DisplayName("afterStep should return the ExitStatus from StepExecution")
        void afterStep_shouldReturnExitStatusFromStepExecution() {
            // Arrange
            when(stepExecution.getId()).thenReturn(10L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.COMPLETED);
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should return FAILED exit status when step failed")
        void afterStep_withFailedStep_shouldReturnFailedExitStatus() {
            // Arrange
            when(stepExecution.getId()).thenReturn(20L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.FAILED);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.FAILED);
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should return STOPPED exit status when step is stopped")
        void afterStep_withStoppedStep_shouldReturnStoppedExitStatus() {
            // Arrange
            when(stepExecution.getId()).thenReturn(30L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.STOPPED);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.STOPPED);
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should return UNKNOWN exit status when step result is unknown")
        void afterStep_withUnknownStatus_shouldReturnUnknownExitStatus() {
            // Arrange
            when(stepExecution.getId()).thenReturn(40L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.UNKNOWN);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.UNKNOWN);
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should return NOOP exit status when step is a no-op")
        void afterStep_withNoopStatus_shouldReturnNoopExitStatus() {
            // Arrange
            when(stepExecution.getId()).thenReturn(50L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.NOOP);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.NOOP);
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should return custom exit status from StepExecution")
        void afterStep_withCustomExitStatus_shouldReturnCustomExitStatus() {
            // Arrange
            var customExitStatus = new ExitStatus("CUSTOM_STATUS", "A custom exit description");
            when(stepExecution.getId()).thenReturn(60L);
            when(stepExecution.getExitStatus()).thenReturn(customExitStatus);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(customExitStatus);
            assertThat(result.getExitCode()).isEqualTo("CUSTOM_STATUS");
            assertThat(result.getExitDescription()).isEqualTo("A custom exit description");
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should call getId() on StepExecution for logging")
        void afterStep_shouldInvokeGetId() {
            // Arrange
            when(stepExecution.getId()).thenReturn(70L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

            // Act
            listener.afterStep(stepExecution);

            // Assert
            verify(stepExecution, atLeastOnce()).getId();
        }

        @Test
        @DisplayName("afterStep should handle null exit status returned by StepExecution")
        void afterStep_withNullExitStatus_shouldReturnNull() {
            // Arrange
            when(stepExecution.getId()).thenReturn(80L);
            when(stepExecution.getExitStatus()).thenReturn(null);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isNull();
            verify(stepExecution).getExitStatus();
        }

        @Test
        @DisplayName("afterStep should handle null step execution id without throwing exception")
        void afterStep_withNullId_shouldNotThrow() {
            // Arrange
            when(stepExecution.getId()).thenReturn(null);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

            // Act
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("Listener interface contract tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("Listener should implement StepExecutionListener")
        void listener_shouldImplementStepExecutionListener() {
            assertThat(listener).isInstanceOf(org.springframework.batch.core.StepExecutionListener.class);
        }

        @Test
        @DisplayName("beforeStep followed by afterStep should both execute without errors")
        void beforeStepThenAfterStep_shouldBothExecuteSuccessfully() {
            // Arrange
            when(stepExecution.getId()).thenReturn(99L);
            when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

            // Act
            listener.beforeStep(stepExecution);
            ExitStatus result = listener.afterStep(stepExecution);

            // Assert
            assertThat(result).isEqualTo(ExitStatus.COMPLETED);
            verify(stepExecution, atLeast(2)).getId();
            verify(stepExecution, times(1)).getExitStatus();
        }
    }
}
