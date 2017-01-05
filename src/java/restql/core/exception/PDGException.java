package restql.core.exception;

public class PDGException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PDGException(){

	}

	public PDGException(Throwable t) {
		super(t);
	}

	public PDGException(String cause) {
		super(cause);
	}

}
