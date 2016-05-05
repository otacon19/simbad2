package sinbad2.core.validator;

import sinbad2.core.nls.Messages;

public class Validator {
	/**
	 * Cadena para mostrar si no se aceptan valores nulos.
	 */
	private final static String NOT_NULL = Messages.ParameterValidator_Cannot_pass_null_value;

	/**
	 * Cadena para mostrar si no se aceptan cadenas vac�as.
	 */
	private final static String NOT_EMPTY_STRING = Messages.ParameterValidator_Cannot_pass_empty_string;

	/**
	 * Cadena para mostrar si no se aceptan arrays vac�os.
	 */
	private final static String NOT_EMPTY_ARRAY = Messages.ParameterValidator_Cannot_pass_empty_array;

	/**
	 * Cadena para mostrar si el tama�o es inv�lido.
	 */
	private final static String INVALID_SIZE = Messages.ParameterValidator_Invalid_size;

	/**
	 * Cadena para mostrar si el rango es inv�lido.
	 */
	private final static String INVALID_RANGE = Messages.ParameterValidator_Invalid_range;

	/**
	 * Cadena para mostrar que no se permiten valores negativos.
	 */
	private final static String NOT_NEGATIVE = Messages.ParameterValidator_Cannot_pass_negative_value;

	/**
	 * Cadena para mostrar que no se permiten valores negativos.
	 */
	private final static String NOT_DISORDER = Messages.ParameterValidator_Cannot_pass_disorder_values;

	/**
	 * Cadena para mostrar que no se permiten tipos ilegales.
	 */
	private final static String NOT_ILLEGAL_TYPE = Messages.ParameterValidator_Cannot_pass_illegal_objects;

	private final static String NOT_SAME_ELEMENT = Messages.ParameterValidator_Cannot_pass_same_element;

