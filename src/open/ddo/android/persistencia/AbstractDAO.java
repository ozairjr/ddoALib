package open.ddo.android.persistencia;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import open.ddo.android.persistencia.excecao.DAOException;
import open.ddo.android.persistencia.excecao.ResultadoUnicoEsperadoException;
import open.ddo.android.util.FormatoData;
import open.ddo.android.util.ObjectUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Classe "abstrata" para DAO.
 * 
 * @author ozairjr
 * 
 */
public class AbstractDAO {

	// --------------------------------------------------------------
	// Atributos do objeto
	// --------------------------------------------------------------

	/** 'Gerenciador' para o banco de dados do Android. */
	protected SQLiteDatabase bd;

	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/**
	 * Construtor padrão.
	 */
	public AbstractDAO() {
		this(null);
	}

	/**
	 * Construtor.
	 * @param bd Banco de dados. Se não for informado
	 * será carregado via <tt>SQLiteUtil.getBD()</tt>.
	 */
	public AbstractDAO(SQLiteDatabase bd) {
		if (bd == null) {
			bd = SQLiteUtil.getBD();
		}
		this.bd = bd;
	}
	
	// --------------------------------------------------------------
	// Métodos públicos
	// --------------------------------------------------------------

	/**
	 * Inicia transação.
	 */
	public void iniciarTransacao() {
		bd.beginTransaction();
	}

	/**
	 * 'Commit' na transação.
	 */
	public void commit() {
		bd.setTransactionSuccessful();
	}

	/**
	 * Finaliza a transação.
	 */
	public void finalizarTransacao() {
		bd.endTransaction();
	}

	/**
	 * Insere o objeto no banco de dados.<br/>
	 * 
	 * <b>Nota</b>: O objeto deve estar anotado com <tt>@Tabela</tt> (opcional) 
	 * e <tt>@Coluna</tt> (obrigatório) corretamente.
	 * 
	 * @param obj Ojeto entidade a ser inserido.
	 */
	public long inserir(Object obj) {
		if (obj == null) {
			throw new DAOException(
					"Recurso (objeto) n\u00e3o informado para ser salvo.");
		}
		MetaDadosEntidade<?> metadados = getMetaDadosEntidade(obj); 
		return inserir(obj, metadados);
	}
	
	/**
	 * Inserir 'nativo' do Android.
	 * @param nomeTabela Nome da tabela.
	 * @param colunaNula As colunas.
	 * @param valores Os valores.
	 */
	public long inserir(String nomeTabela, String colunaNula, ContentValues valores) {
		long ret = bd.insert(nomeTabela, colunaNula, valores);
		if (ret == -1) {
			throw new DAOException("Falha ao inserir o registro.");
		}
		return ret;
	}

	/**
	 * Atualiza o objeto no banco de dados.<br/>
	 * 
	 * <b>Nota</b>: O objeto deve estar anotado com <tt>@Tabela</tt> (opcional) 
	 * e <tt>@Coluna</tt> (obrigatório) corretamente.
	 * 
	 * @param obj Objeto a ser atualizado.
	 * 
	 * @return Quantidade de linhas atualizadas.
	 */
	public int atualizar(Object obj) {
		if (obj == null) {
			throw new DAOException("Sem dados para serem salvos.");
		}
		MetaDadosEntidade<?> metadados = getMetaDadosEntidade(obj);
		return atualizar(obj, metadados);
	}

	/**
	 * Atualizar 'nativo' do B.D.
	 * @param nomeTabela Nome da tabela.
	 * @param valores Valores a serem inseridos.
	 * @param clausulaWhere Cláusula where.
	 * @param argsWhere Argumentos da cláusula where.
	 * @return
	 */ 
	public int atualizar(
			String nomeTabela, 
			ContentValues valores, 
			String clausulaWhere, 
			String[] argsWhere) {
		return bd.update(nomeTabela, valores, clausulaWhere, argsWhere);
	}
	
	/**
	 * Insere ou atualiza o objeto (Para determinar se irá inserir ou atualizar 
	 * a entidade, faz-se uma consulta com base na chave).
	 * 
	 * <b>Nota</b>: O objeto deve estar anotado com <tt>@Tabela</tt> (opcional) 
	 * e <tt>@Coluna</tt> (obrigatório) corretamente.
	 * 
	 * @param obj Objeto a ser inserido ou atualizado.
	 * 
	 * @return
	 */
	public long inserirOuAtualizar(Object obj) {
		if (obj == null) {
			throw new DAOException(
					"Recurso (objeto) n\u00e3o informado para ser salvo.");
		}
		MetaDadosEntidade<?> metadados = getMetaDadosEntidade(obj);
		boolean atualizar = contarPelaChave(metadados, obj) > 0;
		return (atualizar) ?
			atualizar(obj, metadados)
			: inserir(obj, metadados);
		}
	
