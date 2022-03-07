package com.getwiki.testing.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import com.getwiki.testing.model.Article;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class WikiDataToArticleMapperTest {
    
    private WikiDataToArticleMapper mapper;

    private DocumentBuilder builder;

    @BeforeAll
    public void setup() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builder = builderFactory.newDocumentBuilder();
    }

    @Test
    public void wikiDataSuccessfullyMappedToArticle() throws Exception {
        Document xmlDocument = builder.parse("src/test/resources/simple-header.xml");
        Article article = mapper.mapDataToArticle(xmlDocument);
        assertTrue(article.getTitle().equals("I am H1"));
    }

    @Test
    public void exceptionThrownWhenEvaluatingDocument_throwException() throws Exception {
        Document xmlDocument = builder.parse("src/test/resources/cause-havoc.xml");
        Assertions.assertThrows(XPathExpressionException.class, () -> {
            mapper.mapDataToArticle(xmlDocument);
        });
    }
}
