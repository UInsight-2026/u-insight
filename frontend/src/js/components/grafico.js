// Componentes reutilizables: contenedor de gráfico
// Uso: agregar <script src="src/js/components/grafico.js"></script>
// antes del <script> de la página que lo use.
//
// No usa ninguna librería externa (el proyecto solo autoriza HTML/CSS/JS
// puro). Arma un gráfico de barras simple con <div> posicionados por
// porcentaje. Sirve como base; si más adelante se autoriza una librería
// de gráficos, solo hay que reemplazar el contenido de este archivo sin
// tocar las páginas que ya lo consumen.

/**
 * Arma el HTML de un gráfico de barras simple a partir de puntos
 * {label, value} (la misma forma que devuelve evolucion.mock.json).
 *
 * @param {string} titulo
 * @param {Array<{label: string, value: number}>} puntos
 * @returns {string} HTML del contenedor completo del gráfico.
 */
function crearContenedorGrafico(titulo, puntos) {
  if (!puntos || !puntos.length) {
    return `
      <div class="grafico-contenedor">
        <h3>${titulo}</h3>
        <p class="mensaje-estado">No hay suficientes datos para graficar.</p>
      </div>
    `;
  }

  const valores = puntos.map((punto) => punto.value).filter((valor) => valor !== null && valor !== undefined);
  const valorMaximo = Math.max(...valores, 1);

  const barras = puntos
    .map((punto) => {
      const alturaPorcentaje = punto.value === null || punto.value === undefined
        ? 0
        : Math.round((punto.value / valorMaximo) * 100);
      return `
        <div class="grafico-barra" title="${punto.label}: ${punto.value ?? "s/d"}">
          <div class="grafico-barra__relleno" style="height: ${alturaPorcentaje}%"></div>
          <span class="grafico-barra__valor">${punto.value ?? "s/d"}</span>
          <span class="grafico-barra__etiqueta">${punto.label}</span>
        </div>
      `;
    })
    .join("");

  return `
    <div class="grafico-contenedor">
      <h3>${titulo}</h3>
      <div class="grafico-barras">${barras}</div>
    </div>
  `;
}
