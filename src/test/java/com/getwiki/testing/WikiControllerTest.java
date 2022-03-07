package com.getwiki.testing;

import static com.getwiki.testing.WikiController.ERROR_RESPONSE;
import static com.getwiki.testing.WikiController.EXCEPTION_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import com.getwiki.testing.mapper.WikiDataToArticleMapper;
import com.getwiki.testing.model.Article;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WikiControllerTest {

	private static final String DUMMY_DATA = "dummy_data";
	private static final String DUMMY_TITLE = "dummy_title";
	private static final String DUMMY_EXCEPTION_MESSAGE = "I am angry!";

	@InjectMocks
	private WikiController controller;

	@Mock
	private WikiDataToArticleMapper mapper;

	@Mock
	private CloseableHttpClient httpClient;

	@Test
	public void successfullyGetWikiData() throws Exception {
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		HttpEntity entity = HttpEntities.create(DUMMY_DATA);
		response.setEntity(entity);
		response.setCode(200);
		when(httpClient.execute(any())).thenReturn(response);

		Article article = new Article(DUMMY_TITLE);
		when(mapper.mapDataToArticle(any())).thenReturn(article);

		String wikiInfo = controller.index();
		assertTrue(wikiInfo.contains(DUMMY_TITLE));
	}

	@Test
	public void non200ResponseReturnedWhenGettingWikiData_returnErrorMessage() throws Exception {
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		response.setCode(500);
		when(httpClient.execute(any())).thenReturn(response);

		String wikiInfo = controller.index();
		assertTrue(wikiInfo.contains(ERROR_RESPONSE));
	}

	@Test
	public void exceptionThrownWhenGettingWikiData_returnErrorMessage() throws Exception {
		when(httpClient.execute(any())).thenThrow(new IOException(DUMMY_EXCEPTION_MESSAGE));

		String wikiInfo = controller.index();
		assertTrue(wikiInfo.contains(EXCEPTION_RESPONSE + DUMMY_EXCEPTION_MESSAGE));
	}

	@Test
	public void exceptionThrownWhenMappingWikiData_returnErrorMessage() throws Exception {
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		HttpEntity entity = HttpEntities.create(DUMMY_DATA);
		response.setEntity(entity);
		response.setCode(200);
		when(httpClient.execute(any())).thenReturn(response);
		when(mapper.mapDataToArticle(any())).thenThrow(new XPathExpressionException(DUMMY_EXCEPTION_MESSAGE));

		String wikiInfo = controller.index();
		assertTrue(wikiInfo.contains(EXCEPTION_RESPONSE + DUMMY_EXCEPTION_MESSAGE));
	}
}