package com.getwiki.testing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.getwiki.testing.model.Article;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@RestController
public class WikiController {
    
	private static final String TITLE_EXPRESSION = "//h1/text()";
	private static final String H2_EXPRESSION = "/html/body/div[@id='content']/div[@id='bodyContent']/div[@id='catlinks']/div[@id='mw-content-text']/div[@class='mw-parser-output']/h2[1]/span[1]/text()";
	private Article article;

	private static final String WIKI_PAGE_URL = "https://en.wikipedia.org/wiki/Heidenheim_an_der_Brenz";
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
					InputSource inputSource = new InputSource(inputStream);
					inputStream.close();

					mapDataToArticle(inputSource);
					return article.getTitle();
				} else {
					return "A non-200 http response was returned: " + httpCode;
				}

			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return "There was an IOException: " + ioe.getMessage();
		}
	}

	private void mapDataToArticle(InputSource inputSource) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			article = new Article(xPath.compile(TITLE_EXPRESSION).evaluate(inputSource));
			// String temp = xPath.compile(H2_EXPRESSION).evaluate(inputSource);
			// System.out.println(temp);
			// NodeList nodes = (NodeList) xPath.compile(H2_EXPRESSION).evaluate(inputSource, XPathConstants.NODESET);
			// System.out.println(nodes.getLength());
		} catch (XPathExpressionException xpee) {
			xpee.printStackTrace();
		}
	}

}
