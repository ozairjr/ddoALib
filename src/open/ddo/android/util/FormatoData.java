package open.ddo.android.util;


/**
 * Tipo (formato) que a data será armazenada no banco de dados.
 * @author ozairjr
 *
 */
public enum FormatoData {
	
	// --------------------------------------------------------------
	// 'Constantes'
	// --------------------------------------------------------------
	
	/** Tipo long (tempo em milissegundos, <tt>java.util.Date.getTime()</tt> */
	LONG(null),
	/** Tipo String no formato 'yyyyMMdd'. */
	DATA_STR_CURTA("yyyyMMdd"),
	/** Tipo String no formato 'yyyyMMaaaaHHmmss'. */
	DATA_STR_LONGA("yyyyMMddHHmmss"),
	/** Tipo String no formato 'HHmmss'. */
	HORA_STR("HHmmss")
	;
	
	// --------------------------------------------------------------
	// Atributos
	// --------------------------------------------------------------

	/** Formato da data. */
	public final String formato;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/** Construtor. */
	private FormatoData(String fmt) {
		this.formato = fmt;
	}
	
}
