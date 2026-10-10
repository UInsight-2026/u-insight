// Funciones de formato compartidas (sin dependencias del DOM).
// Uso en el navegador: agregar <script src="src/js/utils/formatters.js"></script>
// antes del <script> de la página que las use (igual que los componentes de
// src/js/components/).
//
// Se exportan también vía module.exports al final del archivo para poder
// importarlas desde las pruebas (Vitest/Node) sin tocar el DOM. Eso no afecta
// el uso en el navegador: module no existe ahí, así que ese bloque se ignora.

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

/**
 * Vista legible de un valor que puede venir null/undefined desde la API
 * (componente de analítica no disponible). Usado por varias vistas.
 */
function valorODisponible(valor) {
  return valor === null || valor === undefined ? "No disponible" : valor;
}

/**
 * La moda puede venir como arreglo vacío (sin moda) o con varios valores
 * (multimodal). Ver B1 - celula de tendencia central.
 */
function formatearModa(moda) {
  if (!moda || !moda.length) {
    return "No disponible";
  }
  return moda.join(", ");
}

/** dispersion puede venir null si B3 no respondió o no tiene datos suficientes. */
function formatearDispersion(dispersion) {
  if (!dispersion) {
    return "No disponible";
  }
  const clasificacion = CLASIFICACION_DISPERSION[dispersion.classification] || dispersion.classification;
  return `${dispersion.standardDeviation} (${clasificacion})`;
}

/**
 * tendencia puede venir null (B4 no respondió), o con averageChange null
 * (clasificación INSUFFICIENT_DATA: hay clasificación pero no cambio promedio).
 */
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

if (typeof module !== "undefined" && module.exports) {
  module.exports = {
    CLASIFICACION_DISPERSION,
    CLASIFICACION_TENDENCIA,
    valorODisponible,
    formatearModa,
    formatearDispersion,
    formatearTendencia,
  };
}
