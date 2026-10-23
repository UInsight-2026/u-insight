// Logica de la vista Detalle de seccion (Semana 4: conexion a la API real)
// GET {API_BASE}/analytics/sections/{id}/summary (celula de analytics) reemplaza
// a detalle-seccion.mock.json. Las alertas de la seccion ahora tambien vienen de
// la API real de C5 (GET /reports/alerts), igual que en alertas.js.
// Usa los componentes compartidos tarjeta.js (crearTarjeta / crearTarjetaAlerta),
// tabla.js (crearTablaResultados) y utils/formatters.js (valorODisponible,
// formatearModa, formatearDispersion, formatearTendencia — Semana 5: se movieron
// ahí para poder probarlas con Vitest sin depender del DOM).

const API_BASE = "/api/v1";

// El selector se llena con el catalogo real (api/secciones.js: GET /sections
// + GET /courses), igual que evolucion.js.
let catalogoSecciones = [];

const elementos = {
  selectorSeccion: document.getElementById("selector-seccion"),
  contenedorIndicadores: document.getElementById("contenedor-indicadores"),
  tablaEvaluaciones: document.getElementById("tabla-evaluaciones"),
  avisoEvaluaciones: document.getElementById("aviso-evaluaciones"),
  contenedorAlertas: document.getElementById("contenedor-alertas"),
};

let alertas = [];

async function inicializar() {
  mostrarEstadoCarga();
  try {
    [catalogoSecciones] = await Promise.all([cargarCatalogoSecciones(API_BASE), cargarAlertas()]);
  } catch (error) {
    console.error("DETALLE_SECCION_CATALOGO_ERROR", error);
    mostrarEstadoError("No se pudo cargar el listado de secciones. Intenta de nuevo más tarde.");
    return;
  }

  poblarSelectorSecciones(catalogoSecciones);

  if (!catalogoSecciones.length) {
    mostrarEstadoError("No hay secciones registradas.");
    return;
  }

  const primeraSeccion = catalogoSecciones[0].sectionId;
  elementos.selectorSeccion.value = primeraSeccion;
  await cargarYRenderizarSeccion(primeraSeccion);
}

async function cargarAlertas() {
  try {
    // size=100: el endpoint pagina (default 20); igual que en alertas.js,
    // para esta vista todavia no implementamos controles de pagina en el UI.
    const respuesta = await fetch(`${API_BASE}/reports/alerts?size=100`);
    if (!respuesta.ok) {
      throw new Error(`No se pudo cargar el listado de alertas (HTTP ${respuesta.status})`);
    }
    const datos = await respuesta.json();
    alertas = datos.alerts || [];
  } catch (error) {
    console.error("DETALLE_SECCION_ALERTAS_ERROR", error);
    alertas = [];
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

async function cargarYRenderizarSeccion(sectionId) {
  mostrarEstadoCarga();
  console.info("DETALLE_SECCION_CARGA_INICIADA", sectionId);

  try {
    const respuesta = await fetch(`${API_BASE}/analytics/sections/${sectionId}/summary`);
    if (!respuesta.ok) {
      console.error("DETALLE_SECCION_CARGA_ERROR", sectionId, `HTTP ${respuesta.status}`);
      mostrarEstadoError(mensajeErrorHttp(respuesta.status, "indicadores"));
      return;
    }

    const resumen = await respuesta.json();
    console.info("DETALLE_SECCION_CARGA_EXITOSA", sectionId, resumen);

    renderizarIndicadores(resumen);
    renderizarEvaluaciones(resumen.trendData);
    renderizarAlertas(sectionId);
  } catch (error) {
    console.error("DETALLE_SECCION_CARGA_ERROR", error);
    mostrarEstadoError(mensajeErrorHttp(null, "indicadores"));
  }
}

function renderizarIndicadores(resumen) {
  // Cualquiera de estos componentes puede venir null si esa celula fallo o
  // no respondio; valorODisponible/formatearModa/formatearDispersion/
  // formatearTendencia ya manejan ese caso sin leer propiedades de null.
  // Semana 4: la API real anida estos datos como centralTendencyData /
  // dispersionData / trendData (no centralTendency/dispersion/trend).
  const { centralTendencyData: centralTendency, dispersionData: dispersion, trendData: trend } = resumen || {};

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
  const alertasSeccion = alertas.filter((alerta) => alerta.sectionId === Number(sectionId));

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

function mostrarEstadoError(mensaje) {
  elementos.contenedorIndicadores.innerHTML = `<p class="mensaje-estado">${mensaje}</p>`;
  elementos.contenedorAlertas.innerHTML = "";
}

elementos.selectorSeccion.addEventListener("change", (evento) => {
  if (!evento.target.value) {
    return;
  }
  console.info("DETALLE_SECCION_CAMBIO_SECCION", evento.target.value);
  cargarYRenderizarSeccion(evento.target.value);
});

document.addEventListener("DOMContentLoaded", inicializar);
