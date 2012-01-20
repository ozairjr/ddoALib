package open.ddo.android.widget;


import android.app.Activity;
import android.widget.EditText;

/**
 * Utilitário para atividades.
 * @author ozairjr
 *
 */
public class ActivityUtil {
	
	// --------------------------------------------------------------
	// Métodos
	// --------------------------------------------------------------

	/**
	 * Encontra o recurso pelo ID.
	 * @param activity
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);
	}
	
	/**
	 * Obtém o texto do campo de edição.
	 * @param activity 'Atividade'.
	 * @param resId Identificador do campo de edição.
	 * @return A string contida no campo.
	 */
	public static String getEditTextValue(Activity activity, int resId) {
		EditText editText = findViewById(activity, resId);
		return editText.getText().toString();
	}
}
