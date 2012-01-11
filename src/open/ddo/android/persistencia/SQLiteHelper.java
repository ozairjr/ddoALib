package open.ddo.android.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe auxiliar para se criar o B.D.
 * @author ozairjr
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	
	// --------------------------------------------------------------
	// Atributos do objeto
	// --------------------------------------------------------------
	
	/**
	 * SQLs para criar o B.D.
	 */
	private String[] sqlCreate;
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------

	public SQLiteHelper(
			Context context, 
			String name, 
			CursorFactory factory,
			int version,
			String[] sqlCreate) {
		super(context, name, factory, version);
		this.sqlCreate = sqlCreate;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	
		if (sqlCreate != null && sqlCreate.length > 0) {
			// Criando
			for (String sql: sqlCreate) {
				db.execSQL(sql);
			}
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersa) {
		onCreate(db);
	}

}
