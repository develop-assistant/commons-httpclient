package cn.idea360.commons.http;

import cn.idea360.commons.http.apache.ApacheHttpClientWrapper;
import cn.idea360.commons.http.jdk.JdkHttpClientWrapper;
import cn.idea360.commons.http.okhttp.OkHttpClientWrapper;
import cn.idea360.commons.http.spring.RestTemplateWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author cuishiying
 */
@Slf4j
public class HttpClientFactory {

	private static final String APACHE_HTTP_CLIENT_SPI = "org.apache.hc.client5.http.classic.HttpClient";

	private static final String OK_HTTP_SPI = "okhttp3.OkHttpClient";

	private static final String REST_TEMPLATE_SPI = "org.springframework.web.client.RestTemplate";

	/**
	 * 无参入口
	 * @return client
	 */
	public static HttpClientWrapper createHttpClient() {
		return createHttpClient(new HttpConfig());
	}

	/**
	 * 有参入口, 允许自定义http配置
	 * @param httpConfig http配置
	 * @return client
	 */
	public static HttpClientWrapper createHttpClient(HttpConfig httpConfig) {
		HttpClientWrapper httpClientWrapper;
		if (isPresent(APACHE_HTTP_CLIENT_SPI)) {
			log.info("ApacheHttpClient初始化...");
			httpClientWrapper = ApacheHttpClientAdapter.createHttpClientWrapper(httpConfig);
		}
		else {
			log.info("JdkHttpClient初始化...");
			httpClientWrapper = JdkClientAdapter.createHttpClientWrapper(httpConfig);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				httpClientWrapper.close();
			}
			catch (IOException e) {
				log.error("close http client error.", e);
			}
		}));
		return httpClientWrapper;
	}

	private HttpClientFactory() {
	}

	private static boolean isPresent(String className) {
		try {
			Class.forName(className, false, HttpUtils.class.getClassLoader());
			return true;
		}
		catch (ClassNotFoundException ex) {
			return false;
		}
	}

	private static class ApacheHttpClientAdapter {

		public static HttpClientWrapper createHttpClientWrapper(HttpConfig httpConfig) {
			return new ApacheHttpClientWrapper(httpConfig);
		}

	}

	private static class OkHttpClientAdapter {

		public static HttpClientWrapper createHttpClientWrapper(HttpConfig httpConfig) {
			return new OkHttpClientWrapper();
		}

	}

	private static class RestTemplateAdapter {

		public static HttpClientWrapper createHttpClientWrapper(HttpConfig httpConfig) {
			return new RestTemplateWrapper();
		}

	}

	private static class JdkClientAdapter {

		public static HttpClientWrapper createHttpClientWrapper(HttpConfig httpConfig) {
			return new JdkHttpClientWrapper(httpConfig);
		}

	}

}
