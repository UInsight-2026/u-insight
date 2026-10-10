// Funciones de validación de los formularios de Intervenciones (sin
// dependencias del DOM).
// Uso en el navegador: agregar <script src="src/js/utils/validaciones.js"></script>
// antes del <script> de intervenciones.js (igual que los componentes de
// src/js/components/).
//
// Se exportan también vía module.exports al final del archivo para poder
// importarlas desde las pruebas (Vitest/Node) sin tocar el DOM. Eso no afecta
// el uso en el navegador: module no existe ahí, así que ese bloque se ignora.

const ESTADOS_INTERVENCION_SIN_SEGUIMIENTO = ["COMPLETED", "CANCELLED"];

function validarNuevaIntervencion(payload) {
  const errores = [];

  if (!payload.alertId) {
    errores.push("Debes seleccionar la alerta asociada.");
  }
  if (!payload.type) {
    errores.push("Debes seleccionar un tipo de intervención.");
  }
  if (!payload.responsible || payload.responsible.trim().length === 0) {
    errores.push("El responsable es obligatorio.");
  } else if (payload.responsible.trim().length > 150) {
    errores.push("El responsable no puede superar 150 caracteres.");
  }
  if (!payload.startDate) {
    errores.push("La fecha de inicio es obligatoria.");
  }
  if (!payload.description || payload.description.trim().length === 0) {
    errores.push("La descripción es obligatoria.");
  } else if (payload.description.trim().length > 500) {
    errores.push("La descripción no puede superar 500 caracteres.");
  }

  return errores;
}

function validarNuevoSeguimiento(payload, intervencion) {
  const errores = [];

  if (!payload.interventionId) {
    errores.push("Debes seleccionar la intervención a la que pertenece el seguimiento.");
  } else if (!intervencion) {
    errores.push("La intervención seleccionada ya no existe.");
  } else if (ESTADOS_INTERVENCION_SIN_SEGUIMIENTO.includes(intervencion.status)) {
    errores.push("No se pueden agregar seguimientos a una intervención completada o cancelada.");
  }

  if (!payload.followUpDate) {
    errores.push("La fecha de seguimiento es obligatoria.");
  } else if (intervencion && new Date(payload.followUpDate) < new Date(intervencion.startDate)) {
    errores.push("La fecha de seguimiento no puede ser anterior a la fecha de inicio de la intervención.");
  }

  if (!payload.observation || payload.observation.trim().length === 0) {
    errores.push("La observación es obligatoria.");
  }

  return errores;
}

if (typeof module !== "undefined" && module.exports) {
  module.exports = {
    ESTADOS_INTERVENCION_SIN_SEGUIMIENTO,
    validarNuevaIntervencion,
    validarNuevoSeguimiento,
  };
}