	/**
	 * Comprueba que un valor est� en el rango especificado.
	 * 
	 * @param value
	 *            Valor a comprobar.
	 * @param min
	 *            L�mite inferior del rango.
	 * @param max
	 *            L�mite superior del rango.
	 * @return True si el valor est� dentro del rango establecido, False en caso
	 *         contrario.
	 * @throws IllegalArgumentException
	 *             Si el rango no es v�lido.
	 */
	public static boolean inRange(int value, int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException(INVALID_RANGE);
		}
		return ((min <= value) && (value <= max));
	}

	/**
	 * Comprueba que un valor est� en el rango especificado.
	 * 
	 * @param value
	 *            Valor a comprobar.
	 * @param min
	 *            L�mite inferior del rango.
	 * @param max
	 *            L�mite superior del rango.
	 * @return True si el valor est� dentro del rango establecido, False en caso
	 *         contrario.
	 * @throws IllegalArgumentException
	 *             Si el rango no es v�lido.
	 */
	public static boolean inRange(double value, double min, double max) {
		if (min > max) {
			throw new IllegalArgumentException(INVALID_RANGE);
		}
		return ((min <= value) && (value <= max));
	}

	/**
	 * Comprueba que un valor est� en el rango especificado.
	 * 
	 * @param value
	 *            Valor a comprobar.
	 * @param min
	 *            L�mite inferior del rango.
	 * @param max
	 *            L�mite superior del rango.
	 * @param includeStartpoint
	 *            Considerar el valor inicial dentro del rango.
	 * @param includeEndpoing
	 *            Considerar el valor final dentro del rango.
	 * 
	 * @return True si el valor est� dentro del rango establecido, False en caso
	 *         contrario.
	 * @throws IllegalArgumentException
	 *             Si el rango no es v�lido.
	 */
	public static boolean inRange(int value, int min, int max,
			boolean includeStartpoint, boolean includeEndpoint) {
		if (min > max) {
			throw new IllegalArgumentException(INVALID_RANGE);
		}
		if (includeStartpoint) {
			if (includeEndpoint) {
				return ((min <= value) && (value <= max));
			} else {
				return ((min <= value) && (value < max));
			}
		} else {
			if (includeEndpoint) {
				return ((min < value) && (value <= max));
			} else {
				return ((min < value) && (value < max));
			}
		}
	}

	/**
	 * Comprueba que un valor est� en el rango especificado.
	 * 
	 * @param value
	 *            Valor a comprobar.
	 * @param min
	 *            L�mite inferior del rango.
	 * @param max
	 *            L�mite superior del rango.
	 * @param includeStartpoint
	 *            Considerar el valor inicial dentro del rango.
	 * @param includeEndpoing
	 *            Considerar el valor final dentro del rango.
	 * 
	 * @return True si el valor est� dentro del rango establecido, False en caso
	 *         contrario.
	 * @throws IllegalArgumentException
	 *             Si el rango no es v�lido.
	 */
	public static boolean inRange(double value, double min, double max,
			boolean includeStartpoint, boolean includeEndpoint) {
		if (min > max) {
			throw new IllegalArgumentException(INVALID_RANGE);
		}
		if (includeStartpoint) {
			if (includeEndpoint) {
				return ((min <= value) && (value <= max));
			} else {
				return ((min <= value) && (value < max));
			}
		} else {
			if (includeEndpoint) {
				return ((min < value) && (value <= max));
			} else {
				return ((min < value) && (value < max));
			}
		}
	}

	/**
	 * Comprueba si una par�metro es nulo.
	 * 
	 * @param parameter
	 *            Par�metro a comprobar.
	 * @return True si es nulo, False en caso contrario.
	 */
	public static boolean isNull(Object parameter) {
		return (parameter == null);
	}

	/**
	 * Comprueba si una cadena est� vac�a.
	 * 
	 * @param parameter
	 *            Par�metro a comprobar.
	 * @return True si es la cadena vac�a, False en caso contrario.
	 * @throws IllegalArgumentException
	 *             Si el par�metro es nulo.
	 */
	public static boolean isEmpty(String parameter) {
		notNull(parameter);
		return (parameter.trim().isEmpty());
	}

	/**
	 * Comprueba si un array est� vac�o.
	 * 
	 * @param parameter
	 *            Par�metro a comprobar.
	 * @return True si el array est� vac�o, False en caso contrario.
	 * 
	 * @throws IllegalArgumentException
	 *             Si el par�metro es nulo.
	 */
	public static boolean isEmpty(Object[] parameter) {
		notNull(parameter);
		return (parameter.length == 0);
	}

	/**
	 * Comprueba si un valor es negativo.
	 * 
	 * @param value
	 *            Valor a comprobar.
	 * 
	 * @return True si el valor es negativo, False en caso contrario.
	 */
	public static boolean isNegative(double value) {
		return (value < 0);
	}

	/**
	 * Comprueba que un conjunto de valores est� desordenado
	 * 
	 * @param values
	 *            Valores a comprobar.
	 * 
	 * @return True si los valores est�n en orden, False en caso contrario.
	 * 
	 * @throws IllegalArgumentException
	 *             Si los valores son nulos.
	 */
	public static boolean isOrdered(double[] values) {

		int length;

		notNull(values);

		length = values.length;
		if (length < 2) {
			return true;
		} else {
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < i; j++) {
					if (values[j] > values[i]) {
						return false;
					}
				}
				for (int j = i + 1; j < length; j++) {
					if (values[j] < values[i]) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * Comprueba que un conjunto de valores est� ordenado de forma estricta.
	 * 
	 * @param values
	 *            Valores a comprobar.
	 * @return True si los valores est�n en orden extricto, False en caso
	 *         contrario.
	 * @throws IllegalArgumentException
	 *             Si los valores son nulos.
	 */
	public static boolean isStrictlyOrdered(double[] values) {

		int length;

		notNull(values);

		length = values.length;
		if (length < 2) {
			return true;
		} else {
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < i; j++) {
					if (values[j] >= values[i]) {
						return false;
					}
				}
				for (int j = i + 1; j < length; j++) {
					if (values[j] <= values[i]) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * Comprueba que un elemento sea de alguno de los tipos indicados.
	 * 
	 * @param object
	 *            Objeto que debe ser del tipo indicado.
	 * @param types
	 *            Tipos permitidos.
	 * @return True si el elemento es de alguno de los tipos indicados, False en
	 *         caso contrario.
	 * @throw IllegalArgumentException Si object es null o,
	 *        <p>
	 *        si types est� vac�o o
	 *        <p>
	 *        si types es null.
	 */
	public static boolean validElementType(Object object, String[] types) {

		notEmpty(types);
		notNull(object);

		String objectClass = object.getClass().toString();

		for (String type : types) {
			if (objectClass.equals(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Comprueba si una cadena es nula o est� vac�a.
	 * 
	 * @param parameter
	 *            Par�metro a comprobar.
	 * @return True si es la cadena es nula o est� vac�a, False en caso
	 *         contrario.
	 */
	public static boolean isNullOrEmpty(String parameter) {
		return (parameter == null) ? true : parameter.trim().isEmpty();
	}

	public static boolean isSameElement(Object element, Object other) {
		return (element == other);
	}

	/**
	 * Comprueba que un parametro no sea nulo.
	 * 
	 * @param parameter
	 *            Parametro a comprobar.
	 * 
	 * @throws IllegalArgumentException
	 *             Si el par�metro es nulo.
	 */
	public static void notNull(Object parameter) {
		if (isNull(parameter)) {
			throw new IllegalArgumentException(NOT_NULL);
		}
	}

	/**
	 * Comprueba que una cadena no sea vac�a.
	 * 
	 * @param parameter
	 *            Parametro a comprobar.
	 * @throws IllegalArgumentException
	 *             Si la cadena est� vac�a o es nula.
	 */
	public static void notEmpty(String parameter) {
		if (isEmpty(parameter)) {
			throw new IllegalArgumentException(NOT_EMPTY_STRING);
		}
	}

	/**
	 * Comprueba que un array no est� vac�o
	 * 
	 * @param parameter
	 *            Array a comprobar.
	 * @throws IllegalArgumentException
	 *             Si el array est� vac�a o es nulo.
	 */
	public static void notEmpty(Object[] parameter) {
		if (isEmpty(parameter)) {
			throw new IllegalArgumentException(NOT_EMPTY_ARRAY);
		}
	}

	/**
	 * Comprueba que el tama�o de un elemento est� dentro de los valores
	 * permitidos.
	 * 
	 * @param size
	 *            Tama�o del elemento.
	 * @param minValidSize
	 *            Tama�o m�nimo permitido.
	 * @param maxValidSize
	 *            Tama�o m�ximo permitido.
	 * @throws IllegalArgumentException
	 *             Si el tama�o no est� dentro del rango permitido o si el rango
	 *             es inv�lido.
	 */
	public static void notInvalidSize(int size, int minValidSize,
			int maxValidSize) {
		if (!inRange(size, minValidSize, maxValidSize)) {
			throw new IllegalArgumentException(INVALID_SIZE
					+ Messages.ParameterValidator_Value_must_be_in_range_1
					+ size + ", " //$NON-NLS-1$
					+ minValidSize + ", " //$NON-NLS-1$
					+ maxValidSize + "]."); //$NON-NLS-1$
		}
	}

	/**
	 * Comprueba que el tama�o de un elemento est� dentro de los valores
	 * permitidos.
	 * 
	 * @param size
	 *            Tama�o del elemento.
	 * @param minValidSize
	 *            Tama�o m�nimo permitido.
	 * @param maxValidSize
	 *            Tama�o m�ximo permitido.
	 * @throws IllegalArgumentException
	 *             Si el tama�o no est� dentro del rango permitido o si el rango
	 *             es inv�lido.
	 */
	public static void notInvalidSize(double size, double minValidSize, double maxValidSize, String parameterName) {
		if (!inRange(size, minValidSize, maxValidSize)) {
			throw new IllegalArgumentException(INVALID_SIZE
					+ Messages.ParameterValidator_Value_must_be_in_range_1
					+ size + ", " //$NON-NLS-1$
					+ minValidSize + ", " //$NON-NLS-1$
					+ maxValidSize + "]."); //$NON-NLS-1$
		}
	}

	/**
	 * Comprueba que el tama�o de un elemento est� dentro de los valores
	 * permitidos.
	 * 
	 * @param size
	 *            Tama�o del elemento.
	 * @param minValidSize
	 *            Tama�o m�nimo permitido.
	 * @param maxValidSize
	 *            Tama�o m�ximo permitido.
	 * @param includeStartpoint
	 *            Incluir el valor inicial dentro del rango.
	 * @param maxValidSize
	 *            Incluier el valor final dentro del rango.
	 * @throws IllegalArgumentException
	 *             Si el tama�o no est� dentro del rango permitido o si el rango
	 *             es inv�lido.
	 */
	public static void notInvalidSize(int size, int minValidSize,
			int maxValidSize, boolean includeStartpoint,
			boolean includeEndpoint, String parameterName) {
		if (!inRange(size, minValidSize, maxValidSize, includeStartpoint,
				includeEndpoint)) {
			String open = (includeEndpoint) ? "[" : "("; //$NON-NLS-1$ //$NON-NLS-2$
			String close = (includeEndpoint) ? "]" : ")"; //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalArgumentException(INVALID_SIZE
					+ Messages.ParameterValidator_Value_must_be_in_range_2
					+ open + minValidSize + ", " //$NON-NLS-1$
					+ maxValidSize + close + "."); //$NON-NLS-1$
		}

	}

	/**
	 * Comprueba que el tama�o de un elemento est� dentro de los valores
	 * permitidos.
	 * 
	 * @param size
	 *            Tama�o del elemento.
	 * @param minValidSize
	 *            Tama�o m�nimo permitido.
	 * @param maxValidSize
	 *            Tama�o m�ximo permitido.
	 * @param includeStartpoint
	 *            Incluir el valor inicial dentro del rango.
	 * @param maxValidSize
	 *            Incluier el valor final dentro del rango.
	 * @throws IllegalArgumentException
	 *             Si el tama�o no est� dentro del rango permitido o si el rango
	 *             es inv�lido.
	 */
	public static void notInvalidSize(double size, double minValidSize,
			double maxValidSize, boolean includeStartpoint,
			boolean includeEndpoint) {
		if (!inRange(size, minValidSize, maxValidSize, includeStartpoint,
				includeEndpoint)) {
			String open = (includeEndpoint) ? "[" : "("; //$NON-NLS-1$ //$NON-NLS-2$
			String close = (includeEndpoint) ? "]" : ")"; //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalArgumentException(INVALID_SIZE
					+ Messages.ParameterValidator_Value_must_be_in_range_2
					+ open + minValidSize + ", " //$NON-NLS-1$
					+ maxValidSize + close + "."); //$NON-NLS-1$
		}

	}

	/**
	 * Comprueba que un valor no sea negativo.
	 * 
	 * @param value
	 *            Valor a comprobar.
	 * @throw IllegalArgumentException Si el par�metro es negativo.
	 */
	public static void notNegative(double value) {
		if (isNegative(value)) {
			throw new IllegalArgumentException(NOT_NEGATIVE);
		}
	}

	/**
	 * Comprueba que un conjunto de valores no est�n desordenados.
	 * 
	 * @param values
	 *            Valores a comprobar.
	 * @param strictlyOrder
	 *            Orden estricto.
	 * @throw IllegalArgumentException Si los valores son nulos o est�n
	 *        desordenados.
	 */
	public static void notDisorder(double[] values, boolean strictlyOrder) {

		boolean disorder;

		if (strictlyOrder) {
			disorder = !isStrictlyOrdered(values);
		} else {
			disorder = !isOrdered(values);
		}

		if (disorder) {
			throw new IllegalArgumentException(NOT_DISORDER);
		}
	}

	/**
	 * Comprueba que un elemento sea de alguno de los tipos indicados.
	 * 
	 * @param object
	 *            Objeto que debe ser del tipo indicado.
	 * @param types
	 *            Tipos permitidos.
	 * @throw IllegalArgumentException Si el objeto no es de alguno de los tipos
	 *        indicados o,
	 *        <p>
	 *        si object es null o,
	 *        <p>
	 *        si types est� vac�o o
	 *        <p>
	 *        si types es null.
	 */
	public static void notIllegalElementType(Object object, String[] types) {
		if (!validElementType(object, types)) {
			throw new IllegalArgumentException(NOT_ILLEGAL_TYPE);
		}
	}

	public static void notSameElement(Object element, Object other) {
		if (isSameElement(element, other)) {
			throw new IllegalArgumentException(NOT_SAME_ELEMENT);
		}
	}



}
