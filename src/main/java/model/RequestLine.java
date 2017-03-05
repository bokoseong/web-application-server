package model;

import java.util.Map;

import util.HttpRequestUtils;

/**
 * @author hoseong
 */
public class RequestLine {
	private String httpMethod;

	private String path;

	private Map<String, String> paramMap;

	RequestLine(String requestLine) {
		String[] requestInfo = requestLine.split(" ");

		this.httpMethod = requestInfo[0];
		this.path = requestInfo[1];

		int index = this.path.indexOf("?");

		if (index >= 0) {
			this.paramMap = HttpRequestUtils.parseQueryString(this.path.substring(index + 1));
		}
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}
}
