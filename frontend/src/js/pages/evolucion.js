// Logica de la vista Evolucion (Semana 4: conexion a la API real)
// GET {API_BASE}/analytics/sections/{id}/trends (celula B4) reemplaza a
// evolucion.mock.json. El selector de seccion se llena con el catalogo real
// (api/secciones.js: GET /sections + GET /courses), igual que detalle-seccion.js.

const API_BASE = "/api/v1";

let catalogoSecciones = [];

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
      console.error("EVOLUCION_CARGA_ERROR", sectionId, `HTTP ${respuesta.status}`);
      mostrarEstadoError(mensajeErrorHttp(respuesta.status, "evolución"));
      return;
    }

    const tendencia = await respuesta.json();
    console.info("EVOLUCION_CARGA_EXITOSA", sectionId, tendencia);

    const seccion = catalogoSecciones.find((s) => s.sectionId === Number(sectionId));
    renderizarEvolucion(seccion, tendencia);
  } catch (error) {
    console.error("EVOLUCION_CARGA_ERROR", error);
    mostrarEstadoError(mensajeErrorHttp(null, "evolución"));
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

function mostrarEstadoError(mensaje) {
  elementos.contenedor.innerHTML = `<p class="mensaje-estado">${mensaje}</p>`;
}

async function inicializar() {
  mostrarEstadoCarga();
  try {
    catalogoSecciones = await cargarCatalogoSecciones(API_BASE);
  } catch (error) {
    console.error("EVOLUCION_CATALOGO_ERROR", error);
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
  cargarYRenderizarSeccion(primeraSeccion);
}

elementos.selectorSeccion.addEventListener("change", (evento) => {
  if (!evento.target.value) {
    return;
  }
  console.info("EVOLUCION_CAMBIO_SECCION", evento.target.value);
  cargarYRenderizarSeccion(evento.target.value);
});

document.addEventListener("DOMContentLoaded", inicializar);
