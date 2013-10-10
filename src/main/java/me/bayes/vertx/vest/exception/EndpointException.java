/**
 * 
 */
package me.bayes.vertx.vest.exception;

/**
 * An exception that occurs because of an endpoint. This can include all the codes
 * in the {@link Code} internal class.
 * 
 * @author Kevin Bayes
 *
 */
public class EndpointException extends Exception {

	
	private static final long serialVersionUID = 4591827022535104378L;
	
	private Code code;
	
	public EndpointException(Code code) {
		this.code = code;
	}
	
	public EndpointException(Code code, String message) {
		super(message);
		this.code = code;
	}
	
	public EndpointException(Code code, String message, Throwable throwable) {
		super(message, throwable);
		this.code = code;
	}
	
	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	
	/**
	 * Error codes.
	 * 
	 * @author Kevin Bayes
	 *
	 */
	public static enum Code {
		
		//10000 range is for headers.
		ACCEPT_NOT_SUPPORTED(10000),
		CONTENT_TYPE_NOT_SUPPORTED(10001);
		
		
		private int code;

		private Code(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
		
	}
}
