package com.getwiki.testing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.getwiki.testing.mapper.WikiDataToArticleMapper;
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
import org.xml.sax.SAXException;

@RestController
public class WikiController {

	private static final String WIKI_PAGE_URL = "https://en.wikipedia.org/wiki/Heidenheim_an_der_Brenz";
	protected static final String EXCEPTION_RESPONSE = "There was an Exception: ";
	protected static final String ERROR_RESPONSE = "A non-200 http response was returned: ";

	private Gson gson = new Gson();
	private WikiDataToArticleMapper mapper;
	private CloseableHttpClient httpClient;

    @GetMapping("/test")
	public String index() throws Exception {
		try {
			httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(WIKI_PAGE_URL);
			try(CloseableHttpResponse response = httpClient.execute(httpGet)) {
				HttpEntity entity = response.getEntity();
				int httpCode = response.getCode();
				if(httpCode == 200) {
					Document xmlDocument = buildDocumentFromResponseEntity(entity);
					mapper = new WikiDataToArticleMapper();
					Article article = mapper.mapDataToArticle(xmlDocument);
					return gson.toJson(article);
				} else {
					return ERROR_RESPONSE + httpCode;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return EXCEPTION_RESPONSE + e.getMessage();
		} finally {
			httpClient.close();
		}
	}

	private Document buildDocumentFromResponseEntity(HttpEntity entity) throws IOException, ParserConfigurationException, SAXException {
		byte[] xmlBytes = EntityUtils.toByteArray(entity);
		EntityUtils.consume(entity);
		InputStream inputStream = new ByteArrayInputStream(xmlBytes);

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(inputStream);
		inputStream.close();
		return xmlDocument;
	}

}