	/**
	 * Remove o objeto no banco de dados.<br/>
	 * 
	 * <b>Nota</b>: O objeto deve estar anotado com <tt>@Tabela</tt> (opcional) 
	 * e <tt>@Coluna</tt> (obrigatório) corretamente.
	 * 
	 * @param obj Objeto a ser removido.
	 * 
	 * @return Quantidade de linhas removidas.
	 */
	public int remover(Object obj) {
		if (obj == null) {
			throw new DAOException("Sem informa\u00e7\u00e3o para remover.");
		}
		MetaDadosEntidade<?> metadados = getMetaDadosEntidade(obj);
		return remover(obj, metadados);
	}
	
	/**
	 * Remover 'nativo'.
	 * @param nomeTabela Nome da tabela.
	 * @param clausulaWhere Cláusula where.
	 * @param argsWhere Argumentos do where.
	 * @return
	 */
	public int remover(String nomeTabela, String clausulaWhere, String[] argsWhere) {
		return bd.delete(nomeTabela, clausulaWhere, argsWhere);
	}

	/**
	 * Executa consulta SQL (SQL normal).
	 * @param classeResultado Classe resultado. Os elementos da lista
	 * serão do tipo informado, que podem ser:
	 * <ul>
	 *   <li>Boolean</li>
	 *   <li>Short</li>
	 *   <li>Integer</li>
	 *   <li>Long</li>
	 *   <li>Float</li>
	 *   <li>Double</li>
	 *   <li>BigDecimal</li>
	 *   <li>String</li>
	 *   <li>String[]</li>
	 *   <li>Classe entidade anotada com <tt>@Tabela</tt> e <tt>@Coluna</tt>.
	 * </ul>
	 * @param sql Consulta SQL 'nativa'.
	 * @param args Argumentos da consulta (do parâmetros informados com '?').
	 * @return Lista de instâncias de <tt>classeResultado</tt>.
	 */
	public <T> ArrayList<T> pesquisarSQL(Class<T> classeResultado, String sql,
			Object... args) {
		
		String[] argumentos = obterArgumentos(null, null, args);
		
		Cursor cursor = bd.rawQuery(sql, argumentos);

		try {
			ArrayList<T> lista = carregarResultadoLista(classeResultado, cursor);
			return lista;
		} finally {
			SQLiteUtil.fechar(cursor); 
		}
	}

	/**
	 * Pesquisa com base na classe informada. A pesquisa seria:<br/>
	 * 
	 * <blockquote>
	 * <tt>select * from <@Tabela.nome> where <<tt>clausulaWhere</tt>>'
	 * </blockquote>
	 * 
	 * Exemplo de chamada:
	 * 
	 * <blockquote>
	 * <tt>AbstractDAO dao = ...;<br/>
	 * dao.pesquisar(<br/>
	 * &nbsp;&nbsp;ClassEntidade.class,<br/>
	 * &nbsp;&nbsp;"atributo1 = ? and atributo2 >= ?",<br/>
	 * &nbsp;&nbsp;1, new Date());
	 * </tt>
	 * </blockquote>
	 * 
	 * @param classe Classe da entidade (Deve estar anotada com @Tabela e @Coluna).
	 * @param clausulaWhere Cláusula 'where', sendo que os parâmetros podem ser 
	 * informados com os nomes dos atributos anotado com @Coluna da classe entidade.
	 * @param args Argumentos do where.
	 * @return Lista de.
	 */
	public <T> ArrayList<T> pesquisar(
			Class<T> classe, 
			String clausulaWhere,
			Object... args) {

		@SuppressWarnings("unchecked")
		MetaDadosEntidade<T> metadados = (MetaDadosEntidade<T>) getMetaDadosEntidade(classe);
		return pesquisar(metadados, clausulaWhere, args);
	}
	
