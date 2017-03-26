package http;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author hoseong
 */
public class HttpSessions {
	private static Map<String, HttpSession> httpSessions = Maps.newHashMap();;

	public static HttpSession getHttpSession(String sessionId) {
		HttpSession httpSession = httpSessions.get(sessionId);

		if (httpSession != null) {
			return httpSession;
		}

		httpSession = new HttpSession(sessionId);
		httpSessions.put(sessionId, httpSession);
		return httpSession;
	}

	public static void remove(String sessionId) {
		httpSessions.remove(sessionId);
	}
}
