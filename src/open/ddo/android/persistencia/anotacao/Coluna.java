package open.ddo.android.persistencia.anotacao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para identificar a coluna da tabela.
 * 
 * @author ozairjr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Coluna {
	/** Nome da coluna. Se não for informado será utilizado o nome do atributo. */
	String nome() default "";
}