	/**
	 * Consulta SQL (quase) 'nativa' do Android.
	 * @param metadados Entidade 'resultante'.
	 * @param where Cláusula where.
	 * @param whereArgs Argumentos do where.
	 * @param groupBy Cláusula groupBy.
	 * @param having Cláusula having.
	 * @param orderBy Cláusula orderBy.
	 * @return Lista do tipo 'metadados.classe'.
	 */
	public <T> ArrayList<T> consultar(
			MetaDadosEntidade<T> metadados,
			String where, 
			String[] whereArgs, 
			String groupBy, 
			String having,
			String orderBy) {
		
		Cursor cursor = 
				bd.query(
				metadados.nomeTabela,
				metadados.getNomeColunas(), 
				where, 
				whereArgs, 
				groupBy, // group
				having, // having
				orderBy, // order by
				null); // no limit
		try {
			ArrayList<T> lista = carregarResultadoLista(metadados.classe, cursor);
			return lista;
		} finally {
			SQLiteUtil.fechar(cursor);
		}
	}
	
	/**
	 * Consulta 'nativa' do Android.
	 * @param nomeTabela Nome da tabela.
	 * @param nomeColunas Nome das colunas.
	 * @param where Cláusula where.
	 * @param whereArgs Argumentos do where.
	 * @param groupBy Cláusula groupBy.
	 * @param having Cláusula having.
	 * @param orderBy Cláusula orderBy.
	 * @param limite Limita a quantidade de linhas retornada
	 * pela pesquisa. Se for <tt>null</tt> então siginifica
	 * que não há limite (trará todas as linhas).
	 * @return
	 */
	public Cursor consultar(
			String nomeTabela,
			String[] nomeColunas,
			String where,
			String[] whereArgs,
			String groupBy,
			String having,
			String orderBy,
			String limite) {
		
		return bd.query(
				nomeTabela,
				nomeColunas, 
				where, 
				whereArgs, 
				groupBy, // group
				having, // having
				orderBy, // order by
				limite); // no limit
	}
	
	/**
	 * Pesquisa por único resultado em um consulta SQL.
	 * @param classeResultado Classe/Tipo de retorno esperado.
	 * @param sql SQL.
	 * @param args Argumentos.
	 * @return Objeto retorno encontrado ou <tt>null</tt>.
	 */
	public <T> T pesquisarSQLResultadoUnico(Class<T> classeResultado, String sql, Object... args) {
		
		ArrayList<T> lista = pesquisarSQL(classeResultado, sql, args);
		return obterResultadoUnico(lista);
	}
	
	/**
	 * Pesquisa por único resultado 
	 * @param classe
	 * @param clausulaWhere
	 * @param args
	 * @return
	 */
	public <T> T pesquisarResultadoUnico(Class<T> classe, String clausulaWhere, Object... args) {
		ArrayList<T> lista = pesquisar(classe, clausulaWhere, args);
		return obterResultadoUnico(lista);
	}
	
	/**
	 * Consulta SQL 'nativa' do Android por um resultado único.
	 * @param metadados Entidade 'resultante'.
	 * @param where Cláusula where.
	 * @param whereArgs Argumentos do where.
	 * @param groupBy Cláusula groupBy.
	 * @param having Cláusula having.
	 * @param orderBy Cláusula orderBy.
	 * @return Resultado único.
	 */
	public <T> T consultarResultadoUnico(
			MetaDadosEntidade<T> metadados,
			String where, 
			String[] whereArgs, 
			String groupBy, 
			String having,
			String orderBy) {
		
		ArrayList<T> lista = consultar(metadados, where, whereArgs, groupBy, having, orderBy);
		return obterResultadoUnico(lista);
	}
	
