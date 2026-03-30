package com.example.batchprocessingcsvtodatabase.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {

    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String country;
    private String birthday;
}