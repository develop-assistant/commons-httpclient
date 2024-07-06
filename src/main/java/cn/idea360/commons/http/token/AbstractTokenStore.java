package cn.idea360.commons.http.token;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * @author cuishiying
 */
@Slf4j
public abstract class AbstractTokenStore implements TokenStore {

	@Setter
	private String token;

	@Setter
	private Long expiryTime = 0L;

	@Override
	public String getToken() {
		if (isTokenExpired(token)) {
			refreshToken();
		}
		return token;
	}

	/**
	 * 判断token是否过期(提前5min)
	 * @param token token
	 * @return true过期, false有效
	 */
	protected boolean isTokenExpired(String token) {
		return token == null || expiryTime - Instant.now().getEpochSecond() <= 300;
	}

	@Override
	public void invalidate() {
		this.token = null;
		this.expiryTime = 0L;
	}

	/**
	 * 请求token, 并刷新token和expiryTime
	 */
	protected abstract void refreshToken();

}