	/**
	 * Conta quantos registros há no B.D. com a chave informada.
	 * (<tt>select count(*) from <tabelaEntidade> where <chave = ?></tt>)
	 * @param metadados Metadados da entidade.
	 * @param chave 'Chave' (veja <tt>pesquisarPelaChave(Class<?>, Object)</tt>).
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected int contarPelaChave(MetaDadosEntidade<?> metadados, Object chave) {
		if (metadados == null) {
			throw new DAOException("Sem classe entidade para ser identificada.");
		}
		if (chave == null) {
			throw new DAOException("Chave n\u00e3o informada.");
		}
		
		StringBuilder clausulaWhere = new StringBuilder();
		int conta = metadados.getChaves().length;
		// Extraindo where de acordo com as chaves
		for (AtributoEntidade atributo: metadados.chaves) {
					
			if (atributo.chavePrimaria()) {
				clausulaWhere.append(atributo.nomeColuna);
				clausulaWhere.append(" = ?");
				if ((--conta) > 0) {
					clausulaWhere.append(" and ");
				}
			}
		} // fim for
		conta = metadados.chaves.length;
		ArrayList<String> valores = new ArrayList<String>();
		if ((chave instanceof Long
			|| chave instanceof Integer
			|| chave instanceof Short
			|| chave instanceof Byte
//			|| chave instanceof Date // TODO Implementar/Ajustar
			|| chave instanceof String)
			) {
			if (conta != 1) {
				throw new DAOException("Chave prim\u00e1ria n\u00e3o possui apenas um valor.");
			}
			valores.add(chave.toString());
		}
		else if ((chave instanceof Map<?, ?>)) {
			Map<String, Object> mapa = null;
			try {
				mapa = (Map<String,Object>) chave;
			} catch (Exception e) {
				throw new DAOException("Tipo de mapa para chave inv\u00e1lido.", e);
			}
			String valor;
			for (AtributoEntidade coluna: metadados.chaves) {
				// No mapa, pode ser tanto o nome do atributo com o nome da coluna.
				if (mapa.containsKey(coluna.atributo.getName())) {
					valor = mapa.get(coluna.atributo.getName()).toString();
				}
				else if (mapa.containsKey(coluna.nomeColuna)) {
					valor = mapa.get(coluna.nomeColuna).toString();
				}
				else {
					throw new DAOException("Mapa n\u00e3o contém a chave (nome: '"
							+ coluna.atributo.getName()
							+ "').");
				}
				
				if (valor == null) {
					throw new DAOException("Mapa n\u00e3o contém o valor da coluna '"
							+ coluna.atributo.getName()
							+ "'.");
				}
				valores.add(valor);
			} // fim for
		}
		else {
			// Com base no objeto, deve-se conter atributos com o mesmo nome
			// da classe.
			String valor;
			Field f;
			final boolean ehEntidade = metadados.classe == chave.getClass() 
					|| metadados.classe.equals(chave.getClass());
			
			for (AtributoEntidade coluna: metadados.chaves) {
				
				f = ehEntidade ? 
						coluna.atributo
						: ObjectUtil.getField(chave.getClass(), coluna.atributo.getName()); 
				
				if (f == null){
					throw new DAOException("O tipo da chave '"
							+ chave.getClass().getName()
							+ " n\u00e3o possui o atributo '"
							+ coluna.atributo.getName()
							+ "'.");
				}
				
				valor = ObjectUtil.toString(
						ObjectUtil.getValue(chave, f),
						coluna.formato);
				valores.add(valor);
			}
		}
		Integer resultado = pesquisarSQLResultadoUnico(
				Integer.class,
				String.format("select count(*) from %s where %s",
						metadados.nomeTabela,
						clausulaWhere),
				valores.toArray());
				
		return resultado == null ? 0 : resultado.intValue();
	}
	
	/**
	 * Pequisa pela chave.
	 * @param classeEntidade Classe entidade.
	 * @param chave Chave a ser pesquisada. Podendo se do 'tipo' da chave, o 
	 * próprio objeto entidade, ou uma classe que possua as mesmas colunas que sejam
	 * chave.
	 * @return
	 */
	public <T> T pesquisarPelaChave(Class<T> classeEntidade, Object chave) {
		
		MetaDadosEntidade<T> metadados = MetadadosBuilder.getMetadadosEntidade(classeEntidade);
		
		return pesquisarPelaChave(metadados, chave);
		
	}
	
