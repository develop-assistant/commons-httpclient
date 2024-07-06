package cn.idea360.commons.http;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author cuishiying
 */
@Builder
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

}
