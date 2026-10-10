// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.domain;

/**
 * Tipos de instrumento de evaluación permitidos (sección 7.1 del documento base).
 * La columna evaluation.type es VARCHAR(30); se guarda el nombre del enum.
 */
public enum EvaluationType {
    EXAM, QUIZ, PROJECT, LAB, ASSIGNMENT, OTHER;

    /** Expresión regular usada por Bean Validation en los DTOs de entrada. */
    public static final String REGEX = "(?i)EXAM|QUIZ|PROJECT|LAB|ASSIGNMENT|OTHER";
}
