package open.ddo.android.persistencia.excecao;

/**
 * Exceção do DAO.
 * 
 * @author ozairjr
 *
 */
public class DAOException extends RuntimeException {

	// --------------------------------------------------------------
	// Atributos de classe
	// --------------------------------------------------------------
	/**
	 * Serialização.
	 */
	private static final long serialVersionUID = 1L;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------

	public DAOException() {
		super();
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(Throwable cause) {
		super(cause);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
