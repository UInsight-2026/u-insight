// Logica de la vista Evolucion (Semana 4: conexion a la API real)
// GET {API_BASE}/analytics/sections/{id}/trends (celula B4) reemplaza a
// evolucion.mock.json. Sigue el mismo patron que detalle-seccion.js:
// selector de seccion + catalogo fijo mientras no exista un endpoint de
// catalogo de secciones.

const API_BASE = "http://localhost:8080/api/v1";

// TODO: igual que en detalle-seccion.js, reemplazar por un GET de
// secciones real cuando ese catalogo exista. Se usa la misma lista para
// que las dos vistas muestren las mismas secciones.
const CATALOGO_SECCIONES = [
  { sectionId: 10, courseName: "Programación II", sectionCode: "A" },
  { sectionId: 15, courseName: "Cálculo II", sectionCode: "B" },
  { sectionId: 22, courseName: "Estadística", sectionCode: "A" },
];

const CLASIFICACION_ETIQUETA = {
  POSITIVE: "Ascendente",
  NEGATIVE: "Descendente",
  STABLE: "Estable",
  INSUFFICIENT_DATA: "Datos insuficientes",
};

const elementos = {
  selectorSeccion: document.getElementById("selector-seccion"),
  contenedor: document.getElementById("evolucion-container"),
};

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
  console.info("EVOLUCION_CARGA_INICIADA", sectionId);

  try {
    const respuesta = await fetch(`${API_BASE}/analytics/sections/${sectionId}/trends`);
    if (!respuesta.ok) {
      throw new Error(`La API respondió un error (HTTP ${respuesta.status})`);
    }

    const tendencia = await respuesta.json();
    console.info("EVOLUCION_CARGA_EXITOSA", sectionId, tendencia);

    const seccion = CATALOGO_SECCIONES.find((s) => s.sectionId === Number(sectionId));
    renderizarEvolucion(seccion, tendencia);
  } catch (error) {
    console.error("EVOLUCION_CARGA_ERROR", error);
    mostrarEstadoError();
  }
}

function renderizarEvolucion(seccion, tendencia) {
  const clasificacion = tendencia?.classification || "INSUFFICIENT_DATA";
  const clasificacionTexto = CLASIFICACION_ETIQUETA[clasificacion] || clasificacion;
  const cambioPromedio = tendencia?.averageChange ?? "N/D";
  const titulo = seccion ? `${seccion.courseName} · Sección ${seccion.sectionCode}` : "Sección";

  const grafico = crearContenedorGrafico(titulo, tendencia?.points || []);

  elementos.contenedor.innerHTML = `
    <section class="evolucion-card">
      <div class="evolucion-info">
        <span class="clasificacion ${clasificacion.toLowerCase()}">${clasificacionTexto}</span>
        <span class="promedio">Cambio promedio: ${cambioPromedio}</span>
      </div>
      ${grafico}
    </section>
  `;
}

function mostrarEstadoCarga() {
  elementos.contenedor.innerHTML = `<p class="mensaje-estado">Cargando evolución...</p>`;
}

function mostrarEstadoError() {
  elementos.contenedor.innerHTML = `
    <p class="mensaje-estado">
      No se pudo cargar la evolución de esta sección. Verifica que el backend
      esté corriendo en ${API_BASE} y que no esté bloqueando la petición por
      CORS (revisa la consola del navegador).
    </p>`;
}

function inicializar() {
  poblarSelectorSecciones(CATALOGO_SECCIONES);

  if (CATALOGO_SECCIONES.length) {
    const primeraSeccion = CATALOGO_SECCIONES[0].sectionId;
    elementos.selectorSeccion.value = primeraSeccion;
    cargarYRenderizarSeccion(primeraSeccion);
  }
}

elementos.selectorSeccion.addEventListener("change", (evento) => {
  if (!evento.target.value) {
    return;
  }
  console.info("EVOLUCION_CAMBIO_SECCION", evento.target.value);
  cargarYRenderizarSeccion(evento.target.value);
});

document.addEventListener("DOMContentLoaded", inicializar);
