package open.ddo.android.persistencia.anotacao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Anotação para identificar uma enumeração se será
 * ordinal (o valor padrão) ou se será baseada no
 * 'nome' (<tt>Enum.name()</tt>) da enumeração.
 * 
 * @author ozairjr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Enumeracao {

	/** Como a enumeração será gravada no banco.
	 * Com base no seu valor ordinal (se <tt>true</tt>,
	 * que é o valor padrão)
	 * ou se será em seu nome.
	 * 
	 */
	boolean ordinal() default true;
	
}
