package open.ddo.android.persistencia;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

/**
 * DAO de entidade.
 * @author ozairjr
 *
 * @param <T>
 */
public class EntidadeDAO<T> extends AbstractDAO {
	
	// --------------------------------------------------------------
	// Atributos do objeto
	// --------------------------------------------------------------
	
	/** Metadados da classe entidade. */
	protected final MetaDadosEntidade<T> metadados;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/**
	 * Construtor.
	 * @param classe
	 */
	public EntidadeDAO(Class<T> classe) {
		this(classe, null) ;
	}
	
	/**
	 * Construtor com o 'gerenciador' do banco de dados.
	 * @param classe
	 * @param bd
	 */
	public EntidadeDAO(Class<T> classe, SQLiteDatabase bd) {
		super(bd);
		metadados = new MetaDadosEntidade<T>(classe);
	}
	
	@Override
	public long inserir(Object obj) {
		return inserir(obj, metadados);
	}

	@Override
	public int atualizar(Object obj) {
		return atualizar(obj, metadados);
	}
	
	@Override
	public int remover(Object obj) {
		return remover(obj, metadados);
	}
	
	/**
	 * Pesquisa pela chave.
	 * @param chave Objecto da chave. Pode ser:
	 * <ul>
	 *   <li>Uma instância do tipo da chave (quando a chave for única)</li>
	 *   <li>Uma instância da entidade</li>
	 * </ul>
	 * @return
	 */
	public T pesquisarPelaChave(Object chave) {
		return pesquisarPelaChave(metadados, chave);
	}
	
	/**
	 * Pesquisa por todos.
	 * @return
	 */
	public ArrayList<T> pesquisar() {
		return pesquisar(metadados, null);
	}
	
	/**
	 * Pesquisa entidade com cláusula 'where'.<br/>
	 * A consulta seria:<br/>
	 * 
	 * <blockquote>
	 * <tt>
	 * select *<br/> 
	 * from <b>[tabela entidade]</b><br/>
	 * where <b>[clausulaWhere]</b><br/>	 
	 * </tt>
	 * </blocquote>
	 * 
	 * @param clausulaWhere Cláusula where (pode ser nula).
	 * @param args Argumentos da cláusula
	 * @return Lista com o resultado da pesquisa.
	 */
	public ArrayList<T> pesquisar(String clausulaWhere, Object... args) {
		return pesquisar(metadados, clausulaWhere, args);
	}

}
