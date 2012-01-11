package open.ddo.android.persistencia;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import open.ddo.android.persistencia.anotacao.Coluna;
import open.ddo.android.persistencia.anotacao.Data;
import open.ddo.android.persistencia.anotacao.Enumeracao;
import open.ddo.android.persistencia.anotacao.Tabela;
import open.ddo.android.persistencia.excecao.DAOException;
import open.ddo.android.util.FormatoData;
import open.ddo.android.util.ObjectUtil;

/**
 * Metadados da entidade que está anotada com @Tabela e @Coluna (pelo menos).
 * 
 * @author ozairjr
 *
 * @param <T>
 */
public class MetaDadosEntidade<T> {
	
	// --------------------------------------------------------------
	// Atributos do objeto
	// --------------------------------------------------------------
	
	/** Classe da entidade. */
	public final Class<T> classe;
	/** Nome da tabela. */
	public final String nomeTabela;
	/** Colunas . */
	public final AtributoEntidade[] colunas;
	/** As colunas que são chave. */
	public AtributoEntidade[] chaves;
	/** O nome das colunas. */
	private String[] nomeColunas=  null;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/**
	 * Construtor.
	 * @param classe Classe da entidade, deve estar anotada @Coluna
	 * em pelo menos um atributo.
	 */
	public MetaDadosEntidade(Class<T> classe) {
		this.classe = classe;
		this.nomeTabela = carregarNomeTabela(classe);
		this.colunas = carregarColunas(classe);
		if (colunas == null || colunas.length == 0) {
			throw new RuntimeException("Sem colunas para a tabela " + nomeTabela);
		}
		chaves = null;
	}
	
	/**
	 * Verifica se entidade possui chave auto-incremento.
	 * @return
	 */
	public boolean verificarChaveAutoIncremento() {
		return getChaveAutoIncremento() != null;
	}

	/**
	 * Se houver, obtém o campo que é a 'coluna' que é chave auto-incremento.
	 * @return
	 */
	public AtributoEntidade getChaveAutoIncremento() {
		ArrayList<AtributoEntidade> chavesAutoincremento = new ArrayList<AtributoEntidade>();
		for (AtributoEntidade obj: getChaves()) {
			if (obj.chavePrimariaAutoincremento()) {
				chavesAutoincremento.add(obj);
			}
		}
		if (chavesAutoincremento.isEmpty()) {
			return null;
		}
		if (chavesAutoincremento.size() > 1) {
			throw new DAOException(
					"Identificada mais de uma chave autoincremento para "
					+ nomeTabela);
		}
		return chavesAutoincremento.get(0);
	}
	
	/**
	 * Obtém o campo pelo nome da coluna.
	 * @param nomeColuna Nome da coluna.
	 * @return O campo desejado, ou <tt>null</tt> se não for encontrado.
	 */
	public AtributoEntidade getAtributoPelaColuna(String nomeColuna) {
		if (nomeColuna != null && nomeColuna.length() > 0) {
			for (AtributoEntidade f: colunas) {
				if (f.nomeColuna.equals(nomeColuna)) {
					return f;
				}
			}
		}
		return null;
	}
	
	/**
	 * Obtém os campos que são chaves primárias.
	 * @return
	 */
	public AtributoEntidade[] getChaves() {
		if (this.chaves == null) {
			ArrayList<AtributoEntidade> lista = new ArrayList<AtributoEntidade>();
			for (AtributoEntidade attr: colunas) {
				if (attr.chavePrimaria()) {
					lista.add(attr);
				}
			}
			chaves = lista.isEmpty() ?
					new AtributoEntidade[0]
					: lista.toArray(new AtributoEntidade[lista.size()]);
		}
		return chaves;
	}
	
	/**
	 * Obtém o nome da coluna conforme o nome do atributo informado.
	 * @param nomeAtributo
	 * @return
	 */
	public String obterNomeColuna(String nomeAtributo) {
		for (AtributoEntidade obj: colunas) {
			if (obj.atributo.getName().equals(nomeAtributo)) {
				return obj.nomeColuna;
			}
		}
		return null;
	}
	
	/**
	 * Obtém o nome das colunas.
	 * @return
	 */
	public String[] getNomeColunas() {
		if (nomeColunas == null) {
			ArrayList<String> nomeColunasLst = new ArrayList<String>();
			for (AtributoEntidade obj: colunas) {
				nomeColunasLst.add(obj.nomeColuna);
			}
			nomeColunas = nomeColunasLst.toArray(new String[nomeColunasLst.size()]);
		}
		return nomeColunas;
	}
	