	/**
	 * Pesquisa pela chave.
	 * @param metadados Metadados da entidade.
	 * @param chave Objeto chave, podendo serdo tipo da chave,
	 * o próprio objeto entidade ou uma classe que possua os mesmos
	 * atributos da chave.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T pesquisarPelaChave(MetaDadosEntidade<T> metadados, Object chave) {
		if (metadados == null) {
			throw new DAOException("Sem classe entidade para ser identificada.");
		}
		if (chave == null) {
			throw new DAOException("Chave n\u00e3o informada.");
		}
		
		StringBuilder clausulaWhere = new StringBuilder();
		int conta = metadados.getChaves().length;
		// Extraindo where de acordo com as chaves
		for (AtributoEntidade atributo: metadados.chaves) {
			
			if (atributo.chavePrimaria()) {
				clausulaWhere.append(atributo.nomeColuna);
				clausulaWhere.append(" = ?");
				if ((--conta) > 0) {
					clausulaWhere.append(" and ");
				}
			}
		} // fim for
		conta = metadados.chaves.length;
		ArrayList<String> valores = new ArrayList<String>();
		if ((chave instanceof Long
			|| chave instanceof Integer
			|| chave instanceof Short
			|| chave instanceof Byte
//			|| chave instanceof Date // TODO Implementar/Ajustar
			|| chave instanceof String)
			) {
			if (conta != 1) {
				throw new DAOException("Chave prim\u00e1ria n\u00e3o possui apenas um valor.");
			}
			valores.add(chave.toString());
		}
		else if ((chave instanceof Map<?, ?>)) {
			Map<String, Object> mapa = null;
			try {
				mapa = (Map<String,Object>) chave;
			} catch (Exception e) {
				throw new DAOException("Tipo de mapa para chave inv\u00e1lido.", e);
			}
			String valor;
			for (AtributoEntidade coluna: metadados.chaves) {
				// No mapa, pode ser tanto o nome do atributo com o nome da coluna.
				if (mapa.containsKey(coluna.atributo.getName())) {
					valor = mapa.get(coluna.atributo.getName()).toString();
				}
				else if (mapa.containsKey(coluna.nomeColuna)) {
					valor = mapa.get(coluna.nomeColuna).toString();
				}
				else {
					throw new DAOException("Mapa n\u00e3o contém a chave (nome: '"
							+ coluna.atributo.getName()
							+ "').");
				}
				
				if (valor == null) {
					throw new DAOException("Mapa n\u00e3o contém o valor da coluna '"
							+ coluna.atributo.getName()
							+ "'.");
				}
				valores.add(valor);
			} // fim for
		}
		else {
			// Com base no objeto, deve-se conter atributos com o mesmo nome
			// da classe.
			String valor;
			Field f;
			final boolean ehEntidade = metadados.classe == chave.getClass() 
					|| metadados.classe.equals(chave.getClass());
			
			for (AtributoEntidade coluna: metadados.chaves) {
				
				f = ehEntidade ? 
						coluna.atributo
						: ObjectUtil.getField(chave.getClass(), coluna.atributo.getName()); 
				
				if (f == null){
					throw new DAOException("O tipo da chave '"
							+ chave.getClass().getName()
							+ " n\u00e3o possui o atributo '"
							+ coluna.atributo.getName()
							+ "'.");
				}
				
				valor = ObjectUtil.toString(
						ObjectUtil.getValue(chave, f),
						coluna.formato);
				valores.add(valor);
			}
		}
		
		T procurado = consultarResultadoUnico(
				metadados,
				clausulaWhere.toString(),
				valores.toArray(new String[valores.size()]),
				null,
				null,
				null);
				
		return procurado;
	}
	
	// --------------------------------------------------------------
	// Métodos auxiliares
	// --------------------------------------------------------------
	
	protected static <T> T obterResultadoUnico(ArrayList<T> lista) {
		if (lista == null || lista.isEmpty()) {
			return null;
		}
		if (lista.size() > 1) {
			throw new ResultadoUnicoEsperadoException(
					"Esperado apenas um valor, mas foram encontrados "
							+ lista.size());
		}
		return lista.get(0);
	}

	/**
	 * Ajusta o where, de tal forma se o o nome do parâmetro for igual
	 * ao do atributo, este será convertido para o nome da coluna da tabela.
	 * @param metadados
	 * @param where
	 * @return
	 */
	private static String ajustarWhereParaSQL(MetaDadosEntidade<?> metadados,
			String where) {
		if (where != null && where.length() > 0) {
			StringBuilder builder = new StringBuilder();
			String token, nomeColuna;
			final String caracteres = "><>=<=!=()";
			for (StringTokenizer tokenizer = new StringTokenizer(where, " \n\r\t"); tokenizer
					.hasMoreElements();) {
				token = tokenizer.nextToken();
				if (!caracteres.contains(token)) {
					nomeColuna = metadados.obterNomeColuna(token);
					if (nomeColuna != null) {
						token = nomeColuna;
					}
				}
				builder.append(token);
				builder.append(" ");
			}
			where = builder.toString();
		}
		return where;
	}

	/**
	 * Insere a entidade no banco de dados.
	 * 
	 * @param obj
	 * @param metadados
	 */
	protected long inserir(Object obj, MetaDadosEntidade<?> metadados) {
		// Varrendo as colunas e montando os 'parâmetros' para a inserção.
		ContentValues valores = atribuirValores(obj, metadados, 1);
		long ret = bd.insert(
				metadados.nomeTabela,
				null,
				valores);
		if (ret == -1) {
			throw new DAOException("Falha ao inserir o registro.");
		}
		atribuirChaveAutoincremento(obj, metadados, ret);
		
		return ret;
	}
	
