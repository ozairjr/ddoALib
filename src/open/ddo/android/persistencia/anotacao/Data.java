package open.ddo.android.persistencia.anotacao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import open.ddo.android.util.FormatoData;

/**
 * Identifica o tipo da data.
 * @author ozairjr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {
	
	/** Tipo/formato que a data será salva no banco de dados.
	 * Se o atributo for do tipo <tt>java.util.Date</tt> 
	 * e não contiver esta anotação, então o formato padrão
	 * será <tt>FormatoData.LONG<tt>. */
	FormatoData formato() default FormatoData.LONG;
	

}
