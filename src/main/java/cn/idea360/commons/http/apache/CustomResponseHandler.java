package cn.idea360.commons.http.apache;

import cn.idea360.commons.http.Response;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author cuishiying
 */
public class CustomResponseHandler implements HttpClientResponseHandler<Response> {

	@Override
	public Response handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
		int statusCode = response.getCode();
		String reasonPhrase = response.getReasonPhrase();
		String responseBody = Objects.isNull(response.getEntity()) ? null : EntityUtils.toString(response.getEntity());
		return new Response(statusCode, reasonPhrase, responseBody);
	}

}