	/**
	 * Obtém os nomes das colunas (separados por vírgulas)
	 * @return
	 */
	public String getColunasStr()  {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i < getNomeColunas().length; i++) {
			builder.append(nomeColunas[i]);
			if (i < nomeColunas.length-1) {
				builder.append(",");
			}
		}
		return builder.toString();
	}
	
	/**
	 * Verificar se o atributo é chave primária.
	 * @param atributo
	 * @return
	 */
	public static boolean verificarAtributoChave(AtributoEntidade atributo) {
		return atributo != null && atributo.chavePrimaria();
	}
	
	/**
	 * Extrai o campo para o formato que deve ser salvo no B.D.
	 * @param atributoEntidade
	 * @param obj
	 * @return
	 */
	public static Object extrairValor(AtributoEntidade atributoEntidade, Object obj) {
		
		Object valorOriginal = ObjectUtil.getValue(obj, atributoEntidade.atributo);
		Object resultado = null;
		
		if (valorOriginal != null) {
			resultado = valorOriginal;
			if (atributoEntidade.formato != null) {
				// Com base no formato atual (Atualizar conforme necessidade.
				if (atributoEntidade.atributo.getType().isEnum()) {
					Boolean naoOrdinal = Boolean.FALSE.equals(atributoEntidade.formato);
					Enum<?> e = (Enum<?>) valorOriginal;
					resultado = naoOrdinal ?
							e.name()
							: new Integer(e.ordinal());
				}
				else if (atributoEntidade.atributo.getType() == Date.class
						|| Date.class.equals(atributoEntidade.atributo.getType())) {
					Date d = (Date) valorOriginal;
					FormatoData fmt = FormatoData.LONG;
					if (atributoEntidade.formato instanceof FormatoData) {
						fmt = (FormatoData) atributoEntidade.formato;
					}
					switch (fmt) {
						case LONG:
							resultado = d.getTime();
						break;
						default:
							resultado = ObjectUtil.toString(d, fmt);
					}
				}
			}
		}
		return resultado; 
	}
	
	
	/**
	 * Atribui o valor (originário do banco) no atributo.
	 * @param f Atributo.
	 * @param obj Objeto destino.
	 * @param valor Valor (no formato do B.D.)
	 * @return
	 */
	public static Object atribuirValor(Field f, Object obj, Object valor) {
    	
    	if (obj == null) {
    		throw new RuntimeException("Objeto n\u00e3o informado");
    	}
    	if (f == null) {
    		throw new RuntimeException("Atributo n\u00e3o informado");
    	}
    	
    	Class<?> clazzField = f.getType();
    	if (!f.isAccessible()) {
    		f.setAccessible(true);
    	}
    	try {
			if (Number.class.isAssignableFrom(clazzField)) {
				String valueStr = valor != null ? valor.toString() : null;
				if (valueStr != null && valueStr.trim().length() == 0) {
					valueStr = null;
				}
				valor = valueStr == null ? 
						null
						: clazzField.getConstructor(String.class)
							.newInstance(valor.toString());;
			}
			else if (clazzField.isEnum()) {
				boolean enumeracaoNome = f.isAnnotationPresent(Enumeracao.class)
						&& !f.getAnnotation(Enumeracao.class).ordinal();
				valor = ObjectUtil.toEnum(clazzField, valor, !enumeracaoNome); 
			}
			else if (Date.class.isAssignableFrom(clazzField)) {
				FormatoData formato = FormatoData.LONG;
				if (f.isAnnotationPresent(Data.class)) {
					formato = f.getAnnotation(Data.class).formato();
				}
				valor = ObjectUtil.toDate(valor, formato);
			}
			f.set(obj, valor);
		} catch (Exception e) {
			throw new RuntimeException("Falha ao atribuir valor para "
					+ f.getName(),
					e);
		}
    	return valor;
    }
	
	/**
	 * Verifica se pelo menos um atributo da entidade possui a anotação
	 * <tt>@Coluna</tt>.
	 * 
	 * @param classe Classe entidade.
	 * @return <tt>true</tt> se há pelo menos um atributo com a anotação
	 * <tt>@Coluna</tt>.
	 */
	public static boolean haAnotacaoColuna(Class<?> classe){
		if (classe != null) {
			if (MetadadosBuilder.haClasse(classe)) {
				return true;
			}
			ArrayList<Field> atributos = ObjectUtil.getFields(classe);
			if (atributos != null && !atributos.isEmpty()) {
				for (Field f: atributos) {
					if (f.isAnnotationPresent(Coluna.class)) {
						return true;
					}
				}
			}
			
		}
		return false;
	}
	
	// --------------------------------------------------------------
	// Métodos auxiliares
	// --------------------------------------------------------------
	
	/**
	 * Obtém o valor do atributo no formato de String.
	 * @param f
	 * @param obj
	 * @return
	 */
	public static String obterValorStr(Field f, Object obj) {
		Object valor = ObjectUtil.getValue(obj, f);
		return valor != null ? valor.toString() : null;
	}
	
	/**
	 * Carrega o nome da tabela.
	 * @param classe
	 * @return
	 */
	private static String carregarNomeTabela(Class<?> classe) {
		String nomeTabela = classe.isAnnotationPresent(Tabela.class) ?
				classe.getAnnotation(Tabela.class).nome()
				: null;
		if (nomeTabela == null || nomeTabela.trim().length() == 0) {
			nomeTabela = classe.getSimpleName();
		}
		return nomeTabela;
	}
	
	private static AtributoEntidade[] carregarColunas(Class<?> classe) {
		ArrayList<Field> atributos = ObjectUtil.getFields(classe);
		ArrayList<AtributoEntidade> colunas = 
				new ArrayList<AtributoEntidade>(atributos != null ? atributos.size() : 0);
		if (atributos != null && !atributos.isEmpty()) {
			// Varrendo em busca da anotação '@Coluna'
			for (Field f: atributos) {
				if (f.isAnnotationPresent(Coluna.class)) {
					colunas.add(new AtributoEntidade(f));
				}
			}
				
		}
		return colunas.isEmpty() ? 
				new AtributoEntidade[0]
				: colunas.toArray(new AtributoEntidade[colunas.size()]);
	}

//	/**
//	 * Obtém a chave que será 'id' (utilizado em pesquisas).
//	 * @return
//	 */
//	public String getChaveId() {
//		StringBuilder builder = new StringBuilder();
//		for (int i = 0; i < getChaves().length; i++){
//			builder.append(chaves[i].nomeColuna);
//			if (i < (chaves.length -1)) {
//				builder.append(" || '_' || ");
//			}
//		}
//		return builder.toString();
//	}

}
