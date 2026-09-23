// Lógica de la vista Intervenciones
//
// Consume intervenciones.mock.json (y alertas.mock.json, solo para poblar
// el selector de "alerta asociada" con alertas activas y mostrar su
// curso/sección). Reutiliza los componentes ya existentes de
// src/js/components/ (crearTarjeta de tarjeta.js, crearTablaResultados de
// tabla.js) sin modificarlos.
//
// Reglas de negocio replicadas del backend (célula C4) a nivel de UI, ya
// que en esta etapa la vista trabaja solo contra los JSON mock, sin
// llamar a la API real:
//   - Tipo:     InterventionType   (TUTORING, MEETING, PARENT_CONTACT,
//                                   ACADEMIC_PLAN, REFERRAL, OTHER)
//   - Estado:   InterventionStatus (PLANNED, IN_PROGRESS, COMPLETED,
//                                   CANCELLED) — RN-4: solo se avanza,
//                                   nunca se retrocede ni se saltan pasos.
//   - Resultado de seguimiento: FollowUpResult (IMPROVED, NO_CHANGE,
//                                   WORSENED, PENDING), campo opcional.
//   - Solo se puede asociar una intervención a una alerta activa (no
//     RESOLVED ni DISMISSED), igual que valida AlertValidationPort en el
//     backend.

const TIPO_ETIQUETA = {
  TUTORING: "Tutoría",
  MEETING: "Reunión",
  PARENT_CONTACT: "Contacto con encargado",
  ACADEMIC_PLAN: "Plan académico",
  REFERRAL: "Derivación",
  OTHER: "Otro",
};

const ESTADO_INTERVENCION_ETIQUETA = {
  PLANNED: "Planificada",
  IN_PROGRESS: "En progreso",
  COMPLETED: "Completada",
  CANCELLED: "Cancelada",
};

const RESULTADO_ETIQUETA = {
  IMPROVED: "Mejoró",
  NO_CHANGE: "Sin cambios",
  WORSENED: "Empeoró",
  PENDING: "Pendiente",
};

const ESTADOS_ALERTA_INACTIVA = ["RESOLVED", "DISMISSED"];
const ESTADOS_INTERVENCION_SIN_SEGUIMIENTO = ["COMPLETED", "CANCELLED"];

const elementos = {
  mensajeCarga: document.getElementById("mensaje-carga"),
  contenido: document.getElementById("contenido-intervenciones"),

  contenedorResumen: document.getElementById("contenedor-resumen"),

  formIntervencion: document.getElementById("form-nueva-intervencion"),
  campoAlerta: document.getElementById("campo-alerta"),
  campoTipo: document.getElementById("campo-tipo"),
  campoResponsable: document.getElementById("campo-responsable"),
  campoFechaInicio: document.getElementById("campo-fecha-inicio"),
  campoDescripcion: document.getElementById("campo-descripcion"),
  contadorDescripcion: document.getElementById("contador-descripcion"),
  mensajeFormIntervencion: document.getElementById("mensaje-form-intervencion"),

  filtroEstado: document.getElementById("filtro-estado"),
  contadorHistorial: document.getElementById("contador-historial"),
  tablaHistorial: document.getElementById("tabla-historial"),

  formSeguimiento: document.getElementById("form-seguimiento"),
  campoIntervencion: document.getElementById("campo-intervencion"),
  campoFechaSeguimiento: document.getElementById("campo-fecha-seguimiento"),
  campoResultado: document.getElementById("campo-resultado"),
  campoObservacion: document.getElementById("campo-observacion"),
  mensajeFormSeguimiento: document.getElementById("mensaje-form-seguimiento"),
  detalleSeguimientos: document.getElementById("detalle-seguimientos"),
};

let intervenciones = [];
let alertas = [];
let siguienteIdIntervencion = 1;
let siguienteIdSeguimiento = 1;

async function cargarDatosMock() {
  try {
    const [respuestaIntervenciones, respuestaAlertas] = await Promise.all([
      fetch("src/data/intervenciones.mock.json"),
      fetch("src/data/alertas.mock.json"),
    ]);

    if (!respuestaIntervenciones.ok) {
      throw new Error(`No se pudo cargar intervenciones.mock.json (HTTP ${respuestaIntervenciones.status})`);
    }
    if (!respuestaAlertas.ok) {
      throw new Error(`No se pudo cargar alertas.mock.json (HTTP ${respuestaAlertas.status})`);
    }

    intervenciones = await respuestaIntervenciones.json();
    alertas = await respuestaAlertas.json();

    siguienteIdIntervencion = calcularSiguienteId(intervenciones);
    siguienteIdSeguimiento = calcularSiguienteIdSeguimiento(intervenciones);

    console.info("Datos simulados cargados:", { intervenciones, alertas });

    elementos.mensajeCarga.hidden = true;
    elementos.contenido.hidden = false;

    poblarSelectorAlertas();
    poblarSelectorIntervenciones();
    renderizarResumen();
    renderizarHistorial();
  } catch (error) {
    console.error("Error al cargar datos simulados:", error);
    elementos.mensajeCarga.textContent =
      "No se pudieron cargar los datos simulados. Verifica que intervenciones.mock.json y " +
      "alertas.mock.json existan y que la página se esté sirviendo desde un servidor local.";
    elementos.mensajeCarga.classList.add("mensaje-estado--error");
  }
}

