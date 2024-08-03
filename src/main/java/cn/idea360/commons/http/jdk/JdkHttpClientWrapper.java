package cn.idea360.commons.http.jdk;

import cn.idea360.commons.http.HttpClientWrapper;
import cn.idea360.commons.http.HttpConfig;
import cn.idea360.commons.http.Request;
import cn.idea360.commons.http.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author cuishiying
 */
@Slf4j
public class JdkHttpClientWrapper implements HttpClientWrapper {

	private final HttpClient httpClient;

	private final List<Consumer<HttpRequest.Builder>> requestInterceptors;

	private final List<Consumer<Response>> responseInterceptors;

	/**
	 * 默认构造方法, 默认配置
	 */
	public JdkHttpClientWrapper() {
		this(new HttpConfig());
	}

	/**
	 * 构造方法, 允许自定义配置
	 * @param httpConfig http配置
	 */
	public JdkHttpClientWrapper(HttpConfig httpConfig) {
		this(httpConfig, null, null);
	}

	/**
	 * 构造方法, 允许拦截器
	 * @param httpConfig 配置
	 * @param requestInterceptors 请求拦截器
	 * @param responseInterceptors 响应拦截器
	 */
	public JdkHttpClientWrapper(HttpConfig httpConfig, List<Consumer<HttpRequest.Builder>> requestInterceptors,
			List<Consumer<Response>> responseInterceptors) {
		if (Objects.isNull(httpConfig)) {
			httpConfig = new HttpConfig();
		}
		log.info("http配置: {}", httpConfig.toString());
		this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(httpConfig.getConnectTimeout()))
				.version(HttpClient.Version.HTTP_2).build();
		this.requestInterceptors = requestInterceptors;
		this.responseInterceptors = responseInterceptors;
		log.info("httpclient初始化完成");
	}

	@Override
	public Response get(Request request) throws IOException, URISyntaxException, InterruptedException {
		URI uri = buildUri(request.getUrl(), request.getParams());
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri).GET();
		requestBuilder.header("Accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded");
		addHeaders(requestBuilder, request.getHeaders());
		applyRequestInterceptors(requestBuilder);
		return executeRequest(requestBuilder);
	}

	@Override
	public Response post(Request request) throws IOException, URISyntaxException, InterruptedException {
		URI uri = buildUri(request.getUrl(), request.getParams());
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
				.POST(Objects.nonNull(request.getBody())
						? HttpRequest.BodyPublishers.ofString(request.getBody(), StandardCharsets.UTF_8)
						: HttpRequest.BodyPublishers.noBody());
		requestBuilder.header("Accept", "application/json").header("Content-Type", "application/json");
		addHeaders(requestBuilder, request.getHeaders());
		applyRequestInterceptors(requestBuilder);
		return executeRequest(requestBuilder);
	}

	@Override
	public Response put(Request request) throws IOException, URISyntaxException, InterruptedException {
		URI uri = buildUri(request.getUrl(), request.getParams());
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
				.PUT(Objects.nonNull(request.getBody())
						? HttpRequest.BodyPublishers.ofString(request.getBody(), StandardCharsets.UTF_8)
						: HttpRequest.BodyPublishers.noBody());
		requestBuilder.header("Accept", "application/json").header("Content-Type", "application/json");
		addHeaders(requestBuilder, request.getHeaders());
		applyRequestInterceptors(requestBuilder);
		return executeRequest(requestBuilder);
	}

	@Override
	public Response patch(Request request) throws IOException, URISyntaxException, InterruptedException {
		URI uri = buildUri(request.getUrl(), request.getParams());
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri).method("PATCH",
				Objects.nonNull(request.getBody())
						? HttpRequest.BodyPublishers.ofString(request.getBody(), StandardCharsets.UTF_8)
						: HttpRequest.BodyPublishers.noBody());
		requestBuilder.header("Accept", "application/json").header("Content-Type", "application/json");
		addHeaders(requestBuilder, request.getHeaders());
		applyRequestInterceptors(requestBuilder);
		return executeRequest(requestBuilder);
	}

	@Override
	public Response delete(Request request) throws IOException, URISyntaxException, InterruptedException {
		URI uri = buildUri(request.getUrl(), request.getParams());
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri).DELETE();
		requestBuilder.header("Accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded");
		addHeaders(requestBuilder, request.getHeaders());
		applyRequestInterceptors(requestBuilder);
		return executeRequest(requestBuilder);
	}

	private Response executeRequest(HttpRequest.Builder requestBuilder) throws IOException, InterruptedException {
		HttpRequest request = requestBuilder.build();
		try {
			HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			Response response = new Response(httpResponse.statusCode(), null, httpResponse.body());
			applyResponseInterceptors(response);
			return response;
		}
		catch (HttpConnectTimeoutException e) {
			log.error("Connection timeout: " + e.getMessage());
			throw e;
		}
		catch (HttpTimeoutException e) {
			log.error("Request timeout: " + e.getMessage());
			throw e;
		}
		catch (IOException e) {
			log.error("I/O error: " + e.getMessage());
			throw e;
		}
		catch (InterruptedException e) {
			log.error("Request interrupted: " + e.getMessage());
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error: " + e.getMessage());
			throw new IOException("Unexpected error", e);
		}
	}

	private URI buildUri(String url, Map<String, Object> params) throws URISyntaxException {
		URI uri = new URI(url);
		String query = uri.getQuery() == null ? "" : uri.getQuery();

		if (params != null && !params.isEmpty()) {
			String paramsString = params.entrySet().stream()
					.map(e -> e.getKey() + "=" + e.getValue().toString())
					.collect(Collectors.joining("&"));

			if (query.isEmpty()) {
				query = paramsString;
			}
			else {
				query += "&" + paramsString;
			}
		}

		return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
	}

	private void addHeaders(HttpRequest.Builder requestBuilder, Map<String, String> headers) {
		if (Objects.nonNull(headers)) {
			headers.forEach(requestBuilder::setHeader);
		}
	}

	private void applyRequestInterceptors(HttpRequest.Builder requestBuilder) {
		if (requestInterceptors != null) {
			for (Consumer<HttpRequest.Builder> interceptor : requestInterceptors) {
				interceptor.accept(requestBuilder);
			}
		}
	}

	private void applyResponseInterceptors(Response response) {
		if (responseInterceptors != null) {
			for (Consumer<Response> interceptor : responseInterceptors) {
				interceptor.accept(response);
			}
		}
	}

}
