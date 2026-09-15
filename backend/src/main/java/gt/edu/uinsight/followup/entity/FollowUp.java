package gt.edu.uinsight.followup.entity;

/*
 * Entidad FollowUp (tabla: follow_up)
 * Representa un seguimiento registrado sobre una intervención.
 *
 * Campos previstos:
 *  - Long id
 *  - Long interventionId       (FK a intervention.id)
 *  - LocalDate followUpDate
 *  - String observation
 *  - FollowUpResult result     (opcional)
 *  - boolean deleted           (soporte de borrado lógico, ver RN-7)
 *
 * Reglas de negocio:
 *  - RN-5: solo se registra follow-up sobre intervención en estado PLANNED o IN_PROGRESS.
 *  - RN-6: followUpDate no puede ser anterior al startDate de la intervención asociada.
 *  - RN-7: el borrado es siempre lógico, nunca físico.
 *
 * Nota: sin anotaciones JPA todavía; se agregan cuando la dependencia esté disponible en el pom.
 */
