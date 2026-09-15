package gt.edu.uinsight.followup.controller;

/*
 * FollowUpController
 * Responsabilidad: exponer los endpoints REST del módulo de seguimiento (prefijo /api/v1).
 * [semana 3]
 *
 * Endpoints previstos:
 *  - POST /api/v1/interventions/{interventionId}/follow-ups   -> 201 | 400, 404, 409
 *      FollowUpResponse create(Long interventionId, CreateFollowUpRequest request)
 *
 *  - GET  /api/v1/interventions/{interventionId}/follow-ups   -> 200 | 404
 *      List<FollowUpResponse> listByIntervention(Long interventionId)
 *
 * Responsabilidad limitada a recibir la petición, delegar en FollowUpService y traducir
 * la respuesta a DTO. Las reglas de negocio (RN-5, RN-6) se validan en el service, no aquí.
 */
