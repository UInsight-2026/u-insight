// Logica de la vista Intervenciones — conexion a la API real de C3/C4.
// Ya no se leen intervenciones.mock.json ni alertas.mock.json.
//
// El backend todavia no expone un GET /api/v1/interventions general, asi
// que el "listado general" y el "resumen por estado" se arman en el
// cliente: se listan todas las alertas reales (GET /api/v1/alerts, C3) y
// se consulta, para cada una, sus intervenciones
// (GET /api/v1/alerts/{id}/interventions, C4). Cuando exista un listado
// general del lado del backend, este archivo puede simplificarse a una
// sola llamada.

const API_BASE = "/api/v1";

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

let intervenciones = []; // aplanado de todas las alertas conocidas, con seguimientosCount
let alertasActivas = []; // para el selector de "Alerta asociada" del formulario de creación

async function cargarDatos() {
  try {
    console.info("INTERVENCIONES_CARGA_INICIADA");

    const respuestaAlertas = await fetch(`${API_BASE}/alerts`);
    if (!respuestaAlertas.ok) {
      throw new Error(`No se pudo cargar el listado de alertas (HTTP ${respuestaAlertas.status})`);
    }
    const todasLasAlertas = await respuestaAlertas.json();
    alertasActivas = todasLasAlertas.filter((alerta) => !ESTADOS_ALERTA_INACTIVA.includes(alerta.status));

    const listasPorAlerta = await Promise.all(
      todasLasAlertas.map((alerta) =>
        fetch(`${API_BASE}/alerts/${alerta.id}/interventions`)
          .then((respuesta) => (respuesta.ok ? respuesta.json() : []))
          .catch(() => [])
      )
    );
    const planas = listasPorAlerta.flat();

    // Seguimientos por intervención: un GET adicional por cada una, para
    // poder mostrar el conteo en el historial sin inventar el dato.
    const conteosSeguimientos = await Promise.all(
      planas.map((intervencion) =>
        fetch(`${API_BASE}/interventions/${intervencion.id}/follow-ups`)
          .then((respuesta) => (respuesta.ok ? respuesta.json() : []))
          .then((lista) => lista.length)
          .catch(() => 0)
      )
    );
    intervenciones = planas.map((intervencion, indice) => ({
      ...intervencion,
      seguimientosCount: conteosSeguimientos[indice],
    }));

    console.info("INTERVENCIONES_CARGA_EXITOSA", intervenciones.length, "intervenciones");

    elementos.mensajeCarga.hidden = true;
    elementos.contenido.hidden = false;

    poblarSelectorAlertas();
    poblarSelectorIntervenciones();
    renderizarResumen();
    renderizarHistorial();
  } catch (error) {
    console.error("INTERVENCIONES_CARGA_ERROR", error);
    elementos.mensajeCarga.textContent =
      "No se pudieron cargar los datos. Verifica que el backend esté corriendo y que no esté " +
      "bloqueando la petición por CORS (revisa la consola del navegador).";
    elementos.mensajeCarga.classList.add("mensaje-estado--error");
  }
}

function renderizarResumen() {
  const conteos = Object.keys(ESTADO_INTERVENCION_ETIQUETA).reduce((acumulado, estado) => {
    acumulado[estado] = intervenciones.filter((intervencion) => intervencion.status === estado).length;
    return acumulado;
  }, {});

  elementos.contenedorResumen.innerHTML = Object.entries(ESTADO_INTERVENCION_ETIQUETA)
    .map(([estado, etiqueta]) => crearTarjeta(etiqueta, conteos[estado] ?? 0))
    .join("");
}

function poblarSelectorAlertas() {
  if (!alertasActivas.length) {
    elementos.campoAlerta.innerHTML = `<option value="">No hay alertas activas</option>`;
    return;
  }

  elementos.campoAlerta.innerHTML =
    `<option value="">Selecciona…</option>` +
    alertasActivas
      .map(
        (alerta) =>
          `<option value="${alerta.id}">#${alerta.id} · Sección ${alerta.sectionId} — ${
            alerta.title || alerta.alertType || "Alerta"
          }</option>`
      )
      .join("");
}

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
      seguimientos: intervencion.seguimientosCount ?? 0,
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

