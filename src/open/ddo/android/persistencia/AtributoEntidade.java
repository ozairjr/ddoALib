package open.ddo.android.persistencia;

import java.lang.reflect.Field;
import java.util.Date;

import open.ddo.android.persistencia.anotacao.Chave;
import open.ddo.android.persistencia.anotacao.Coluna;
import open.ddo.android.persistencia.anotacao.Data;
import open.ddo.android.persistencia.anotacao.Enumeracao;
import open.ddo.android.util.FormatoData;

/**
 * Atributo da entidade (classe) que foi identificado com a
 * anotação <tt>@Coluna</tt>.
 *  
 * @author ozairjr
 *
 */
public class AtributoEntidade {
	
	// --------------------------------------------------------------
	// Atributos do objeto
	// --------------------------------------------------------------
	
	/** Atributo do objeto anotado com <tt>@Coluna</tt>. */
	public final Field atributo;
	/** Nome da coluna. */
	public final String nomeColuna;
	/** Identifica se é chave primária. Possíveis valores são:
	 * <ul>
	 *   <li>0: Não é chave primária.</li>
	 *   <li>1: É chave primária.</li>
	 *   <li>2: É chave primária autoincremento. </li>
	 * </ul> 
	 */
	public final byte chavePrimaria;
	/** Formato do campo.<br/>
	 *  Se for enumeração então é <tt>true</tt>,
	 * se é para considerar como ordinal, senão é <tt>false</tt>, 
	 * para considerar como string (<tt>Enum.name()</tt>).<br/>
	 * Se for data, é uma instância de <tt>FormatoData</tt>. */
	public final Object formato;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------

	/**
	 * Construtor.
	 * @param f
	 */
	public AtributoEntidade(Field f) {
		this.atributo = f;
		this.nomeColuna = obterNomeColuna(f);
		Chave chave = obterAnotacaoChave(f);
		chavePrimaria = (byte) (chave != null ?
				(chave.autoincremento() ? 2 : 1)
				: 0);
		formato = obterFormato(f);
	}
	
	/**
	 * Verifica se o atributo é chave primária.
	 * @return
	 */
	public boolean chavePrimaria() {
		return chavePrimaria > 0;
	}
	
	/**
	 * Confirma se o atributo é chave autoincremento.
	 * @return
	 */
	public boolean chavePrimariaAutoincremento() {
		return chavePrimaria == 2;
	}
	
	/**
	 * Obtém o nome da coluna do atributo.
	 * @param f
	 * @return
	 */
	public static String obterNomeColuna(Field f) {
		String nomeColuna = f.getName();
		if (f.isAnnotationPresent(Coluna.class)) {
			String temp = f.getAnnotation(Coluna.class).nome();
			if (temp != null && temp.length() > 0) {
				nomeColuna = temp;
			}
		}
		return nomeColuna;
	}
	
	/**
	 * Acessa a anotação <tt>@Chave<tt> do atribuo informado.
	 * @param f
	 * @return
	 */
	public static Chave obterAnotacaoChave(Field f) {
		return f.isAnnotationPresent(Chave.class) ?
				f.getAnnotation(Chave.class)
				: null;
	}
	
	/**
	 * Obtém o formato do atributo.
	 * @param f
	 * @return
	 */
	private static Object obterFormato(Field f) {
		Object formato = null;
		if (f.getType().isEnum()) {
			formato = (f.isAnnotationPresent(Enumeracao.class)
					&& Boolean.TRUE.equals(f.getAnnotation(Enumeracao.class).ordinal()))
					|| (!f.isAnnotationPresent(Enumeracao.class));
		} else if (Date.class.equals(f.getType())) {
			formato = f.isAnnotationPresent(Data.class) ?
					f.getAnnotation(Data.class).formato()
					: null;
			if (formato == null) {
				formato = FormatoData.LONG;
			}
		}
		return formato;
	}
	
	@Override
	public int hashCode() {
		return atributo != null ? atributo.hashCode() : 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AtributoEntidade) {
			AtributoEntidade outro = (AtributoEntidade) o;
			return (this.atributo == outro.atributo)
					|| this.atributo.equals(outro.atributo);
		}
		return false;
	}
}
