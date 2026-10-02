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
 * Arma el HTML de una tarjeta de alerta a partir de un objeto con
 * la forma de alertas.mock.json (id, sectionCode, courseName, type,
 * severity, status, message, createdAt).
 */
function crearTarjetaAlerta(alerta) {
  const severidadClase = `severidad-${(alerta.severity || "").toLowerCase()}`;
  const estadoClase = `estado-alerta-${(alerta.status || "").toLowerCase()}`;
  const severidadTexto = SEVERIDAD_ETIQUETA[alerta.severity] || alerta.severity;
  const estadoTexto = ESTADO_ETIQUETA[alerta.status] || alerta.status;

  return `
    <div class="card card-alerta">
      <div class="card-alerta__encabezado">
        <span class="badge ${severidadClase}">${severidadTexto}</span>
        <span class="badge ${estadoClase}">${estadoTexto}</span>
      </div>
      <h3>${alerta.courseName || ""} · Sección ${alerta.sectionCode || ""}</h3>
      <p>${alerta.message || ""}</p>
    </div>
  `;
}
