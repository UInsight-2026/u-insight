// Logica de la vista Detalle de seccion (Semana 3)
// Consume detalle-seccion.mock.json (indicadores por seccion) y
// alertas.mock.json (para filtrar las alertas de la seccion seleccionada).
// Usa los componentes compartidos tarjeta.js (crearTarjeta / crearTarjetaAlerta)
// y tabla.js (crearTablaResultados).

const RUTA_MOCK_DETALLE = "src/data/detalle-seccion.mock.json";
const RUTA_MOCK_ALERTAS = "src/data/alertas.mock.json";

const CLASIFICACION_DISPERSION = {
  LOW_DISPERSION: "Dispersión baja",
  MODERATE_DISPERSION: "Dispersión moderada",
  HIGH_DISPERSION: "Dispersión alta",
};

const CLASIFICACION_TENDENCIA = {
  POSITIVE: "Ascendente",
  NEGATIVE: "Descendente",
  STABLE: "Estable",
  INSUFFICIENT_DATA: "Datos insuficientes",
};

const elementos = {
  selectorSeccion: document.getElementById("selector-seccion"),
  contenedorIndicadores: document.getElementById("contenedor-indicadores"),
  tablaEvaluaciones: document.getElementById("tabla-evaluaciones"),
  avisoEvaluaciones: document.getElementById("aviso-evaluaciones"),
  contenedorAlertas: document.getElementById("contenedor-alertas"),
};

let secciones = [];
let alertas = [];

function valorODisponible(valor) {
  return valor === null || valor === undefined ? "No disponible" : valor;
}

function formatearModa(moda) {
  if (!moda || !moda.length) {
    return "No disponible";
  }
  return moda.join(", ");
}

function formatearDispersion(dispersion) {
  if (!dispersion) {
    return "No disponible";
  }
  const clasificacion = CLASIFICACION_DISPERSION[dispersion.classification] || dispersion.classification;
  return `${dispersion.standardDeviation} (${clasificacion})`;
}

function formatearTendencia(tendencia) {
  if (!tendencia) {
    return "No disponible";
  }
  const clasificacion = CLASIFICACION_TENDENCIA[tendencia.classification] || tendencia.classification;
  if (tendencia.averageChange === null || tendencia.averageChange === undefined) {
    return clasificacion;
  }
  return `${clasificacion} (${tendencia.averageChange} pts promedio)`;
}

async function cargarDatos() {
  mostrarEstadoCarga();

  try {
    const [respuestaDetalle, respuestaAlertas] = await Promise.all([
      fetch(RUTA_MOCK_DETALLE),
      fetch(RUTA_MOCK_ALERTAS),
    ]);

    if (!respuestaDetalle.ok) {
      throw new Error(`No se pudo cargar el detalle de secciones (HTTP ${respuestaDetalle.status})`);
    }
    if (!respuestaAlertas.ok) {
      throw new Error(`No se pudo cargar el listado de alertas (HTTP ${respuestaAlertas.status})`);
    }

    secciones = await respuestaDetalle.json();
    alertas = await respuestaAlertas.json();

    console.info(`Detalle de secciones cargado: ${secciones.length} seccion(es) disponibles.`);

    poblarSelectorSecciones(secciones);

    if (secciones.length) {
      elementos.selectorSeccion.value = secciones[0].sectionId;
      renderizarSeccion(secciones[0].sectionId);
    }
  } catch (error) {
    console.error("Error al cargar el detalle de seccion:", error);
    mostrarEstadoError();
  }
}

function poblarSelectorSecciones(lista) {
  elementos.selectorSeccion.innerHTML = lista
    .map(
      (seccion) =>
        `<option value="${seccion.sectionId}">${seccion.courseName} · Sección ${seccion.sectionCode}</option>`
    )
    .join("");
}

function renderizarSeccion(sectionId) {
  const seccion = secciones.find((item) => item.sectionId === Number(sectionId));

  if (!seccion) {
    console.error(`No se encontro la seccion con id ${sectionId} en los datos simulados.`);
    mostrarEstadoError();
    return;
  }

  console.info(`Mostrando detalle de la seccion ${seccion.sectionCode} (${seccion.courseName}).`);

  renderizarIndicadores(seccion);
  renderizarEvaluaciones(seccion.trend);
  renderizarAlertas(seccion.sectionId);
}

function renderizarIndicadores(seccion) {
  const { centralTendency, dispersion, trend } = seccion;

  elementos.contenedorIndicadores.innerHTML = [
    crearTarjeta("Media", valorODisponible(centralTendency?.mean)),
    crearTarjeta("Mediana", valorODisponible(centralTendency?.median)),
    crearTarjeta("Moda", formatearModa(centralTendency?.mode)),
    crearTarjeta("Dispersión", formatearDispersion(dispersion)),
    crearTarjeta("Tendencia", formatearTendencia(trend)),
  ].join("");
}

function renderizarEvaluaciones(tendencia) {
  const columnas = [
    { campo: "label", titulo: "Evaluación" },
    { campo: "evaluationDate", titulo: "Fecha" },
    { campo: "value", titulo: "Nota", formatear: (valor) => valorODisponible(valor) },
    { campo: "present", titulo: "Presente", formatear: (valor) => (valor ? "Sí" : "No") },
  ];

  const puntos = tendencia?.points || [];
  elementos.tablaEvaluaciones.innerHTML = crearTablaResultados(
    columnas,
    puntos,
    "No hay evaluaciones registradas para esta sección."
  );

  const advertencias = tendencia?.warnings || [];
  elementos.avisoEvaluaciones.textContent = advertencias.join(" ");
  elementos.avisoEvaluaciones.hidden = advertencias.length === 0;
}

function renderizarAlertas(sectionId) {
  const alertasSeccion = alertas.filter((alerta) => alerta.sectionId === sectionId);

  if (!alertasSeccion.length) {
    elementos.contenedorAlertas.innerHTML = `<p class="mensaje-estado">No hay alertas asociadas a esta sección.</p>`;
    return;
  }

  elementos.contenedorAlertas.innerHTML = alertasSeccion.map((alerta) => crearTarjetaAlerta(alerta)).join("");
}

function mostrarEstadoCarga() {
  elementos.contenedorIndicadores.innerHTML = `<p class="mensaje-estado">Cargando indicadores...</p>`;
  elementos.contenedorAlertas.innerHTML = `<p class="mensaje-estado">Cargando alertas...</p>`;
}

function mostrarEstadoError() {
  const mensaje =
    "No se pudieron cargar los datos de la sección. Verifica que los archivos mock existan y que la página se esté sirviendo desde un servidor local (no abierta directamente como archivo).";
  elementos.contenedorIndicadores.innerHTML = `<p class="mensaje-estado">${mensaje}</p>`;
  elementos.contenedorAlertas.innerHTML = "";
}

elementos.selectorSeccion.addEventListener("change", (evento) => {
  if (!evento.target.value) {
    return;
  }
  console.info(`Selector de seccion cambiado a sectionId=${evento.target.value}`);
  renderizarSeccion(evento.target.value);
});

document.addEventListener("DOMContentLoaded", cargarDatos);