	/**
	 * Atualiza o objeto.
	 * @param obj
	 * @param metadados
	 * @return
	 */
	protected int atualizar(Object obj, MetaDadosEntidade<?> metadados) {
		
		// Varrendo as colunas e montando os 'parâmetros' para a inserção.
		ContentValues valores = atribuirValores(obj, metadados, 1);
		StringBuilder clausulaWhere = new StringBuilder();
		String[] argumentosWhere = montarWhereChaves(metadados, obj, clausulaWhere);
		
		return atualizar(
				metadados.nomeTabela,
				valores, 
				clausulaWhere.toString(),
				argumentosWhere);
	}
	
	/**
	 * Atualiza o objeto.
	 * @param obj
	 * @param metadados
	 * @return
	 */
	protected int remover(Object obj, MetaDadosEntidade<?> metadados) {
		
		StringBuilder clausulaWhere = new StringBuilder();
		String[] argumentosWhere = montarWhereChaves(metadados, obj, clausulaWhere);
		
		return remover(metadados.nomeTabela, clausulaWhere.toString(), argumentosWhere);
	}
	
	protected static String[] montarWhereChaves(MetaDadosEntidade<?> metadados, Object obj, StringBuilder clausulaWhere) {
		 String[] argumentosWhere = new String[metadados.getChaves().length];
		 int conta = argumentosWhere.length;
			
		// Extraindo where de acordo com as chaves
		for (AtributoEntidade atributo: metadados.colunas) {
			
			if (atributo.chavePrimaria()) {
				argumentosWhere[--conta] = MetaDadosEntidade.obterValorStr(
						atributo.atributo, 
						obj);
				clausulaWhere.insert(0, " = ?");
				clausulaWhere.insert(0, atributo.nomeColuna);
		
				if (conta > 0) {
					clausulaWhere.insert(0, " and ");
				}
			}
		} // fim for
		return argumentosWhere;
	}

	/**
	 * @param obj
	 * @param metadados
	 * @param ret
	 */
	private void atribuirChaveAutoincremento(Object obj,
			MetaDadosEntidade<?> metadados, long ret) {
		AtributoEntidade fAutoIncremento = metadados.getChaveAutoIncremento();
		if (fAutoIncremento != null) {
			try {
				MetaDadosEntidade.atribuirValor(
						fAutoIncremento.atributo, 
						obj, 
						ret);
			} catch (Exception e) {
				throw new DAOException("Falha ao atribuir chave ao registro.",
						e);
			}
		}
	}

	/**
	 * Atribui os valores do objeto a uma instância de <tt>ContentValues</tt>.
	 * 
	 * @param obj
	 *            Objeto base.
	 * @param metadados
	 *            Metadados do objeto.
	 * @param excetoChaves
	 *            Quais colunas podem ser inseridas:
	 *            <ul>
	 *            <li>0</li>Todos.
	 *            <li>1</li>Exceto chaves autoincremento.
	 *            <li>2</li>Exceto chaves.
	 *            </ul>
	 * @return Nova instância de <tt>ContentValues</tt>.
	 */
	protected static ContentValues atribuirValores(
			Object obj,
			MetaDadosEntidade<?> metadados, 
			final int excetoChaves) {

		ContentValues valores = new ContentValues();

		// Atributo da chave primária autoincremento
		final AtributoEntidade atributoCP = metadados.getChaveAutoIncremento();
		for (AtributoEntidade atributo: metadados.colunas) {
			// Desconsidera se 'excetoChaves' conforme o caso.
			if ((excetoChaves == 1 && atributo != null && atributo.equals(atributoCP))
					|| (excetoChaves == 2 && atributo.chavePrimaria())) { 
				continue;
			}
			atribuirValor(obj, metadados, atributo, valores);
		}
		return valores;
	}

	/**
	 * Atribui o valor.
	 * @param obj Objeto base.
	 * @param metadados Metadados.
	 * @param f Campo corrente.
	 * @param valores Valroes que estão sendo atribuídos.
	 */
	protected static void atribuirValor(
			Object obj,
			MetaDadosEntidade<?> metadados, 
			AtributoEntidade ae, 
			ContentValues valores) {
		final String coluna = ae.nomeColuna;
		Object valor = MetaDadosEntidade.extrairValor(
				ae, 
				obj);
		if (metadados == null) {
			metadados = MetadadosBuilder.getMetadadosEntidade(obj.getClass());
		}
		if (valor == null) {
			valores.putNull(coluna);
		} else if (valor instanceof Boolean) {
			valores.put(coluna, (Boolean) valor);
		} else if (valor instanceof Byte) {
			valores.put(coluna, (Byte) valor);
		} else if (valor instanceof Short) {
			valores.put(coluna, (Short) valor);
		} else if (valor instanceof Integer) {
			valores.put(coluna, (Integer) valor);
		} else if (valor instanceof Long) {
			valores.put(coluna, (Long) valor);
		} else if (valor instanceof Float) {
			valores.put(coluna, (Float) valor);
		} else if (valor instanceof Double) {
			valores.put(coluna, (Double) valor);
		} else if (valor instanceof BigDecimal) {
			// Ajustar conforme a necessidade.
			valores.put(coluna, new Double(((BigDecimal) valor).doubleValue()));
		} else { // Resta apenas string
			valores.put(coluna, valor.toString());
		}
	}
	
