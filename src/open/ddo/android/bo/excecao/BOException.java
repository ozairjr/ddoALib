package open.ddo.android.bo.excecao;

/**
 * Exceção de regra de negócio.
 * 
 * @author ozairjr
 *
 */
public class BOException extends RuntimeException {
	
	// --------------------------------------------------------------
	// Atributos de classe
	// --------------------------------------------------------------
	
	/**
	 *  Serialização.
	 */
	private static final long serialVersionUID = 1L;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------

	public BOException() {
	}

	public BOException(String detailMessage) {
		super(detailMessage);
	}

	public BOException(Throwable throwable) {
		super(throwable);
	}

	public BOException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
