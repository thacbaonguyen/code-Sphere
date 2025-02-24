package com.thacbao.codeSphere.dto.response.exercise;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private int id;
    private String code;

    private String title;

    private String paper;

    private String input;

    private String output;

    private String note;

    private String createdBy;

    private String createdAt;

    private String subject;

    private String description;

    private int level;

    private int timeLimit;

    private int memoryLimit;

    private String topic;

    public ExerciseDTO(String code, String title, String description, int level, String subject, String topic, int timeLimit, int memoryLimit) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.level = level;
        this.subject = subject;
        this.topic = topic;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }
}
