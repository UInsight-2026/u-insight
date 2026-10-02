// Componentes reutilizables: tarjetas (cards)
// Uso: agregar <script src="src/js/components/tarjeta.js"></script>
// antes del <script> de la página que las use.

/**
 * Arma el HTML de una tarjeta simple de indicador (título + valor).
 * Reemplaza la función local que cada vista repetía por su cuenta.
 */
function crearTarjeta(titulo, valor) {
  return `
    <div class="card">
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

  return `
    <div class="card card-alerta">
      <div class="card-alerta__encabezado">
        <span class="badge ${severidadClase}">${severidadTexto}</span>
        <span class="badge ${estadoClase}">${estadoTexto}</span>
      </div>
      <h3>${alerta.courseCode || ""} · Sección ${alerta.sectionId ?? ""}</h3>
      <p>${alerta.title || ""}</p>
    </div>
  `;
}
