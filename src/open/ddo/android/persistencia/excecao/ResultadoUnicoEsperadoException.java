package open.ddo.android.persistencia.excecao;

/**
 * Exceção lançada quando se excecuta uma consulta a qual se espera
 * um resultado único (e.g., apenas uma linha do cursor).
 * 
 * @author ozairjr
 *
 */
public class ResultadoUnicoEsperadoException extends DAOException {

	private static final long serialVersionUID = 1L;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	public ResultadoUnicoEsperadoException(String msg) {
		super(msg);
	}

}
