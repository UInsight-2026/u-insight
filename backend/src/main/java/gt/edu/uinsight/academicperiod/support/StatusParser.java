package gt.edu.uinsight.academicperiod.support;

import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;

import java.util.Arrays;

/**
 * Convierte el texto de un estado (filtro ?status= o cuerpo de PATCH) al enum
 * correspondiente. Un valor no reconocido responde 400: nunca se ignora en silencio.
 */
public final class StatusParser {

    private StatusParser() {
    }

    public static <E extends Enum<E>> E parse(Class<E> type, String value) {
        if (value == null || value.isBlank()) {
            throw AcademicBusinessRuleException.invalidValue("El estado es obligatorio. Valores permitidos: "
                    + Arrays.toString(type.getEnumConstants()));
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw AcademicBusinessRuleException.invalidValue("Estado no reconocido: '" + value
                    + "'. Valores permitidos: " + Arrays.toString(type.getEnumConstants()));
        }
    }

    /** Igual que {@link #parse} pero devuelve null si no se envio filtro. */
    public static <E extends Enum<E>> E parseOptional(Class<E> type, String value) {
        return value == null ? null : parse(type, value);
    }
}
