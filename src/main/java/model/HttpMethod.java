package model;

/**
 * @author hoseong
 */
public enum HttpMethod {
	GET("GET"),

	POST("POST");

	private String code;

	HttpMethod(String code) {
		this.code = code;
	}

	public Boolean is(HttpRequest httpRequest) {
		return this.code.equals(httpRequest.getRequestLine().getHttpMethod());
	}
}
