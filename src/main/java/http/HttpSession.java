package http;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author hoseong
 */
public class HttpSession {
	private String id;

	private Map<String, Object> attributes;

	public HttpSession(String sessionId) {
		this.id = sessionId;
		this.attributes = Maps.newHashMap();
	}

	/**
	 * 현재 세션에 할당되어 있는 고유한 세션 아이디를 반환
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 현재 세션에 name 인자로 전달되는 객체 값을 찾아 반환
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * 현재 세션에 value 인자로 전달되는 객체를 name 인자 이름으로 저장
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	/**
	 * 현재 세션에 name 인자로 저장되어 있는 객체 값을 삭제
	 * @param name
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	/**
	 * 현재 세션에 저장되어 있는 모든 값을 삭제
	 */
	public void invalidate() {
		HttpSessions.remove(id);
	}
}
