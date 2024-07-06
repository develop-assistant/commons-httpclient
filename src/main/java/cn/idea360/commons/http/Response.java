package cn.idea360.commons.http;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cuishiying
 */
@AllArgsConstructor
@Data
public class Response implements Serializable, AutoCloseable {

	/**
	 * http code
	 */
	private int statusCode;

	/**
	 * http code reason
	 */
	private String reasonPhrase;

	/**
	 * response data
	 */
	private String body;

	@Override
	public void close() throws Exception {

	}

}
