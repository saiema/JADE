package jade.exceptions;

/**
 * This exception is used when an error occurred inside an AST class
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 */
public class ASTInternalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1264391049617048735L;

	public ASTInternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ASTInternalException(String message) {
		super(message);
	}

	public ASTInternalException(Throwable cause) {
		super(cause);
	}
	
	

}
