// Logica de la vista Inicio (Semana 4: conexion a la API real)
// GET {API_BASE}/reports/overview (celula C5) reemplaza a inicio.mock.json.
// Usa crearTarjeta() del componente compartido tarjeta.js.

const API_BASE = "/api/v1";

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

const TENDENCIA_VARIANTE = {
  POSITIVE: "bajo",
  STABLE: "medio",
  NEGATIVE: "alto",
};

/** Clasifica un conteo en bajo/medio/alto para colorear la tarjeta. */
function nivelPorConteo(valor, limiteMedio, limiteAlto) {
  if (typeof valor !== "number") {
    return "neutral";
  }
  if (valor >= limiteAlto) {
    return "alto";
  }
  if (valor >= limiteMedio) {
    return "medio";
  }
  return "bajo";
}

function renderizarResumen(datos) {
  const tendenciaTexto = TENDENCIA_ETIQUETA[datos.overallTrend] || datos.overallTrend || "No disponible";

  elementos.contenedor.innerHTML = `
    <div class="card-container">
      ${crearTarjeta("Alertas activas", datos.activeAlerts ?? "No disponible", nivelPorConteo(datos.activeAlerts, 1, 3))}
      ${crearTarjeta("Secciones en riesgo", datos.highRiskSections ?? "No disponible", nivelPorConteo(datos.highRiskSections, 1, 3))}
      ${crearTarjeta("Estudiantes en riesgo", datos.studentsAtRisk ?? "No disponible", nivelPorConteo(datos.studentsAtRisk, 1, 6))}
      ${crearTarjeta("Tendencia general", tendenciaTexto, TENDENCIA_VARIANTE[datos.overallTrend] || "neutral")}
    </div>
  `;

  // No se le muestra al usuario final qué célula/módulo interno falló (eso es
  // un detalle de implementación); solo que el resumen puede estar incompleto.
  const fuentesNoDisponibles = datos.unavailableSources || [];
  if (fuentesNoDisponibles.length) {
    console.warn("INICIO_FUENTES_NO_DISPONIBLES", fuentesNoDisponibles);
    elementos.contenedor.innerHTML += `
      <p class="mensaje-estado">
        Algunos indicadores podrían no reflejar la información más reciente.
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
