// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.repository;

/*
 * FollowUpRepository
 * Responsabilidad: acceso a datos de la entidad FollowUp.
 *
 * Métodos previstos:
 *  - List<FollowUp> findByInterventionId(Long interventionId)
 *
 * Nota: extenderá JpaRepository<FollowUp, Long> una vez que la dependencia JPA
 * esté disponible en el pom (a cargo del coordinador general).
 *
 * RN-7: las consultas de listado deben excluir registros con borrado lógico activo
 * (deleted = true).
 */
