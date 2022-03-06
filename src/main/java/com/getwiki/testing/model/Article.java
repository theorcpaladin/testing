package com.getwiki.testing.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Article {
    
    private String title;
    @Setter
    private List<Section> sections;

    public Article(String title) {
        this.title = title;
    }
}
