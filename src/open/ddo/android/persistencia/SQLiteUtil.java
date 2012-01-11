package open.ddo.android.persistencia;

import open.ddo.android.persistencia.excecao.DAOException;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Utilitário para o B.D. SQLite.
 * @author ozairjr
 *
 */
public class SQLiteUtil {
	
	// --------------------------------------------------------------
	// Atributos de classe
	// --------------------------------------------------------------
	
	/** Nome do banco de dados. (deve ser informado pela aplicação) */
	public static String NOME_BD = null;
	/** Versão do B.D. (Se possível deve ser informado pela aplicação). */
	public static int VERSAO_BD = 1;
	/** 'Gerenciador' do B.D. */
	private static SQLiteDatabase bd = null;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/**
	 * Acessa o gerenciador do B.D.<br/>
	 * 
	 * <b>Nota</b>: Deve-se chamar antes o método para iniciar o B.D.
	 * @return
	 */
	public static SQLiteDatabase getBD() {
		if (bd == null) {
			throw new DAOException("B.D. n\u00e3o foi iniciado pelo sistema.");
		}
		return bd;
	}
	
	/**
	 * Fecha o banco de dados.
	 */
	public static void fecharBD() {
		if (bd != null) {
			try {
				bd.close();
			} catch (Exception _) {
				
			} finally {
				bd = null;
			}
		}
	}
	
	/**
	 * Inicia o B.D. A aplicação que iniciar o B.D., informando o nome do B.D.
	 * @param ctx Contexto
	 * @param sqls SQL de criação/atualização do B.D.
	 * @param nomeBD Nome do B.D.
	 * @return
	 */
	public static SQLiteDatabase iniciarBD(Context ctx, String[] sqls, final String nomeBD) {
		NOME_BD = nomeBD;
		return iniciarBD(ctx, sqls);
	}
	
	/**
	 * Inicia o B.D. A aplicação que iniciar o B.D. deve informar
	 * o <tt>NOME_BD</tt> e se possível a <tt>VERSAO_BD</tt>.
	 * @param ctx Conexto.
	 * @param sqls SQL de criação do B.D.
	 * @return
	 */
	public static SQLiteDatabase iniciarBD(Context ctx, String[] sqls) {
		
		if (NOME_BD == null){
			throw new DAOException("Nome do B.D. n\u00e3o foi informado.");
		}
		if (VERSAO_BD <= 0){
			throw new DAOException("Vers\u00e3o do B.D. inv\u00e1lida: "
					+ VERSAO_BD
					+ ".");
		}
		fecharBD();
		SQLiteHelper helper = new SQLiteHelper(
				ctx,
				NOME_BD,
				null,
				VERSAO_BD,
				sqls);
		bd = helper.getWritableDatabase();
		return bd;
	}
	
	/**
	 * Fecha o cursor sem lançar exceção.
	 * @param cursor
	 */
	public static void fechar(Cursor cursor) {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (Exception _) {
			}
		}
	}
}
