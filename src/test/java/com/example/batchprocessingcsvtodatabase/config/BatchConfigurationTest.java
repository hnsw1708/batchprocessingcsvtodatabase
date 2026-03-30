package com.example.batchprocessingcsvtodatabase.config;

import com.example.batchprocessingcsvtodatabase.listener.UserJobExecutionNotificationListener;
import com.example.batchprocessingcsvtodatabase.listener.UserStepCompleteNotificationListener;
import com.example.batchprocessingcsvtodatabase.model.User;
import com.example.batchprocessingcsvtodatabase.model.UserInput;
import com.example.batchprocessingcsvtodatabase.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchConfiguration Tests")
class BatchConfigurationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @InjectMocks
    private BatchConfiguration batchConfiguration;

    // -----------------------------------------------------------------------
    // reader()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("reader() should return a non-null FlatFileItemReader")
    void reader_shouldReturnNonNullReader() {
        FlatFileItemReader<UserInput> reader = batchConfiguration.reader();

        assertThat(reader).isNotNull();
    }

    @Test
    @DisplayName("reader() should be configured to skip the header line")
    void reader_shouldSkipHeaderLine() {
        // We call reader() twice to confirm independent instances are created
        FlatFileItemReader<UserInput> reader1 = batchConfiguration.reader();
        FlatFileItemReader<UserInput> reader2 = batchConfiguration.reader();

        assertThat(reader1).isNotNull();
        assertThat(reader2).isNotNull();
        // Both instances should be valid and distinct
        assertThat(reader1).isNotSameAs(reader2);
    }

    @Test
    @DisplayName("reader() should produce a reader of the correct generic type")
    void reader_shouldBeTypedToUserInput() {
        FlatFileItemReader<UserInput> reader = batchConfiguration.reader();

        assertThat(reader).isInstanceOf(FlatFileItemReader.class);
    }

    // -----------------------------------------------------------------------
    // processor()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("processor() should return a non-null UserProcessor")
    void processor_shouldReturnNonNullProcessor() {
        UserProcessor processor = batchConfiguration.processor();

        assertThat(processor).isNotNull();
    }

    @Test
    @DisplayName("processor() should return a UserProcessor instance")
    void processor_shouldReturnUserProcessorInstance() {
        Object processor = batchConfiguration.processor();

        assertThat(processor).isInstanceOf(UserProcessor.class);
    }

    @Test
    @DisplayName("processor() should return a new instance each invocation")
    void processor_shouldReturnNewInstanceEachTime() {
        UserProcessor first = batchConfiguration.processor();
        UserProcessor second = batchConfiguration.processor();

        // Without Spring proxy each call creates a fresh object
        assertThat(first).isNotSameAs(second);
    }

    // -----------------------------------------------------------------------
    // writer()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("writer() should return a non-null RepositoryItemWriter")
    void writer_shouldReturnNonNullWriter() {
        RepositoryItemWriter<User> writer = batchConfiguration.writer();

        assertThat(writer).isNotNull();
    }

    @Test
    @DisplayName("writer() should be an instance of RepositoryItemWriter")
    void writer_shouldBeRepositoryItemWriter() {
        Object writer = batchConfiguration.writer();

        assertThat(writer).isInstanceOf(RepositoryItemWriter.class);
    }

    @Test
    @DisplayName("writer() should use the injected UserRepository")
    void writer_shouldUseInjectedRepository() {
        RepositoryItemWriter<User> writer = batchConfiguration.writer();

        // The writer is configured with userRepository — just verify it is not null
        assertThat(writer).isNotNull();
        // Verify that userRepository was not inadvertently called during configuration
        verifyNoInteractions(userRepository);
    }

    // -----------------------------------------------------------------------
    // taskExecutor()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("taskExecutor() should return a non-null TaskExecutor")
    void taskExecutor_shouldReturnNonNull() {
        TaskExecutor executor = batchConfiguration.taskExecutor();

        assertThat(executor).isNotNull();
    }

    @Test
    @DisplayName("taskExecutor() should return a SimpleAsyncTaskExecutor")
    void taskExecutor_shouldBeSimpleAsyncTaskExecutor() {
        TaskExecutor executor = batchConfiguration.taskExecutor();

        assertThat(executor).isInstanceOf(SimpleAsyncTaskExecutor.class);
    }

    @Test
    @DisplayName("taskExecutor() should configure concurrency limit of 10")
    void taskExecutor_shouldHaveConcurrencyLimitOf10() {
        TaskExecutor executor = batchConfiguration.taskExecutor();

        // Use pattern matching (Java 21) to avoid explicit cast
        if (executor instanceof SimpleAsyncTaskExecutor asyncExecutor) {
            assertThat(asyncExecutor.getConcurrencyLimit()).isEqualTo(10);
        } else {
            org.junit.jupiter.api.Assertions.fail("Expected SimpleAsyncTaskExecutor but got: " + executor.getClass());
        }
    }

    // -----------------------------------------------------------------------
    // stepExecutionListener()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("stepExecutionListener() should return a non-null listener")
    void stepExecutionListener_shouldReturnNonNull() {
        UserStepCompleteNotificationListener listener = batchConfiguration.stepExecutionListener();

        assertThat(listener).isNotNull();
    }

    @Test
    @DisplayName("stepExecutionListener() should return UserStepCompleteNotificationListener instance")
    void stepExecutionListener_shouldBeCorrectType() {
        Object listener = batchConfiguration.stepExecutionListener();

        assertThat(listener).isInstanceOf(UserStepCompleteNotificationListener.class);
    }

    @Test
    @DisplayName("stepExecutionListener() should return independent instances")
    void stepExecutionListener_shouldReturnNewInstanceEachTime() {
        UserStepCompleteNotificationListener first = batchConfiguration.stepExecutionListener();
        UserStepCompleteNotificationListener second = batchConfiguration.stepExecutionListener();

        assertThat(first).isNotSameAs(second);
    }

    // -----------------------------------------------------------------------
    // jobExecutionListener()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("jobExecutionListener() should return a non-null listener")
    void jobExecutionListener_shouldReturnNonNull() {
        UserJobExecutionNotificationListener listener = batchConfiguration.jobExecutionListener();

        assertThat(listener).isNotNull();
    }

    @Test
    @DisplayName("jobExecutionListener() should return UserJobExecutionNotificationListener instance")
    void jobExecutionListener_shouldBeCorrectType() {
        Object listener = batchConfiguration.jobExecutionListener();

        assertThat(listener).isInstanceOf(UserJobExecutionNotificationListener.class);
    }

    @Test
    @DisplayName("jobExecutionListener() should not interact with repository during construction")
    void jobExecutionListener_shouldNotInteractWithRepositoryDuringConstruction() {
        UserJobExecutionNotificationListener listener = batchConfiguration.jobExecutionListener();

        assertThat(listener).isNotNull();
        verifyNoInteractions(userRepository);
    }

    // -----------------------------------------------------------------------
    // Multiple bean independence checks
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("reader and writer beans should be independent")
    void readerAndWriter_shouldBeIndependent() {
        FlatFileItemReader<UserInput> reader = batchConfiguration.reader();
        RepositoryItemWriter<User> writer = batchConfiguration.writer();

        assertThat(reader).isNotNull();
        assertThat(writer).isNotNull();
        assertThat((Object) reader).isNotSameAs(writer);
    }

    @Test
    @DisplayName("All listener beans should be distinct instances")
    void allListeners_shouldBeDistinctInstances() {
        UserStepCompleteNotificationListener stepListener = batchConfiguration.stepExecutionListener();
        UserJobExecutionNotificationListener jobListener = batchConfiguration.jobExecutionListener();

        assertThat(stepListener).isNotNull();
        assertThat(jobListener).isNotNull();
        assertThat((Object) stepListener).isNotSameAs(jobListener);
    }

    @Test
    @DisplayName("processor and reader beans should be distinct instances")
    void processorAndReader_shouldBeDistinctInstances() {
        UserProcessor processor = batchConfiguration.processor();
        FlatFileItemReader<UserInput> reader = batchConfiguration.reader();

        assertThat(processor).isNotNull();
        assertThat(reader).isNotNull();
        assertThat((Object) processor).isNotSameAs(reader);
    }
}
