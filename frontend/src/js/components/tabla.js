// Componentes reutilizables: tabla de resultados
// Uso: agregar <script src="src/js/components/tabla.js"></script>
// antes del <script> de la página que la use.

/**
 * Arma el <thead> + <tbody> de una tabla a partir de una configuración
 * de columnas y una lista de filas (objetos planos).
 *
 * @param {Array<{campo: string, titulo: string, formatear?: (valor: any, fila: object) => string}>} columnas
 * @param {Array<object>} filas
 * @param {string} mensajeVacio Texto a mostrar cuando no hay filas.
 * @returns {string} HTML listo para insertar dentro de un <table>.
 */
function crearTablaResultados(columnas, filas, mensajeVacio = "No hay registros para mostrar.") {
  const encabezado = `
    <thead>
      <tr>
        ${columnas.map((columna) => `<th>${columna.titulo}</th>`).join("")}
      </tr>
    </thead>
  `;

  if (!filas || !filas.length) {
    return `
      ${encabezado}
      <tbody>
        <tr><td colspan="${columnas.length}" class="mensaje-estado">${mensajeVacio}</td></tr>
      </tbody>
    `;
  }

  const cuerpo = filas
    .map((fila) => {
      const celdas = columnas
        .map((columna) => {
          const valor = fila[columna.campo];
          const contenido = columna.formatear ? columna.formatear(valor, fila) : valor;
          return `<td>${contenido ?? ""}</td>`;
        })
        .join("");
      return `<tr>${celdas}</tr>`;
    })
    .join("");

  return `
    ${encabezado}
    <tbody>${cuerpo}</tbody>
  `;
}
