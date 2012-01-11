package open.ddo.android.persistencia;

import java.util.HashMap;

import open.ddo.android.persistencia.excecao.DAOException;

/**
 * Construtor/repositório das instâncias de <tt>MetaDadosEntidade</tt>.
 * @author ozairjr
 *
 */
public class MetadadosBuilder {
	
	// --------------------------------------------------------------
	// Atributos de classe
	// --------------------------------------------------------------
	
	/** O repositório de <tt>MetaDadosEntidade</tt>. */
	private static HashMap<Class<?>, MetaDadosEntidade<?>> repositorio
		= new HashMap<Class<?>, MetaDadosEntidade<?>>();
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/**
	 * Obtém a instância de <tt>MetaDadosEntidade</tt> conforme a classe
	 * informada.
	 * 
	 * @param classe
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> MetaDadosEntidade<T> getMetadadosEntidade(Class<T> classe) {
		
		if (classe == null) {
			throw new DAOException("N\u00e3o informada a classe da entidade.");
		}
		
		MetaDadosEntidade<T> metadados = null;
		if (!repositorio.containsKey(classe)) {
			metadados = new MetaDadosEntidade<T>(classe);
			if (metadados.nomeTabela != null
					&& metadados.colunas.length > 0
					&& metadados.getChaves() != null) {
				repositorio.put(classe, metadados);
			}
		} else {
			metadados = (MetaDadosEntidade<T>) repositorio.get(classe);
		}
		return metadados;
	}

	/**
	 * Verifica se a classe entidade está no repositório.
	 * @param classe
	 * @return
	 */
	public static boolean haClasse(Class<?> classe) {
		if (!repositorio.isEmpty()) {
			for (Class<?> c: repositorio.keySet()) {
				if (c == classe || c.equals(classe)) {
					return true;
				}
			}
		}
		return false;
	}

}
