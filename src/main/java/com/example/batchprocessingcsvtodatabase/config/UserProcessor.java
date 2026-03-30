package com.example.batchprocessingcsvtodatabase.config;

import com.example.batchprocessingcsvtodatabase.model.Gender;
import com.example.batchprocessingcsvtodatabase.model.User;
import com.example.batchprocessingcsvtodatabase.model.UserInput;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UserProcessor implements ItemProcessor<UserInput, User> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public User process(UserInput userInput) throws Exception {
        User user = new User();
        user.setPersonId(UUID.fromString(userInput.getPersonId()));
        user.setFirstName(userInput.getFirstName());
        user.setLastName(userInput.getLastName());
        user.setEmail(userInput.getEmail());
        user.setCountry(userInput.getCountry());

        LocalDate birthday = LocalDateTime.parse(userInput.getBirthday(), FORMATTER).toLocalDate();
        user.setBirthday(birthday);
        user.setAge(Period.between(birthday, LocalDate.now()).getYears());

        if ("Male".equals(userInput.getGender())) {
            user.setGender(Gender.MALE);
        } else {
            user.setGender(Gender.FEMALE);
        }

        return user;
    }
}