function calcularSiguienteId(lista) {
  const maximo = lista.reduce((max, item) => Math.max(max, item.id), 0);
  return maximo + 1;
}

function calcularSiguienteIdSeguimiento(listaIntervenciones) {
  const maximo = listaIntervenciones
    .flatMap((intervencion) => intervencion.followUps || [])
    .reduce((max, seguimiento) => Math.max(max, seguimiento.id), 0);
  return maximo + 1;
}

// ---- Resumen -------------------------------------------------------------

function renderizarResumen() {
  const conteos = Object.keys(ESTADO_INTERVENCION_ETIQUETA).reduce((acumulado, estado) => {
    acumulado[estado] = intervenciones.filter((intervencion) => intervencion.status === estado).length;
    return acumulado;
  }, {});

  elementos.contenedorResumen.innerHTML = Object.entries(ESTADO_INTERVENCION_ETIQUETA)
    .map(([estado, etiqueta]) => crearTarjeta(etiqueta, conteos[estado] ?? 0))
    .join("");
}

// ---- Selector de alertas (solo activas) -----------------------------------

function poblarSelectorAlertas() {
  const alertasActivas = alertas.filter((alerta) => !ESTADOS_ALERTA_INACTIVA.includes(alerta.status));

  if (!alertasActivas.length) {
    elementos.campoAlerta.innerHTML = `<option value="">No hay alertas activas</option>`;
    return;
  }

  elementos.campoAlerta.innerHTML =
    `<option value="">Selecciona…</option>` +
    alertasActivas
      .map(
        (alerta) =>
          `<option value="${alerta.id}">#${alerta.id} · ${alerta.courseName} (Sección ${alerta.sectionCode})</option>`
      )
      .join("");
}

// ---- Historial -------------------------------------------------------------

function renderizarHistorial() {
  const estadoSeleccionado = elementos.filtroEstado.value;
  const filas = intervenciones
    .filter((intervencion) => estadoSeleccionado === "" || intervencion.status === estadoSeleccionado)
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .map((intervencion) => ({
      id: intervencion.id,
      tipo: intervencion.type,
      descripcion: intervencion.description,
      responsable: intervencion.responsible,
      fechaInicio: intervencion.startDate,
      estado: intervencion.status,
      seguimientos: (intervencion.followUps || []).length,
    }));

  elementos.contadorHistorial.textContent = `${filas.length} resultado(s)`;

  elementos.tablaHistorial.innerHTML = crearTablaResultados(
    [
      { campo: "id", titulo: "ID" },
      {
        campo: "tipo",
        titulo: "Tipo",
        formatear: (valor) =>
          `<span class="badge tipo-${(valor || "").toLowerCase()}">${TIPO_ETIQUETA[valor] || valor}</span>`,
      },
      { campo: "descripcion", titulo: "Descripción" },
      { campo: "responsable", titulo: "Responsable" },
      { campo: "fechaInicio", titulo: "Fecha inicio" },
      {
        campo: "estado",
        titulo: "Estado",
        formatear: (valor) =>
          `<span class="badge estado-intervencion-${(valor || "").toLowerCase()}">${
            ESTADO_INTERVENCION_ETIQUETA[valor] || valor
          }</span>`,
      },
      { campo: "seguimientos", titulo: "Seguimientos" },
    ],
    filas,
    "No hay intervenciones que coincidan con el filtro seleccionado."
  );
}

function filtrarHistorial() {
  renderizarHistorial();
}

// ---- Crear intervención -----------------------------------------------------

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

function manejarEnvioIntervencion(evento) {
  evento.preventDefault();

  const payload = {
    alertId: elementos.campoAlerta.value,
    type: elementos.campoTipo.value,
    responsible: elementos.campoResponsable.value,
    startDate: elementos.campoFechaInicio.value,
    description: elementos.campoDescripcion.value,
  };

  const errores = validarNuevaIntervencion(payload);
  if (errores.length > 0) {
    console.error("Acción rechazada: creación de intervención inválida.", errores);
    mostrarMensajeForm(elementos.mensajeFormIntervencion, errores.join(" "), false);
    return;
  }

  const nuevaIntervencion = {
    id: siguienteIdIntervencion++,
    alertId: Number(payload.alertId),
    type: payload.type,
    description: payload.description.trim(),
    responsible: payload.responsible.trim(),
    startDate: payload.startDate,
    status: "PLANNED",
    createdAt: new Date().toISOString(),
    followUps: [],
  };

  intervenciones.unshift(nuevaIntervencion);
  console.info("Intervención registrada:", nuevaIntervencion);

  elementos.formIntervencion.reset();
  elementos.contadorDescripcion.textContent = "0 / 500";
  mostrarMensajeForm(elementos.mensajeFormIntervencion, `Intervención #${nuevaIntervencion.id} registrada correctamente.`, true);

  poblarSelectorIntervenciones();
  renderizarResumen();
  renderizarHistorial();
}

