package com.example.batchprocessingcsvtodatabase.listener;

import com.example.batchprocessingcsvtodatabase.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class UserJobExecutionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(UserJobExecutionNotificationListener.class);

    private final UserRepository userRepository;

    public UserJobExecutionNotificationListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job started with id: {}", jobExecution.getJobId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job finished with id: {}", jobExecution.getJobId());

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job Completed!");
        }

        userRepository.findAll().forEach(user -> log.info("Found user: {}", user));
    }
}