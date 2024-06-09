package com.techhitter.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
public class QueObject {
    private String que;
    private ArrayList<String> options;
    private boolean type;
    private ArrayList<String> ans;
    private String subject;
}
