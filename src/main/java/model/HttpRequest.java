package model;

import java.util.Map;

import com.google.common.collect.Maps;

import util.HttpRequestUtils;

/**
 * @author hoseong
 */
public class HttpRequest {
	private RequestLine requestLine;

	private Map<String, String> headers;

	private Map<String, String> bodyParamMap;

	public HttpRequest() {
		this.headers = Maps.newHashMap();
		this.bodyParamMap = Maps.newHashMap();
	}

	public RequestLine getRequestLine() {
		return requestLine;
	}

	public void setRequestLine(String requestLine) {
		this.requestLine = new RequestLine(requestLine);
	}

	public Map<String, String> getParamMap() {
		if (HttpMethod.GET.is(this)) {
			return requestLine.getParamMap();
		}

		return bodyParamMap;
	}

	public void setBodyParamMap(String bodyString) {
		this.bodyParamMap = HttpRequestUtils.parseQueryString(bodyString);
	}

	public void addHeaderItem(String key, String value) {
		this.headers.put(key, value);
	}

	public String getHeaderItem(String key) {
		return this.headers.get(key);
	}
}
