package com.getwiki.testing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.getwiki.testing.model.Article;
import com.google.gson.Gson;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@RestController
public class WikiController {
    
	private static final String WIKI_PAGE_URL = "https://en.wikipedia.org/wiki/Heidenheim_an_der_Brenz";
	private static final String TITLE_EXPRESSION = "//h1/text()";
	private static final String HREF_EXPRESSION = "//p/a[contains(@href, '/wiki/')]";
	private static final String INFOBOX_EXPRESSION = "//table[contains(@class, 'infobox')]";
	private Article article;

	private Gson gson = new Gson();

    @GetMapping("/test")
	public String index() {

		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(WIKI_PAGE_URL);
			try(CloseableHttpResponse response = httpclient.execute(httpGet)) {
				HttpEntity entity = response.getEntity();
				int httpCode = response.getCode();
				if(httpCode == 200) {
					byte[] xmlBytes = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);

					InputStream inputStream = new ByteArrayInputStream(xmlBytes);
					// InputSource inputSource = new InputSource(inputStream);

					DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = builderFactory.newDocumentBuilder();
					Document xmlDocument = builder.parse(inputStream);
					
					mapDataToArticle(xmlDocument);
					inputStream.close();
					return gson.toJson(article);
				} else {
					return "A non-200 http response was returned: " + httpCode;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "There was an Exception: " + e.getMessage();
		}
	}

	private void mapDataToArticle(Document xmlDocument) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		List<String> wikiLinks = new ArrayList<>();

		article = new Article(xPath.compile(TITLE_EXPRESSION).evaluate(xmlDocument));
		NodeList nodes = (NodeList) xPath.compile(HREF_EXPRESSION).evaluate(xmlDocument, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			wikiLinks.add(nodes.item(i).getTextContent());
		}
		article.setKeywords(wikiLinks);
		article.getKeywords().stream().forEach(hl -> System.out.println(hl));

	}

}