// ---- Selector de intervenciones para seguimiento ----------------------------

function poblarSelectorIntervenciones() {
  const disponibles = intervenciones.filter(
    (intervencion) => !ESTADOS_INTERVENCION_SIN_SEGUIMIENTO.includes(intervencion.status)
  );

  if (!disponibles.length) {
    elementos.campoIntervencion.innerHTML = `<option value="">No hay intervenciones abiertas</option>`;
    elementos.detalleSeguimientos.innerHTML = "";
    return;
  }

  elementos.campoIntervencion.innerHTML =
    `<option value="">Selecciona…</option>` +
    disponibles
      .map(
        (intervencion) =>
          `<option value="${intervencion.id}">#${intervencion.id} · ${TIPO_ETIQUETA[intervencion.type] || intervencion.type} — ${intervencion.responsible}</option>`
      )
      .join("");
}

function mostrarSeguimientosDeIntervencionSeleccionada() {
  const idSeleccionado = Number(elementos.campoIntervencion.value);
  const intervencion = intervenciones.find((item) => item.id === idSeleccionado);

  if (!intervencion || !(intervencion.followUps || []).length) {
    elementos.detalleSeguimientos.innerHTML = "";
    return;
  }

  const items = intervencion.followUps
    .slice()
    .sort((a, b) => new Date(a.followUpDate) - new Date(b.followUpDate))
    .map((seguimiento) => {
      const badgeResultado = seguimiento.result
        ? `<span class="badge resultado-${seguimiento.result.toLowerCase()}">${
            RESULTADO_ETIQUETA[seguimiento.result] || seguimiento.result
          }</span>`
        : "";
      return `<li><strong>${seguimiento.followUpDate}</strong> — ${seguimiento.observation} ${badgeResultado}</li>`;
    })
    .join("");

  elementos.detalleSeguimientos.innerHTML = `
    <h3>Seguimientos registrados</h3>
    <ul class="lista-seguimientos">${items}</ul>
  `;
}

// ---- Registrar seguimiento --------------------------------------------------

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

function manejarEnvioSeguimiento(evento) {
  evento.preventDefault();

  const payload = {
    interventionId: elementos.campoIntervencion.value,
    followUpDate: elementos.campoFechaSeguimiento.value,
    result: elementos.campoResultado.value,
    observation: elementos.campoObservacion.value,
  };

  const intervencion = intervenciones.find((item) => item.id === Number(payload.interventionId));

  const errores = validarNuevoSeguimiento(payload, intervencion);
  if (errores.length > 0) {
    console.error("Acción rechazada: registro de seguimiento inválido.", errores);
    mostrarMensajeForm(elementos.mensajeFormSeguimiento, errores.join(" "), false);
    return;
  }

  const nuevoSeguimiento = {
    id: siguienteIdSeguimiento++,
    interventionId: intervencion.id,
    followUpDate: payload.followUpDate,
    observation: payload.observation.trim(),
    result: payload.result || null,
  };

  intervencion.followUps = intervencion.followUps || [];
  intervencion.followUps.push(nuevoSeguimiento);

  // RN-4 (replicada a nivel de UI): una intervención planificada pasa a
  // "en progreso" en cuanto recibe su primer seguimiento.
  if (intervencion.status === "PLANNED") {
    intervencion.status = "IN_PROGRESS";
  }

  console.info("Seguimiento registrado:", nuevoSeguimiento);

  elementos.formSeguimiento.reset();
  mostrarMensajeForm(elementos.mensajeFormSeguimiento, `Seguimiento agregado a la intervención #${intervencion.id}.`, true);

  poblarSelectorIntervenciones();
  renderizarResumen();
  renderizarHistorial();
  elementos.detalleSeguimientos.innerHTML = "";
}

// ---- Utilidades de UI --------------------------------------------------------

function mostrarMensajeForm(elemento, texto, esExito) {
  elemento.textContent = texto;
  elemento.hidden = false;
  elemento.classList.remove("mensaje-form--ok", "mensaje-form--error");
  elemento.classList.add(esExito ? "mensaje-form--ok" : "mensaje-form--error");
}

function actualizarContadorDescripcion() {
  elementos.contadorDescripcion.textContent = `${elementos.campoDescripcion.value.length} / 500`;
}

// ---- Listeners ----------------------------------------------------------------

elementos.filtroEstado.addEventListener("change", filtrarHistorial);
elementos.formIntervencion.addEventListener("submit", manejarEnvioIntervencion);
elementos.formSeguimiento.addEventListener("submit", manejarEnvioSeguimiento);
elementos.campoIntervencion.addEventListener("change", mostrarSeguimientosDeIntervencionSeleccionada);
elementos.campoDescripcion.addEventListener("input", actualizarContadorDescripcion);

document.addEventListener("DOMContentLoaded", cargarDatosMock);