	/**
	 * Carrega os valores do cursor na lista.
	 * @param classeResultado Classe resultado.
	 * @param cursor Cursor.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> ArrayList<T> carregarResultadoLista(
			Class<T> classeResultado, Cursor cursor) {
		
		ArrayList<T> lista = new ArrayList<T>();
		// Há valores ?
		if (cursor != null && cursor.moveToFirst()) {
			Log.i("ABSTRACTDAO", "Qtde cursor: " + cursor.getCount());
			
			// Extraindo os valores.
			int tipoResultado = -1;
			MetaDadosEntidade<T> metadados = null;
			if (Byte.class.equals(classeResultado)
					|| Short.class.equals(classeResultado)
					|| Integer.class.equals(classeResultado)
					|| Long.class.equals(classeResultado)
					|| Float.class.equals(classeResultado)
					|| Double.class.equals(classeResultado)
					|| BigDecimal.class.equals(classeResultado)
					|| String.class.equals(classeResultado)
					|| Date.class.equals(classeResultado)
					|| classeResultado.isEnum()) {
				tipoResultado = 1;
			} else if (String[].class.equals(classeResultado)) {
				tipoResultado = 2;
			} else {
				metadados = MetadadosBuilder.haClasse(classeResultado) ?
						MetadadosBuilder.getMetadadosEntidade(classeResultado) 
						: new MetaDadosEntidade<T>(classeResultado);
				if (metadados == null) {
					throw new DAOException("Tipo n\u00e3o suportado pelo sistema.");
				}
			}
			do {
				// Objeto da lista.
				T obj = null;
				
				switch (tipoResultado) {
					case 1:
						obj = carregarValorColuna(
								cursor,
								0,
								classeResultado,
								null);
					break;
					
					case 2: {
						String[] valores = new String[cursor.getColumnCount()];
						for (int i=0; i < valores.length; i++) {
							if (!cursor.isNull(i)) {
								valores[i] = cursor.getString(i);
							}
						} // fim for
						obj = (T) valores;
					}
					break;
					default:
						obj = (T) extrairLinha(metadados, cursor);
					break;
				}
				lista.add(obj);

			}  while (cursor.moveToNext());
		}
		return lista;

	}
	
	protected static Object extrairLinha(MetaDadosEntidade<?> metadados, Cursor cursor) {
		Object obj = null;
		try {
			obj = metadados.classe.newInstance();
		} catch (Exception e) {
			throw new DAOException(
					"Falha ao reservar recurso de sistema para novo registro.", 
					e);
		}
		
		int indice = 0; // índice da coluna
		Object valor = null;
		Object formato = null;
		for (AtributoEntidade ae: metadados.colunas) {
			indice = cursor.getColumnIndex(ae.nomeColuna);
			formato = ae.formato;
			// extraindo o valor do banco
			valor = carregarValorColuna(cursor, indice, ae.atributo.getType(), formato);
			// definindo o valor para o objeto.
			ObjectUtil.set(obj, ae.atributo, valor);		
		} // fim for
		
		return obj;
	}
	
	
	protected <T> ArrayList<T> pesquisar(MetaDadosEntidade<T> metadados,
			String where, Object... args) {
		where = ajustarWhereParaSQL(metadados, where);
		String[] argumentos = obterArgumentos(metadados, where, args);
		return consultar(metadados, where, argumentos, null, null, null);
	}
	
	protected static MetaDadosEntidade<?> getMetaDadosEntidade(Object obj) {
		return getMetaDadosEntidade(obj.getClass());
	}
	
	protected static MetaDadosEntidade<?> getMetaDadosEntidade(Class<?> classe) {
		MetaDadosEntidade<?> metadados = MetadadosBuilder.getMetadadosEntidade(classe);
		if (metadados == null 
				|| metadados.nomeTabela == null
				|| metadados.colunas.length == 0) {
			throw new DAOException("Recurso (classe '"
					+ (classe != null ? classe.getName() : null)
					+ "') n\u00e3o \u00e9 uma entidade.");
		}
		return metadados;
	}
	
	/**
	 * Carrega o valor da coluna.
	 * @param cursor Cursor.
	 * @param coluna Coluna.
	 * @param classe Tipo do objeto.
	 * @param formato Identificador do tipo (formato).
	 * @return O valor 'convertido'.
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T carregarValorColuna(Cursor cursor, int coluna, Class<T> classe, Object formato) {
		T obj = null;
		if (!cursor.isNull(coluna)) {
			// É um inteiro ?
			if (Byte.class.equals(classe)
					|| Integer.class.equals(classe)
					|| Short.class.equals(classe)
					|| Long.class.equals(classe)) {
				
				try {
					obj = classe.getConstructor(String.class)
							.newInstance(
									cursor.getString(coluna));
				} catch (Exception e) {
					throw new DAOException(
							"Falha na leitura de um valor num\u00e9rico do sistema.",
							e);
				}
			}
			// É para ler um valor de ponto flutuante ?
			else if (Float.class.equals(classe)
					|| Double.class.equals(classe)
					|| BigDecimal.class.equals(classe)) {
				double v = cursor.getDouble(coluna);
				try {
					obj = classe.getConstructor(Double.TYPE)
							.newInstance(v);
				} catch (Exception e) {
					throw new RuntimeException(
							"Falha na leitura de um valor num\u00e9rico real do sistema.",
							e);
				}
			} else if (classe.isEnum()) {
				boolean ordinal = !(formato != null && Boolean.FALSE.equals(formato));
				
				obj = (T) ObjectUtil.toEnum(
						classe, 
						cursor.getString(coluna),
						ordinal);
				
			} else if (Date.class.equals(classe)) {
				FormatoData formatoData = FormatoData.LONG;
				if (formato instanceof FormatoData) {
					formatoData = (FormatoData) formato;
				}
				String dataStr = cursor.getString(coluna);
				try {
					obj = (T) ObjectUtil.toDate(
							dataStr,
							formatoData);
				} catch (ParseException e) {
					throw new DAOException(
							"Falha ao converter a data '"
							+ dataStr
							+ "' para o formato "
							+ formatoData, 
							e);
				}
			} else {
				// Considerado como String;
				obj = (T) cursor.getString(coluna);
			}
		}
		return obj;
	}
	
	public static String[] obterArgumentos(MetaDadosEntidade<?> metadados, String clausula, Object... args) {
		
		ArrayList<String> colunas = extrairColunasParametrizadas(clausula);
		String[] argumentos = null;
		if (args != null && args.length > 0) {
			
			argumentos = new String[args.length];
			
			if (metadados == null || colunas == null) {
				// Considerando tudo como string.
				for (int i = 0; i < args.length; i++) {
					argumentos[i] = String.valueOf(args[i]);
				}
			} 
			else if (colunas != null && colunas.size() !=  args.length) {
				throw new DAOException(
					"H\u00e1 diferen\u00e7a na quantidade entre os '?' da consulta (qtd="
						+ colunas.size()
						+ ") e os argumentos informados (qtde="
						+ colunas.size()
						+ ").");
			}
			else if (colunas != null) {
				// Extraindo conforme o tip
				AtributoEntidade ae;
				Object formato; 
				for (int i=0; i < colunas.size(); i++) {
					ae = metadados.getAtributoPelaColuna(colunas.get(i));
					if (ae == null) {
						throw new DAOException("Coluna '"
								+ colunas.get(i)
								+ "' n\u00e3o encontrada para a entidade");
					}

					formato = ae.formato;
					
					// Obter o valor nativo
					argumentos[i] = ObjectUtil.toString(args[i], formato);
				}
			}
		}
		return argumentos;
	}
	
	protected static ArrayList<String> extrairColunasParametrizadas(String clausula) {
		ArrayList<String> colunas = null;
		if (clausula != null && clausula.contains("?")) {
			colunas = new ArrayList<String>();
			ArrayList<String> tokens = new ArrayList<String>();
			for (StringTokenizer tokenizer = new StringTokenizer(clausula, " \n\r\t");
					tokenizer.hasMoreTokens();) {
				tokens.add(tokenizer.nextToken());
			}
			for (int i=0; i < tokens.size(); i++) {
				if ("?".equals(tokens.get(i))) {
					try {
						colunas.add(tokens.get(i-2));
					} catch (Exception e){
						throw new DAOException("Falha ao identificar coluna de '?'", e);
					}
				}
			}
		}
		return colunas;
	}

}
