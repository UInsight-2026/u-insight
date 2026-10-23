// Catálogo de secciones para los selectores de Evolución y Detalle de sección.
// Reemplaza el CATALOGO_SECCIONES fijo (ids 10, 15 y 22, que no existen en la
// BD) por los datos reales de:
//   GET /api/v1/sections          (célula A4)
//   GET /api/v1/courses?size=100  (célula A1, para mostrar el nombre del curso)
//
// Uso en el navegador: agregar <script src="src/js/api/secciones.js"></script>
// antes del <script> de la página. Igual que utils/formatters.js, se exporta
// vía module.exports para probar armarCatalogoSecciones y mensajeErrorHttp con
// Vitest; en el navegador module no existe y ese bloque se ignora.

/**
 * Une secciones y cursos en la forma que usan los selectores:
 * { sectionId, courseName, sectionCode }, ordenado por curso y sección.
 * Si el curso no viene en la lista, se muestra "Curso #id".
 */
function armarCatalogoSecciones(secciones, cursos) {
  const cursosPorId = new Map((cursos || []).map((curso) => [curso.id, curso]));

  return (secciones || [])
    .map((seccion) => {
      const curso = cursosPorId.get(seccion.courseId);
      return {
        sectionId: seccion.id,
        courseName: curso ? curso.name : `Curso #${seccion.courseId}`,
        sectionCode: seccion.sectionCode,
      };
    })
    .sort(
      (a, b) =>
        a.courseName.localeCompare(b.courseName, "es") ||
        String(a.sectionCode).localeCompare(String(b.sectionCode), "es")
    );
}

/**
 * Mensaje para el usuario según el código HTTP real, en lugar de suponer
 * siempre un problema de CORS. status null/undefined = no hubo respuesta
 * (backend caído o red).
 */
function mensajeErrorHttp(status, recurso) {
  if (status === 404) {
    return `Sección sin datos de ${recurso}.`;
  }
  if (status >= 500) {
    return `El servidor falló al calcular ${recurso} (HTTP ${status}). Intenta de nuevo más tarde.`;
  }
  if (status) {
    return `No se pudo cargar ${recurso} (HTTP ${status}).`;
  }
  return `No se pudo conectar con el servidor para cargar ${recurso}.`;
}

/**
 * Pide secciones y cursos a la API y devuelve el catálogo armado.
 * Lanza Error si alguna de las dos llamadas falla.
 */
async function cargarCatalogoSecciones(apiBase) {
  const [seccionesResp, cursosResp] = await Promise.all([
    fetch(`${apiBase}/sections`),
    fetch(`${apiBase}/courses?size=100`),
  ]);

  if (!seccionesResp.ok || !cursosResp.ok) {
    throw new Error(
      `No se pudo cargar el catálogo de secciones (secciones ${seccionesResp.status}, cursos ${cursosResp.status})`
    );
  }

  const secciones = await seccionesResp.json();
  const cursosPagina = await cursosResp.json();
  return armarCatalogoSecciones(secciones, cursosPagina.content);
}

if (typeof module !== "undefined" && module.exports) {
  module.exports = {
    armarCatalogoSecciones,
    mensajeErrorHttp,
    cargarCatalogoSecciones,
  };
}
