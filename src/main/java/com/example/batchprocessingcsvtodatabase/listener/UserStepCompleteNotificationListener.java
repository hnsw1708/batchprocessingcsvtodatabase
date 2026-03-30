package com.example.batchprocessingcsvtodatabase.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class UserStepCompleteNotificationListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("UserStepCompleteNotificationListener | beforeStep | StepExecution job id : " + stepExecution.getId());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("UserStepCompleteNotificationListener | afterStep | StepExecution job id : " + stepExecution.getId());
        return;
    }

}
