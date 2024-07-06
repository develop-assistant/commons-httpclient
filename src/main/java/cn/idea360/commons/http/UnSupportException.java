package cn.idea360.commons.http;

/**
 * @author cuishiying
 */
public class UnSupportException extends RuntimeException {

	/**
	 * 接口未实现异常
	 */
	public UnSupportException() {
		super("方法未实现");
	}

}
