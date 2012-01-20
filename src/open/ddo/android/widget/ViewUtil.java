package open.ddo.android.widget;

import open.ddo.android.util.ObjectUtil;
import android.view.View;
import android.widget.EditText;

/**
 * Utilitário para <tt>View</tt>.
 * @author ozairjr
 *
 */
public class ViewUtil {

	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------
	
	/**
	 * Obtém a <tt>View</tt> pelo seu identificador.
	 * @param view <i>Visão</i> básica.
	 * @param id Identificador do recurso.
	 * @return A <i>visão</i>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findViewById(View view, int id) {
		return (T) view.findViewById(id);
	}

	/**
	 * Obtém o texto (a String) do campo de edição de texto.
	 * @param editText Campo de edição de texto.
	 * @return A string contida no campo.
	 */
	public static String get(EditText editText) {
		return editText.getText().toString();
	}
	
	/**
	 * Obtém o texto do campo de edição.
	 * @param view 'Visão'.
	 * @param resId Identificador do campo de edição.
	 * @return A string contida no campo.
	 */
	public static String getEditTextValue(View view, int resId) {
		EditText editText = findViewById(view, resId);
		return editText.getText().toString();
	}
	

	/**
	 * Obtém o valor do campo texto como sendo instância de <tt>Integer</tt>.
	 * @param editText Campo de edição de texto.
	 * @return O valor inteiro.
	 */
	public static Integer getAsInteger(EditText editText) {
		String temp = get(editText);
		if (temp != null && temp.trim().length() > 0) {
			return Integer.valueOf(temp.trim());
		}
		return null;
	}

	/**
	 * Obtém o valor do campo texto como sendo instância de <tt>Long</tt>.
	 * @param editText Campo de edição de texto.
	 * @return O valor inteiro.
	 */
	public static Long getAsLong(EditText editText) {
		String temp = get(editText);
		if (temp != null && temp.trim().length() > 0) {
			return Long.valueOf(temp.trim());
		}
		return null;
	}

	/**
	 * Define o valor do campo de edição de texto.
	 * @param editText Campo de edição de texto.
	 * @param valor Novo valor.
	 */
	public static void set(EditText editText, Object valor) {
		set(editText, valor, null);
	}

	/**
	 * Define o valor do campo de edição de texto.
	 * @param editText Campo de edição de texto.
	 * @param valor Novo valor.
	 * @param formato Formato.
	 */
	public static void set(EditText editText, Object valor, Object formato) {
		String temp = ObjectUtil.toString(valor, formato);
		set(editText, temp);
	}

	/**
	 * Define o valor do campo de texto.
	 * @param editText Campo de edição de texto.
	 * @param valor O valor.
	 */
	public static void set(EditText editText, String valor) {
		editText.setText(valor);
	}

}
