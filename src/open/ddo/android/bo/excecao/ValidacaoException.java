package open.ddo.android.bo.excecao;

/**
 * Exceção de validação. 
 * @author ozairjr
 *
 */
public class ValidacaoException extends RuntimeException {
	
	// --------------------------------------------------------------
	// Atributos de classe.
	// --------------------------------------------------------------

	/**
	 * Serialização.
	 */
	private static final long serialVersionUID = 1L;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------

	public ValidacaoException() {
		super();
	}

	public ValidacaoException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ValidacaoException(String detailMessage) {
		super(detailMessage);
	}

	public ValidacaoException(Throwable throwable) {
		super(throwable);
	}

}
