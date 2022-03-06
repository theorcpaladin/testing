package com.getwiki.testing.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Section {
    
    private String name;
    @Setter
    private List<String> highlights;
    @Setter
    private Map<String, String> subsections; 

    public Section(String name) {
        this.name = name;
    }
}
