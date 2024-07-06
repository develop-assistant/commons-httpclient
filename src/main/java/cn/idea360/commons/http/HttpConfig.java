package cn.idea360.commons.http;

import lombok.Data;

/**
 * @author cuishiying
 */
@Data
public class HttpConfig {

	/**
	 * 用于设置与服务器建立连接的最大等待时间(ms)
	 */
	private int connectTimeout = 5000;

	/**
	 * 用于设置从连接池获取连接的最大等待时间(ms)
	 */
	private int connectionRequestTimeout = 5000;

	/**
	 * 用于设置读取数据的超时时间(ms)
	 */
	private int socketTimeout = 5000;

	/**
	 * 设置连接池的最大连接数
	 */
	private int maxTotal = 100;

	/**
	 * 设置每个路由的默认最大连接数
	 */
	private int defaultMaxPerRoute = 20;

	/**
	 * 最大重试次数
	 */
	private int maxRetries = 0;

}
