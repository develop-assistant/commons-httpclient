package cn.idea360.commons.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author cuishiying
 */
@Slf4j
public class HttpUtils {

	private static final HttpClientWrapper HTTP_CLIENT_WRAPPER;

	static {
		HTTP_CLIENT_WRAPPER = HttpClientFactory.createHttpClient();
	}

	private HttpUtils() {
	}

	/**
	 * get请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	public static Response get(Request request) throws IOException, URISyntaxException, InterruptedException {
		return HTTP_CLIENT_WRAPPER.get(request);
	}

	/**
	 * post请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	public static Response post(Request request) throws IOException, URISyntaxException, InterruptedException {
		return HTTP_CLIENT_WRAPPER.post(request);
	}

	/**
	 * put请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	public static Response put(Request request) throws IOException, URISyntaxException, InterruptedException {
		return HTTP_CLIENT_WRAPPER.put(request);
	}

	/**
	 * patch请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	public static Response patch(Request request) throws IOException, URISyntaxException, InterruptedException {
		return HTTP_CLIENT_WRAPPER.patch(request);
	}

	/**
	 * delete请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	public static Response delete(Request request) throws IOException, URISyntaxException, InterruptedException {
		return HTTP_CLIENT_WRAPPER.delete(request);
	}

}
