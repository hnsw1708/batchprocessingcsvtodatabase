package com.example.batchprocessingcsvtodatabase.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class UserStepCompleteNotificationListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(UserStepCompleteNotificationListener.class);

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Step completed with status: {}", stepExecution.getStatus());
        return null;
    }
}