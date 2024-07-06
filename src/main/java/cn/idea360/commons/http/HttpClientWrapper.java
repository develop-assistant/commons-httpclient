package cn.idea360.commons.http;

import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author cuishiying
 */
public interface HttpClientWrapper extends Closeable {

	/**
	 * get请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	default Response get(Request request) throws IOException, URISyntaxException, InterruptedException {
		throw new UnSupportException();
	}

	/**
	 * post请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	default Response post(Request request) throws IOException, URISyntaxException, InterruptedException {
		throw new UnSupportException();
	}

	/**
	 * put请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	default Response put(Request request) throws IOException, URISyntaxException, InterruptedException {
		throw new UnSupportException();
	}

	/**
	 * patch请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	default Response patch(Request request) throws IOException, URISyntaxException, InterruptedException {
		throw new UnSupportException();
	}

	/**
	 * delete请求
	 * @param request 请求参数
	 * @return 响应
	 * @throws IOException 异常
	 * @throws URISyntaxException 异常
	 * @throws InterruptedException 异常
	 */
	default Response delete(Request request) throws IOException, URISyntaxException, InterruptedException {
		throw new UnSupportException();
	}

	@Override
	default void close() throws IOException {

	}

}
