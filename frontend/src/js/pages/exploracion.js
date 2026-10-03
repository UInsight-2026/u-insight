// Logica de la vista Exploracion academica (Semana 4: conexion a la API real)
//
// Combina 3 endpoints reales (no hay un endpoint unico de exploracion):
//   GET /api/v1/academic-periods?size=100  (celula A1)
//   GET /api/v1/sections                   (celula A4)
//   GET /api/v1/teachers                   (celula A2)
// y arma localmente una fila por seccion.
//
// Limitacion conocida: los modulos de curso (A1) y estudiante (A3) todavia
// no exponen API (solo tienen .gitkeep), asi que no hay nombre de curso ni
// las columnas de estudiante/carnet/nota del mock original. "Curso" se
// muestra como "Curso #<id>" hasta que A1 publique ese endpoint.

const API_BASE = "http://localhost:8080/api/v1";

const elementos = {
  contador: document.getElementById("contador-resultados"),
  cuerpoTabla: document.getElementById("cuerpo-tabla"),
  filtroPeriodo: document.getElementById("filtro-periodo"),
  filtroCurso: document.getElementById("filtro-curso"),
  filtroDocente: document.getElementById("filtro-docente"),
  filtroSeccion: document.getElementById("filtro-seccion"),
  btnBuscar: document.getElementById("btn-buscar"),
  btnLimpiar: document.getElementById("btn-limpiar"),
};

let registros = [];

async function cargarDatos() {
  console.info("EXPLORACION_CARGA_INICIADA");
  try {
    const [periodosResp, seccionesResp, docentesResp] = await Promise.all([
      fetch(`${API_BASE}/academic-periods?size=100`),
      fetch(`${API_BASE}/sections`),
      fetch(`${API_BASE}/teachers`),
    ]);

    if (!periodosResp.ok || !seccionesResp.ok || !docentesResp.ok) {
      throw new Error(
        `La API respondió un error (periodos ${periodosResp.status}, secciones ${seccionesResp.status}, docentes ${docentesResp.status})`
      );
    }

    const periodosPagina = await periodosResp.json();
    const secciones = await seccionesResp.json();
    const docentes = await docentesResp.json();

    const periodosPorId = new Map(periodosPagina.content.map((p) => [p.id, p]));
    const docentesPorId = new Map(docentes.map((d) => [d.id, d]));

    registros = secciones.map((seccion) => {
      const periodo = periodosPorId.get(seccion.academicPeriodId);
      const docente = docentesPorId.get(seccion.teacherId);
      return {
        periodo: periodo ? periodo.name : `Período #${seccion.academicPeriodId}`,
        curso: `Curso #${seccion.courseId}`,
        docente: docente ? docente.teacherName : `Docente #${seccion.teacherId}`,
        seccion: seccion.sectionCode,
        estado: seccion.status,
      };
    });

    poblarFiltros(registros);
    renderizarTabla(registros);
    console.info("EXPLORACION_CARGA_EXITOSA", registros.length, "secciones");
  } catch (error) {
    console.error("EXPLORACION_CARGA_ERROR", error);
    elementos.cuerpoTabla.innerHTML = `
      <tr>
        <td colspan="5" class="mensaje-estado">
          No se pudieron cargar los datos. Verifica que el backend esté corriendo en
          ${API_BASE} y que no esté bloqueando la petición por CORS (revisa la consola
          del navegador).
        </td>
      </tr>`;
    elementos.contador.textContent = "Error al cargar datos.";
  }
}

function valoresUnicos(lista, campo) {
  return [...new Set(lista.map((item) => item[campo]))].sort((a, b) =>
    String(a).localeCompare(String(b), "es")
  );
}

function poblarSelect(selectEl, valores) {
  valores.forEach((valor) => {
    const opcion = document.createElement("option");
    opcion.value = valor;
    opcion.textContent = valor;
    selectEl.appendChild(opcion);
  });
}

function poblarFiltros(lista) {
  poblarSelect(elementos.filtroPeriodo, valoresUnicos(lista, "periodo"));
  poblarSelect(elementos.filtroCurso, valoresUnicos(lista, "curso"));
  poblarSelect(elementos.filtroDocente, valoresUnicos(lista, "docente"));
  poblarSelect(elementos.filtroSeccion, valoresUnicos(lista, "seccion"));
}

function renderizarTabla(lista) {
  if (!lista.length) {
    elementos.cuerpoTabla.innerHTML = `
      <tr><td colspan="5" class="mensaje-estado">No hay secciones para mostrar.</td></tr>`;
    elementos.contador.textContent = "0 resultados";
    return;
  }

  elementos.cuerpoTabla.innerHTML = lista
    .map((registro) => {
      return `
        <tr>
          <td>${registro.periodo}</td>
          <td>${registro.curso}</td>
          <td>${registro.docente}</td>
          <td>${registro.seccion}</td>
          <td>${registro.estado}</td>
        </tr>`;
    })
    .join("");

  elementos.contador.textContent = `${lista.length} resultado(s)`;
}

function manejarBuscar() {
  const periodo = elementos.filtroPeriodo.value;
  const curso = elementos.filtroCurso.value;
  const docente = elementos.filtroDocente.value;
  const seccion = elementos.filtroSeccion.value;

  const filtrados = registros.filter((registro) => {
    return (
      (periodo === "" || registro.periodo === periodo) &&
      (curso === "" || registro.curso === curso) &&
      (docente === "" || registro.docente === docente) &&
      (seccion === "" || registro.seccion === seccion)
    );
  });
  renderizarTabla(filtrados);
}

function manejarLimpiar() {
  elementos.filtroPeriodo.value = "";
  elementos.filtroCurso.value = "";
  elementos.filtroDocente.value = "";
  elementos.filtroSeccion.value = "";
  renderizarTabla(registros);
}

elementos.btnBuscar.addEventListener("click", manejarBuscar);
elementos.btnLimpiar.addEventListener("click", manejarLimpiar);

document.addEventListener("DOMContentLoaded", cargarDatos);
