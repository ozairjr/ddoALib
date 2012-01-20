package open.ddo.android.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Utilitário para <tt>Toast</tt>.
 * @author ozairjr
 *
 */
public class ToastUtil {

	/**
	 * Apresenta mensagem curta.
	 * @param ctx Contexto.
	 * @param msg Mensagem.
	 * @param args Argumentos (opcionais) da mensagem.
	 */
	public static void showShort(Context ctx, String msg, Object... args) {
		show(ctx, Toast.LENGTH_SHORT, msg, args);
	}
	
	/**
	 * Apresenta mensagem 'longa'.
	 * @param ctx Contexto.
	 * @param msg Mensagem.
	 * @param args Argumentos.
	 */
	public static void showLong(Context ctx, String msg, Object... args) {
		show(ctx, Toast.LENGTH_LONG, msg, args);
	}
	
	/**
	 * Apresenta instância de <tt>Toast</tt>.
	 * @param ctx Contexto.
	 * @param duration Duração (<tt>Toast.LENGTH_LONG</tt> ou <tt>Toast.LENGTH_SHORT</tt>).
	 * @param msg Mensagem.
	 * @param args Argumentos.
	 */
	public static void show(Context ctx, int duration, String msg, Object... args) {
		Toast.makeText(ctx, msg != null ? String.format(msg, args) : "", duration).show();
	}
	
	/**
	 * Apresenta mensagem em <tt>Toast</tt>.
	 * @param ctx Contexto.
	 * @param duration Duração (<tt>Toast.LENGTH_LONG</tt> ou <tt>Toast.LENGTH_SHORT</tt>).
	 * @param gravity 'Gravidade' (<tt>Gravity.<i>??</i></tt>).
	 * @param x Ponto X.
	 * @param y Ponto Y.
	 * @param msg Mensagem.
	 * @param args Argumentos.
	 */
	public static void show(Context ctx, int duration, int gravity, int x, int y, String msg, Object... args) {
		Toast t = new Toast(ctx);
		t.setDuration(duration);
		t.setGravity(gravity, x, y);
		t.setText(msg != null ? String.format(msg, args) : "");
		t.show();
	}
	
	/**
	 * Apresenta mensagem de aviso 
	 * (um <tt>Toast</tt> com 'ícone' de aviso, com a duração 'longa')
	 * @param activity Atividade 'pai'.
	 * @param msg Mensagem.
	 */
	public static void showWarning(Activity activity, String msg) {
		show(activity, msg, false, open.ddo.android.R.drawable.warning_16x16);
	}
	
	/**
	 * Apresenta mensagem de aviso 
	 * (um <tt>Toast</tt> com 'ícone' de aviso, com a duração 'curta')
	 * @param activity Atividade 'pai'.
	 * @param msg Mensagem.
	 */
	public static void showShortWarning(Activity activity, String msg) {
		show(activity, msg, true, open.ddo.android.R.drawable.warning_16x16);
	}
	
	/**
	 * Apresenta mensagem de erro 
	 * (um <tt>Toast</tt> com 'ícone' de erro, com a duração 'longa')
	 * @param activity Atividade 'pai'.
	 * @param msg Mensagem.
	 */
	public static void showError(Activity activity, String msg) {
		show(activity, msg, false, open.ddo.android.R.drawable.error2_16x_16);
	}
	
	/**
	 * Apresenta mensagem de erro 
	 * (um <tt>Toast</tt> com 'ícone' de erro, com a duração 'curta')
	 * @param activity Atividade 'pai'.
	 * @param msg Mensagem.
	 */
	public static void showShortError(Activity activity, String msg) {
		show(activity, msg, true, open.ddo.android.R.drawable.error2_16x_16);
	}
	
	/**
	 * Apresenta 'custom toast'
	 * @param activity 'Atividade pai'.
	 * @param msg Mensagem.
	 * @param shortDuration A duração do 'toast' é short? Sim (true).
	 * @param type 'Identificador' da imagem.
	 */
	protected static void show(Activity activity, String msg, boolean shortDuration, int resId) {
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(
				open.ddo.android.R.layout.toast_layout,
				(ViewGroup) activity.findViewById(open.ddo.android.R.id.ddo_toast_layout));
		
		ImageView imgView = (ImageView) view.findViewById(open.ddo.android.R.id.toast_image);
		imgView.setImageResource(resId);
		
		TextView txt = (TextView) view.findViewById(open.ddo.android.R.id.toast_texto);
		
		txt.setText(msg);
		
		Toast toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(shortDuration ?
				Toast.LENGTH_SHORT
				: Toast.LENGTH_LONG);
		toast.setView(view);
		toast.show();
	}
}
