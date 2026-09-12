const RUTA_MOCK = "src/data/exploracion.mock.json";

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

async function cargarDatosMock() {
  try {
    const respuesta = await fetch(RUTA_MOCK);
    if (!respuesta.ok) {
      throw new Error(`No se pudo cargar el archivo mock (HTTP ${respuesta.status})`);
    }
    registros = await respuesta.json();
    poblarFiltros(registros);
    renderizarTabla(registros);
  } catch (error) {
    console.error("Error al cargar datos simulados:", error);
    elementos.cuerpoTabla.innerHTML = `
      <tr>
        <td colspan="8" class="mensaje-estado">
          No se pudieron cargar los datos simulados. Verifica que el archivo
          src/data/exploracion.mock.json exista y que la página se esté sirviendo
          desde un servidor local (no abierta directamente como archivo).
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
      <tr><td colspan="8" class="mensaje-estado">No hay registros para mostrar.</td></tr>`;
    elementos.contador.textContent = "0 resultados";
    return;
  }

  elementos.cuerpoTabla.innerHTML = lista
    .map((registro) => {
      const claseEstado =
        registro.estado === "Aprobado" ? "estado-aprobado" : "estado-reprobado";

      return `
        <tr>
          <td>${registro.periodo}</td>
          <td>${registro.curso}</td>
          <td>${registro.docente}</td>
          <td>${registro.seccion}</td>
          <td>${registro.estudiante}</td>
          <td>${registro.carnet}</td>
          <td>${registro.nota}</td>
          <td><span class="estado ${claseEstado}">${registro.estado}</span></td>
        </tr>`;
    })
    .join("");

  elementos.contador.textContent = `${lista.length} resultado(s) simulado(s)`;
}

function manejarBuscar() {
  console.info(
    "Filtro aún no implementado (Semana 3). Mostrando todos los datos simulados."
  );
  renderizarTabla(registros);
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

document.addEventListener("DOMContentLoaded", cargarDatosMock);
