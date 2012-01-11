package open.ddo.android.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Utilitário para objetos java.
 * @author ozairjr
 *
 */
public class ObjectUtil {
	
	/**
	 * Carrega o valor do objeto como enumeração.
	 * @param clazz Classe.
	 * @param value Valor.
	 * @param numeric É numérico (ordinal()) ou é String (name).
	 * @return
	 */
	public static Enum<?> toEnum(Class<?> clazz, Object value, boolean numeric) {
		Enum<?> obj = null;
		if (value != null) {
			Enum<?>[] valores = (Enum<?>[]) clazz.getEnumConstants();
			for (Enum<?> e: valores) {
				int vrInt = numeric ? Integer.parseInt(value.toString()) : -1;
				String str = value.toString();
				if ((numeric && vrInt == e.ordinal())
					|| (!numeric && e.name().equals(str))) {
					obj = e;
					break;
				}
			} // fim for
		}
		return obj;
	}
	
	/**
	 * Tenta converter o objeto informado em uma instância de <tt>java.util.Date</tt>.
	 * @param value
	 * @param formato
	 * @return
	 * @throws ParseException
	 */
	public static Date toDate(Object value, FormatoData formato) throws ParseException {
		if (formato == null) {
			formato = FormatoData.LONG;
		}
		Date d = null;
		if (value != null) {
			switch (formato) {
				case LONG:
					d = new Date(Long.parseLong(value.toString()));
				break;
				default:
					d = toDate(value.toString(), formato.formato);
				break;
			}
		}
		return d;
	}
	
	/**
	 * Converte a string em uma data.
	 * @param str
	 * @param fmtJava
	 * @return
	 * @throws ParseException
	 */
	public static Date toDate(String str, final String fmtJava) throws ParseException {
		Date d = null;
		if (str != null && str.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(fmtJava);
			d = sdf.parse(str);
		}
		return d;
	}

	/**
	 * Atribui o valor 'value' ao atributo 'f' do objeto 'obj'.
	 * @param obj
	 * @param f
	 * @param value
	 */
	public static void set(Object obj, Field f, Object value) {
		boolean b = false;
		if (!f.isAccessible()) {
			f.setAccessible(true);
			b = true;
		}
		try {
			f.set(obj, value);
		} catch (Exception e) {
			new RuntimeException(e.getMessage(), e);
		} finally {
			if (b){
				try {
					f.setAccessible(false);
				} catch (Exception _) {
					
				}
			}
		}
	}
	
	/**
	 * Converte a data em uma string.
	 * @param valor
	 * @param formatoData
	 * @return
	 */
	public static String toString(Date valor, FormatoData formatoData) {
		String str = null;
		if (valor != null) {
			switch (formatoData) {
				case LONG:
					str = String.valueOf(valor.getTime());
				break;
				default:
					str = toString(valor, formatoData.formato);
				break;
			}
		}
		return str;
	}
	
	/**
	 * Converte a data em uma string.
	 * @param valor
	 * @param fmtJava
	 * @return
	 */
	public static String toString(Date valor, String fmtJava) {
		String str = null;
		if (valor != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(fmtJava);
			str = sdf.format(valor);
		}
		return str;
	}
	
	/**
	 * Converte o objeto em uma string.
	 * @param valor
	 * @param formato
	 * @return
	 */
	public static String toString(Object valor, Object formato) {
		String str = null;
		if (valor != null) {
			if (valor instanceof Date) {
				FormatoData formatoData = FormatoData.LONG;
				if (formato instanceof FormatoData) {
					formatoData = (FormatoData) formato;
				}
				str = toString((Date) valor, formatoData);
			}
			else if (valor instanceof Enum<?>) {
				Enum<?> e = (Enum<?>) valor;
				// É para considerar o valor ordinal ? (ou o name?)
				boolean ordinal = !(formato != null && Boolean.FALSE.equals(formato));
				
				str = ordinal ? 
						String.valueOf(e.ordinal())
						: e.name();
			}
			else {
				str = valor.toString();
			}
		}
		return str;
	}
	
	/**
	 * Obtém o valor do atributo do objeto informado. 
	 * @param obj
	 * @param f
	 * @return
	 */
	public static Object getValue(Object obj, Field f) {
    	boolean b = false;
    	if (!f.isAccessible()) {
    		f.setAccessible(true);
    		b = true;
    	}
    	try {
			return f.get(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (b) {
				f.setAccessible(false);
			}
		}
    }
	
	/**
	 * Obtém o atributo da classe que possui o nome informado. 
	 * @param clazz Classe.
	 * @param fieldName Nome do atributo.
	 * @return O atributo encontrado, ou <tt>null</tt> se não encontrar.
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		if (fieldName != null && (clazz != Object.class || !Object.class.equals(clazz))) {
			for (Field f: clazz.getDeclaredFields()) {
				if (f.getName().equals(fieldName)) {
					return f;
				}
			}
			return getField(clazz.getSuperclass(), fieldName);
		}
		return null;
	}
	
	/**
	 * Obtém os atributos da classe informada.
	 * @param clazz
	 * @return
	 */
	public static ArrayList<Field> getFields(Class<?> clazz) {
		ArrayList<Field> fields = new ArrayList<Field>();
		searchFields(clazz, fields);
		return fields;
	}
	
	/**
	 * Pesquisa pelos atributos da classe, populando a lista informada.
	 * @param clazz
	 * @param fields
	 */
	protected static void searchFields(Class<?> clazz, ArrayList<Field> fields) {
		if (clazz != null && !Object.class.equals(clazz) && fields != null) {
			searchFields(clazz.getSuperclass(), fields);
			for (Field f: clazz.getDeclaredFields()) {
				fields.add(f);
			}
		}
	}
}