async function manejarEnvioIntervencion(evento) {
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

  try {
    const respuesta = await fetch(`${API_BASE}/alerts/${payload.alertId}/interventions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        type: payload.type,
        description: payload.description.trim(),
        responsible: payload.responsible.trim(),
        startDate: payload.startDate,
      }),
    });

    if (!respuesta.ok) {
      const cuerpoError = await respuesta.json().catch(() => null);
      throw new Error(cuerpoError?.message || `La API respondió un error (HTTP ${respuesta.status})`);
    }

    const nuevaIntervencion = await respuesta.json();
    intervenciones.unshift({ ...nuevaIntervencion, seguimientosCount: 0 });
    console.info("INTERVENCION_REGISTRADA", nuevaIntervencion.id);

    elementos.formIntervencion.reset();
    elementos.contadorDescripcion.textContent = "0 / 500";
    mostrarMensajeForm(
      elementos.mensajeFormIntervencion,
      `Intervención #${nuevaIntervencion.id} registrada correctamente.`,
      true
    );

    poblarSelectorIntervenciones();
    renderizarResumen();
    renderizarHistorial();
  } catch (error) {
    console.error("INTERVENCIONES_CREAR_ERROR", error);
    mostrarMensajeForm(
      elementos.mensajeFormIntervencion,
      error.message || "No se pudo registrar la intervención.",
      false
    );
  }
}

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
          `<option value="${intervencion.id}">#${intervencion.id} · ${
            TIPO_ETIQUETA[intervencion.type] || intervencion.type
          } — ${intervencion.responsible}</option>`
      )
      .join("");
}

async function mostrarSeguimientosDeIntervencionSeleccionada() {
  const idSeleccionado = Number(elementos.campoIntervencion.value);
  if (!idSeleccionado) {
    elementos.detalleSeguimientos.innerHTML = "";
    return;
  }

  try {
    const respuesta = await fetch(`${API_BASE}/interventions/${idSeleccionado}/follow-ups`);
    if (!respuesta.ok) {
      throw new Error(`HTTP ${respuesta.status}`);
    }
    const seguimientos = await respuesta.json();

    if (!seguimientos.length) {
      elementos.detalleSeguimientos.innerHTML = "";
      return;
    }

    const items = seguimientos
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
  } catch (error) {
    console.error("INTERVENCIONES_SEGUIMIENTOS_ERROR", error);
    elementos.detalleSeguimientos.innerHTML =
      `<p class="mensaje-estado">No se pudieron cargar los seguimientos.</p>`;
  }
}

async function manejarEnvioSeguimiento(evento) {
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

  try {
    const respuesta = await fetch(`${API_BASE}/interventions/${payload.interventionId}/follow-ups`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        followUpDate: payload.followUpDate,
        observation: payload.observation.trim(),
        result: payload.result || null,
      }),
    });

    if (!respuesta.ok) {
      const cuerpoError = await respuesta.json().catch(() => null);
      throw new Error(cuerpoError?.message || `HTTP ${respuesta.status}`);
    }

    const nuevoSeguimiento = await respuesta.json();
    console.info("SEGUIMIENTO_REGISTRADO", nuevoSeguimiento.id);

    // Nota: a diferencia de la simulacion anterior, el backend real NO
    // cambia automaticamente el estado de la intervencion a IN_PROGRESS al
    // agregar un seguimiento. Ese cambio de estado se hace aparte con
    // PATCH /api/v1/interventions/{id}/status.
    elementos.formSeguimiento.reset();
    mostrarMensajeForm(
      elementos.mensajeFormSeguimiento,
      `Seguimiento agregado a la intervención #${payload.interventionId}.`,
      true
    );

    const intervencionLocal = intervenciones.find((item) => item.id === Number(payload.interventionId));
    if (intervencionLocal) {
      intervencionLocal.seguimientosCount = (intervencionLocal.seguimientosCount ?? 0) + 1;
    }
    renderizarHistorial();
    await mostrarSeguimientosDeIntervencionSeleccionada();
  } catch (error) {
    console.error("INTERVENCIONES_SEGUIMIENTO_ERROR", error);
    mostrarMensajeForm(
      elementos.mensajeFormSeguimiento,
      error.message || "No se pudo registrar el seguimiento.",
      false
    );
  }
}

function mostrarMensajeForm(elemento, texto, esExito) {
  elemento.textContent = texto;
  elemento.hidden = false;
  elemento.classList.remove("mensaje-form--ok", "mensaje-form--error");
  elemento.classList.add(esExito ? "mensaje-form--ok" : "mensaje-form--error");
}

function actualizarContadorDescripcion() {
  elementos.contadorDescripcion.textContent = `${elementos.campoDescripcion.value.length} / 500`;
}

elementos.filtroEstado.addEventListener("change", filtrarHistorial);
elementos.formIntervencion.addEventListener("submit", manejarEnvioIntervencion);
elementos.formSeguimiento.addEventListener("submit", manejarEnvioSeguimiento);
elementos.campoIntervencion.addEventListener("change", mostrarSeguimientosDeIntervencionSeleccionada);
elementos.campoDescripcion.addEventListener("input", actualizarContadorDescripcion);

document.addEventListener("DOMContentLoaded", cargarDatos);
