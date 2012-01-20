package open.ddo.android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogUtil {
	
	/**
	 * Apresenta diálogo de altera com botão 'Yes' e 'No'.
	 * @param context Contexto.
	 * @param message Mensagem.
	 * @param yes 'Label' do botão 'Yes'.
	 * @param yesListener Listener do botão 'yes'.
	 * @param no 'Label' do botão 'No'.
	 * @param noListener Listener do botão 'No'.
	 */
	public static void showAlertDialogYesNo(
			Context context,
			String message,
			String yes, 
			OnClickListener yesListener, 
			String no, 
			OnClickListener noListener) {
		showAlertDialogYesNo(context, message, yes, yesListener, no, noListener, false);
	}
	
	public static void showAlertDialogYesNo(
			Context context,
			String message,
			String yes, 
			OnClickListener yesListener, 
			String no, 
			OnClickListener noListener,
			boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setCancelable(cancelable)
				.setPositiveButton(yes, yesListener)
				.setNegativeButton(no, noListener);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static void showAlertDialogSimNao(
			Context context,
			String message,
			OnClickListener simListener,
			OnClickListener naoListener,
			boolean cancelable) {
		showAlertDialogYesNo(
				context, 
				message, 
				"Sim", 
				simListener, 
				"N\u00e3o", 
				naoListener, 
				cancelable);
	}
	
	public static void showAlertDialogSimNao(
			Context context,
			String message,
			OnClickListener simListener,
			OnClickListener naoListener) {
		showAlertDialogYesNo(
				context, 
				message, 
				"Sim", 
				simListener, 
				"N\u00e3o", 
				naoListener, 
				false);
	}
	
	public static void showAlertDialogSimNao(
			Context context,
			String message,
			OnClickListener simListener) {
		showAlertDialogYesNo(
				context, 
				message, 
				"Sim", 
				simListener, 
				"N\u00e3o", 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, 
				false);
	}
	

}
