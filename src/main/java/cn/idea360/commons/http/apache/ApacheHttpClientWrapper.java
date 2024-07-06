package cn.idea360.commons.http.apache;

import cn.idea360.commons.http.HttpClientWrapper;
import cn.idea360.commons.http.HttpConfig;
import cn.idea360.commons.http.Request;
import cn.idea360.commons.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.TimeValue;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cuishiying
 */
@Slf4j
public class ApacheHttpClientWrapper implements HttpClientWrapper {

	private final CloseableHttpClient httpClient;

	/**
	 * 默认构造方法, 默认配置
	 */
	public ApacheHttpClientWrapper() {
		this(new HttpConfig());
	}

	/**
	 * 构造方法, 允许自定义配置
	 * @param httpConfig http配置
	 */
	public ApacheHttpClientWrapper(HttpConfig httpConfig) {
		this(httpConfig, null, null);
	}

	/**
	 * 构造方法, 允许拦截器
	 * @param httpConfig 配置
	 * @param requestInterceptor 请求拦截器
	 * @param responseInterceptor 响应拦截器
	 */
	@SuppressWarnings("all")
	public ApacheHttpClientWrapper(HttpConfig httpConfig, HttpRequestInterceptor requestInterceptor,
			HttpResponseInterceptor responseInterceptor) {
		if (Objects.isNull(httpConfig)) {
			httpConfig = new HttpConfig();
		}
		log.info("http配置: {}", httpConfig.toString());
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(httpConfig.getMaxTotal());
		connectionManager.setDefaultMaxPerRoute(httpConfig.getDefaultMaxPerRoute());

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(httpConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
				.setConnectionRequestTimeout(httpConfig.getConnectionRequestTimeout(), TimeUnit.MILLISECONDS)
				.setResponseTimeout(httpConfig.getSocketTimeout(), TimeUnit.MILLISECONDS).build();

		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

		HttpClientBuilder builder = HttpClients.custom().setDefaultHeaders(headers)
				.setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig);

		if (Objects.nonNull(requestInterceptor)) {
			builder.addRequestInterceptorFirst(requestInterceptor);
		}

		if (Objects.nonNull(responseInterceptor)) {
			builder.addResponseInterceptorFirst(responseInterceptor);
		}

		if (httpConfig.getMaxRetries() > 0) {
			builder.setRetryStrategy(
					new DefaultHttpRequestRetryStrategy(httpConfig.getMaxRetries(), TimeValue.ofSeconds(2)));
		}

		this.httpClient = builder.build();
		log.info("httpclient初始化完成");
	}

	@Override
	public Response get(Request request) throws IOException, URISyntaxException {
		URI uri = new URIBuilder(request.getUrl()).addParameters(convertParams(request.getParams())).build();
		HttpUriRequestBase httpRequest = new HttpGet(uri);
		httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		addHeaders(httpRequest, request.getHeaders());
		return executeRequest(httpRequest);
	}

	@Override
	public Response post(Request request) throws IOException, URISyntaxException {
		URI uri = new URIBuilder(request.getUrl()).addParameters(convertParams(request.getParams())).build();
		HttpUriRequestBase httpRequest = new HttpPost(uri);
		addHeaders(httpRequest, request.getHeaders());
		if (Objects.nonNull(request.getBody())) {
			httpRequest.setEntity(new StringEntity(request.getBody()));
		}
		return executeRequest(httpRequest);
	}

	@Override
	public Response put(Request request) throws IOException, URISyntaxException {
		URI uri = new URIBuilder(request.getUrl()).addParameters(convertParams(request.getParams())).build();
		HttpUriRequestBase httpRequest = new HttpPut(uri);
		addHeaders(httpRequest, request.getHeaders());
		if (Objects.nonNull(request.getBody())) {
			httpRequest.setEntity(new StringEntity(request.getBody()));
		}
		return executeRequest(httpRequest);
	}

	@Override
	public Response patch(Request request) throws IOException, URISyntaxException {
		URI uri = new URIBuilder(request.getUrl()).addParameters(convertParams(request.getParams())).build();
		HttpUriRequestBase httpRequest = new HttpPatch(uri);
		addHeaders(httpRequest, request.getHeaders());
		if (Objects.nonNull(request.getBody())) {
			httpRequest.setEntity(new StringEntity(request.getBody()));
		}
		return executeRequest(httpRequest);
	}

	@Override
	public Response delete(Request request) throws IOException, URISyntaxException {
		URI uri = new URIBuilder(request.getUrl()).addParameters(convertParams(request.getParams())).build();
		HttpUriRequestBase httpRequest = new HttpDelete(uri);
		httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		addHeaders(httpRequest, request.getHeaders());
		return executeRequest(httpRequest);
	}

	@SuppressWarnings("all")
	private Response executeRequest(HttpUriRequestBase request) throws IOException {
		try (Response response = httpClient.execute(request, new CustomResponseHandler())) {
			return response;
		}
		catch (ConnectTimeoutException e) {
			log.error("Connection timeout: " + e.getMessage());
			throw e;
		}
		catch (SocketTimeoutException e) {
			log.error("Socket timeout: " + e.getMessage());
			throw e;
		}
		catch (IOException e) {
			log.error("I/O error: " + e.getMessage());
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error: " + e.getMessage());
			throw new IOException("Unexpected error", e);
		}
	}

	private List<NameValuePair> convertParams(Map<String, Object> params) {
		if (Objects.nonNull(params)) {
			return params.entrySet().stream().map(e -> new BasicNameValuePair(e.getKey(), e.getValue().toString()))
					.collect(Collectors.toList());
		}
		return List.of();
	}

	private void addHeaders(HttpUriRequestBase request, Map<String, String> headers) {
		if (Objects.nonNull(headers)) {
			headers.forEach((key, value) -> request.setHeader(new BasicHeader(key, value)));
		}
	}

	@Override
	public void close() throws IOException {
		httpClient.close();
	}

}
