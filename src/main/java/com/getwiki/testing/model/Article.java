package com.getwiki.testing.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Article {

    private String title;
    @Setter
    private Map<String,String> infobox;
    @Setter
    private List<String> keywords;

    private Map<String, List<String>> sections;

    public Article(String title) {
        this.title = title;
    }
}
