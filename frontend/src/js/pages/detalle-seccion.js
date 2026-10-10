// Logica de la vista Detalle de seccion (Semana 4: conexion a la API real)
// GET {API_BASE}/analytics/sections/{id}/summary (celula de analytics) reemplaza
// a detalle-seccion.mock.json. Las alertas de la seccion se siguen tomando de
// alertas.mock.json (eso no fue parte de este cambio).
// Usa los componentes compartidos tarjeta.js (crearTarjeta / crearTarjetaAlerta)
// y tabla.js (crearTablaResultados).

const API_BASE = "http://localhost:8080/api/v1";
const RUTA_MOCK_ALERTAS = "src/data/alertas.mock.json";

// TODO: todavia no existe un endpoint de catalogo de secciones para poblar
// el selector. Mientras tanto se deja este listado minimo (mismos ids que
// ya se usaban en detalle-seccion.mock.json). Reemplazar cuando haya un
// GET de secciones disponible.
const CATALOGO_SECCIONES = [
  { sectionId: 10, courseName: "Programación II", sectionCode: "A" },
  { sectionId: 15, courseName: "Cálculo II", sectionCode: "B" },
  { sectionId: 22, courseName: "Estadística", sectionCode: "A" },
];

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

async function inicializar() {
  poblarSelectorSecciones(CATALOGO_SECCIONES);
  await cargarAlertas();

  if (CATALOGO_SECCIONES.length) {
    const primeraSeccion = CATALOGO_SECCIONES[0].sectionId;
    elementos.selectorSeccion.value = primeraSeccion;
    await cargarYRenderizarSeccion(primeraSeccion);
  }
}

async function cargarAlertas() {
  try {
    const respuesta = await fetch(RUTA_MOCK_ALERTAS);
    if (!respuesta.ok) {
      throw new Error(`No se pudo cargar el listado de alertas (HTTP ${respuesta.status})`);
    }
    alertas = await respuesta.json();
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
      throw new Error(`La API respondió un error (HTTP ${respuesta.status})`);
    }

    const resumen = await respuesta.json();
    console.info("DETALLE_SECCION_CARGA_EXITOSA", sectionId, resumen);

    renderizarIndicadores(resumen);
    renderizarEvaluaciones(resumen.trendData);
    renderizarAlertas(sectionId);
  } catch (error) {
    console.error("DETALLE_SECCION_CARGA_ERROR", error);
    mostrarEstadoError();
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

function mostrarEstadoError() {
  const mensaje = `No se pudieron cargar los indicadores de la sección. Verifica que el backend esté corriendo en ${API_BASE} y que no esté bloqueando la petición por CORS (revisa la consola del navegador).`;
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
