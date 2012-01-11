package open.ddo.android.persistencia.anotacao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para a coluna que a identifica como chave
 * da tabela.
 * 
 * @author ozairjr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Chave {
	/**
	 * Identifica se a chave é auto-incremento.
	 * <b>Nota</b>: Somente pode haver apenas uma única
	 * coluna que seja autoincremento.
	 * (Chave primária composta é aceita). 
	 * @return
	 */
	boolean autoincremento() default false;

}
