package cn.idea360.commons.http.token;

/**
 * @author cuishiying
 */
public interface TokenStore {

	/**
	 * 获取token
	 * @return token
	 */
	String getToken();

	/**
	 * 使token失效
	 */
	void invalidate();

}
