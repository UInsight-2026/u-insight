// Logica de la vista Inicio (Semana 4: conexion a la API real)
// GET {API_BASE}/reports/overview (celula C5) reemplaza a inicio.mock.json.
// Usa crearTarjeta() del componente compartido tarjeta.js.

const API_BASE = "http://localhost:8080/api/v1";

const TENDENCIA_ETIQUETA = {
  POSITIVE: "Positiva",
  NEGATIVE: "Negativa",
  STABLE: "Estable",
  INSUFFICIENT_DATA: "Datos insuficientes",
};

const elementos = {
  contenedor: document.getElementById("resumen-container"),
};

async function cargarResumen() {
  mostrarEstadoCarga();
  console.info("INICIO_CARGA_INICIADA");

  try {
    const respuesta = await fetch(`${API_BASE}/reports/overview`);
    if (!respuesta.ok) {
      throw new Error(`La API respondió un error (HTTP ${respuesta.status})`);
    }

    const datos = await respuesta.json();
    console.info("INICIO_CARGA_EXITOSA", datos);
    renderizarResumen(datos);
  } catch (error) {
    console.error("INICIO_CARGA_ERROR", error);
    mostrarEstadoError();
  }
}

function renderizarResumen(datos) {
  const tendenciaTexto = TENDENCIA_ETIQUETA[datos.overallTrend] || datos.overallTrend || "No disponible";

  elementos.contenedor.innerHTML = `
    <div class="card-container">
      ${crearTarjeta("Alertas activas", datos.activeAlerts ?? "No disponible")}
      ${crearTarjeta("Secciones en riesgo", datos.highRiskSections ?? "No disponible")}
      ${crearTarjeta("Estudiantes en riesgo", datos.studentsAtRisk ?? "No disponible")}
      ${crearTarjeta("Tendencia general", tendenciaTexto)}
    </div>
  `;

  const fuentesNoDisponibles = datos.unavailableSources || [];
  if (fuentesNoDisponibles.length) {
    elementos.contenedor.innerHTML += `
      <p class="mensaje-estado">
        Algunos componentes no estuvieron disponibles al calcular este resumen: ${fuentesNoDisponibles.join(", ")}.
      </p>
    `;
  }
}

function mostrarEstadoCarga() {
  elementos.contenedor.innerHTML = `<p class="mensaje-estado">Cargando resumen...</p>`;
}

function mostrarEstadoError() {
  elementos.contenedor.innerHTML = `
    <p class="mensaje-estado">
      No se pudo cargar el resumen general. Verifica que el backend esté
      corriendo en ${API_BASE} y que no esté bloqueando la petición por CORS
      (revisa la consola del navegador).
    </p>`;
}

document.addEventListener("DOMContentLoaded", cargarResumen);
