package cn.idea360.commons.http;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author cuishiying
 */
@Data
public class Request implements Serializable {

	/**
	 * 请求地址
	 */
	private String url;

	/**
	 * 请求头
	 */
	private Map<String, String> headers;

	/**
	 * 表单参数
	 */
	private Map<String, Object> params;

	/**
	 * 请求体(支持POST、PUT、PATCH)
	 */
	private String body;

	Request(String url, Map<String, String> headers, Map<String, Object> params, String body) {
		this.url = url;
		this.headers = headers;
		this.params = params;
		this.body = body;
	}

	public static Request.RequestBuilder builder() {
		return new Request.RequestBuilder();
	}

	public static class RequestBuilder {

		private String url;

		private Map<String, String> headers;

		private Map<String, Object> params;

		private String body;

		RequestBuilder() {
		}

		public Request.RequestBuilder url(String url) {
			this.url = url;
			return this;
		}

		public Request.RequestBuilder headers(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}

		public Request.RequestBuilder params(Map<String, Object> params) {
			this.params = params;
			return this;
		}

		public Request.RequestBuilder body(String body) {
			this.body = body;
			return this;
		}

		public Request.RequestBuilder param(String key, Object value) {
			if (Objects.isNull(params)) {
				this.params = new HashMap<>();
			}
			this.params.put(key, value);
			return this;
		}

		public Request.RequestBuilder header(String key, String value) {
			if (Objects.isNull(headers)) {
				this.headers = new HashMap<>();
			}
			this.headers.put(key, value);
			return this;
		}

		public Request build() {
			return new Request(this.url, this.headers, this.params, this.body);
		}

	}

}
