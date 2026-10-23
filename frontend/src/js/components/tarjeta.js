// Componentes reutilizables: tarjetas (cards)
// Uso: agregar <script src="src/js/components/tarjeta.js"></script>
// antes del <script> de la página que las use.

/**
 * Arma el HTML de una tarjeta simple de indicador (título + valor).
 * Reemplaza la función local que cada vista repetía por su cuenta.
 *
 * @param {string} titulo
 * @param {string|number} valor
 * @param {"neutral"|"bajo"|"medio"|"alto"} variante Color de acento según
 *   nivel de riesgo. "neutral" (por defecto) usa el dorado de marca; se usa
 *   en vistas sin semántica de riesgo (Detalle de sección, Intervenciones).
 */
function crearTarjeta(titulo, valor, variante = "neutral") {
  return `
    <div class="card stat-card stat-card--${variante}">
      <h3>${titulo}</h3>
      <p>${valor}</p>
    </div>
  `;
}

const SEVERIDAD_ETIQUETA = {
  HIGH: "Alta",
  MEDIUM: "Media",
  LOW: "Baja",
};

const ESTADO_ETIQUETA = {
  NEW: "Nueva",
  UNDER_REVIEW: "En revisión",
  IN_PROGRESS: "En progreso",
  RESOLVED: "Resuelta",
  DISMISSED: "Descartada",
};

/**
 * Arma el HTML de una tarjeta de alerta a partir de un AlertItemResponse
 * real de GET /api/v1/reports/alerts (célula C5): id, sectionId,
 * courseCode, type, riskLevel, status, title, generatedAt.
 *
 * Nota: el endpoint real no trae sectionCode (solo sectionId numérico),
 * ni un campo "message" — el texto visible es "title".
 */
function crearTarjetaAlerta(alerta) {
  const severidadClase = `severidad-${(alerta.riskLevel || "").toLowerCase()}`;
  const estadoClase = `estado-alerta-${(alerta.status || "").toLowerCase()}`;
  const severidadTexto = SEVERIDAD_ETIQUETA[alerta.riskLevel] || alerta.riskLevel;
  const estadoTexto = ESTADO_ETIQUETA[alerta.status] || alerta.status;

  const encabezadoSeccion = alerta.courseCode
    ? `${alerta.courseCode} · Sección ${alerta.sectionId ?? "—"}`
    : `Sección ${alerta.sectionId ?? "—"}`;

  return `
    <div class="card card-alerta">
      <div class="card-alerta__encabezado">
        <span class="badge ${severidadClase}">${severidadTexto}</span>
        <span class="badge ${estadoClase}">${estadoTexto}</span>
      </div>
      <h3>${encabezadoSeccion}</h3>
      <p>${alerta.title || "Sin descripción"}</p>
    </div>
  `;
}
