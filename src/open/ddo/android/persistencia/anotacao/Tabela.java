package open.ddo.android.persistencia.anotacao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para a classe identificando-a como um entidade
 * para uma tabela.
 * @author ozairjr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tabela {
	
	/** Nome da tabela. Se não informado será o nome da classe. */
	String nome() default "";
}
