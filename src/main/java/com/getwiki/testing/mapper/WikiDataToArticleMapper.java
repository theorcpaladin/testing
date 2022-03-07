package com.getwiki.testing.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.getwiki.testing.model.Article;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class WikiDataToArticleMapper {

	private static final String TITLE_EXPRESSION = "//h1/text()";
	// private static final String INFOBOX_EXPRESSION = "//table[contains(@class, 'infobox')]";
	// private static final String SUBTITLE_EXPRESSION = "//h2/span[@class='mw-headline']";
	private static final String PHREF_EXPRESSION = "//p/a[contains(@href, '/wiki/')]";
	// private static final String LHREF_EXPRESSION = "//div[@class='mw-parser-output']/*/li/a[contains(@href, '/wiki/')] | //div[@class='mw-parser-output']/div/*/li/a[contains(@href, '/wiki/')]";
	// private static String SECTION_HREF_EXPRESSION = "//h2/p/a/following::href[count(preceding::h2) = {}]";
    
    public Article mapDataToArticle(Document xmlDocument) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		List<String> wikiKeywords = new ArrayList<>();

		Article article = new Article(xPath.compile(TITLE_EXPRESSION).evaluate(xmlDocument));
		NodeList linkNodes = (NodeList) xPath.compile(PHREF_EXPRESSION).evaluate(xmlDocument, XPathConstants.NODESET);
		for (int i = 0; i < linkNodes.getLength(); i++) {
			wikiKeywords.add(linkNodes.item(i).getTextContent());
		}
		article.setKeywords(wikiKeywords);
        return article;
	}
}
