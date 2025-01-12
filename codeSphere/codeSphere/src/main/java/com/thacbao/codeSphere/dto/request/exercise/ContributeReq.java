package com.thacbao.codeSphere.dto.request.exercise;

import lombok.Data;

@Data
public class ContributeReq {
    private String title;

    private String paper;

    private String input;

    private String output;

    private String note;
}
