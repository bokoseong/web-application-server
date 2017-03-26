package http;

import java.util.Map;

import util.HttpRequestUtils;

/**
 * @author hoseong
 */
public class HttpCookie {
	private Map<String, String> cookies;

	public HttpCookie(String cookieValue) {
		cookies = HttpRequestUtils.parseCookies(cookieValue);
	}

	public String getCookie(String name) {
		return cookies.get(name);
	}
}
