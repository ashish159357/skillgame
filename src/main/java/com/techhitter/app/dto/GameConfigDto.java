package com.techhitter.app.dto;

import lombok.Data;

@Data
public class GameConfigDto {
    private String subject;
    private Integer no_of_player;
    private Integer no_of_question;
    private Integer time_for_each_question;
    private String key;
}
