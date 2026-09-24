// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.exception;

/*
 * InterventionNotActiveException
 * Se lanza al intentar registrar un follow-up sobre una intervención que no está en
 * estado PLANNED o IN_PROGRESS (RN-5).
 * Debe mapear a HTTP 409 en el manejador global de excepciones.
 */
