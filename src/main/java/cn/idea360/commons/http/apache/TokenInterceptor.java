package cn.idea360.commons.http.apache;

import cn.idea360.commons.http.token.TokenStore;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;

/**
 * @author cuishiying
 */
public class TokenInterceptor implements HttpRequestInterceptor {

	private final TokenStore tokenStore;

	/**
	 * token自动刷新拦截器
	 * @param tokenStore token存储
	 */
	public TokenInterceptor(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	@Override
	public void process(HttpRequest httpRequest, EntityDetails entityDetails, HttpContext httpContext)
			throws HttpException, IOException {
		httpRequest.setHeader("access-token", tokenStore.getToken());
	}

}
