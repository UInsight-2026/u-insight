// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.dto.request;

/*
 * CreateFollowUpRequest
 * DTO de entrada para POST /api/v1/interventions/{interventionId}/follow-ups   [semana 3]
 *
 * Campos previstos:
 *  - LocalDate followUpDate    (obligatorio, RN-6)
 *  - String observation        (obligatorio)
 *  - FollowUpResult result     (opcional)
 *
 * Nota: interventionId no viaja en el body, se toma del path variable.
 * Validaciones (Bean Validation) se agregarán cuando la dependencia esté disponible.
 */
