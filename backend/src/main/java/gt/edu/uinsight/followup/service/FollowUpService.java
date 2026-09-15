package gt.edu.uinsight.followup.service;

/*
 * FollowUpService
 * Responsabilidad: orquestar la lógica de negocio del módulo de seguimiento (follow-up).
 * [semana 3]
 *
 * Métodos previstos:
 *  - FollowUpResponse create(Long interventionId, CreateFollowUpRequest request)
 *      Valida que la intervención exista, RN-5 (intervención en estado PLANNED o IN_PROGRESS)
 *      y RN-6 (followUpDate no anterior al startDate de la intervención).
 *      Lanza InterventionNotFoundException (módulo intervention), InterventionNotActiveException.
 *
 *  - List<FollowUpResponse> listByIntervention(Long interventionId)
 *      Lanza InterventionNotFoundException (módulo intervention).
 *
 * Reglas de negocio cubiertas: RN-5, RN-6, RN-7.
 */
