const elementos = {
  contador: document.getElementById("contador-resultados"),
  contenedorResultados: document.getElementById("contenedor-resultados"),
  filtroSeveridad: document.getElementById("filtro-severidad"),
  filtroEstado: document.getElementById("filtro-estado"),
  btnBuscar: document.getElementById("btn-buscar"),
  btnLimpiar: document.getElementById("btn-limpiar"),
};

let registros = [];

async function cargarDatosMock() {
  try {
    const respuesta = await fetch("src/data/alertas.mock.json");
    if (!respuesta.ok) {
      throw new Error(`No se pudo cargar el archivo mock (HTTP ${respuesta.status})`);
    }
    registros = await respuesta.json();
    renderizarAlertas(registros);
    console.log("Datos simulados cargados:", registros);
  } catch (error) {
    console.error("Error al cargar datos simulados:", error);
    elementos.contenedorResultados.innerHTML = `
      <div class="mensaje-estado">
        No se pudieron cargar los datos simulados. Verifica que el archivo
        src/data/alertas.mock.json exista y que la página se esté sirviendo
        desde un servidor local (no abierta directamente como archivo).
      </div>`;
    elementos.contador.textContent = "Error al cargar datos.";
  }
}

function renderizarAlertas(lista) {
    if (!lista.length) {
        elementos.contenedorResultados.innerHTML = `
            <div class="mensaje-estado">
                No hay alertas que coincidan con los criterios de búsqueda.
            </div>`;
        elementos.contador.textContent = "0 resultados";
        return;
    }

    elementos.contenedorResultados.innerHTML = lista
    .map((alerta) => {
        return crearTarjetaAlerta(alerta);
    })
    .join("");
    elementos.contador.textContent = `${lista.length} resultado(s)`;
}

function filtrarAlertas() {
    const severidadSeleccionada = elementos.filtroSeveridad.value;
    const estadoSeleccionado = elementos.filtroEstado.value;
    const alertasFiltradas = registros.filter((alerta) => {
        return (
            (severidadSeleccionada === "" || alerta.severity === severidadSeleccionada) &&
            (estadoSeleccionado === "" || alerta.status === estadoSeleccionado)
        );
    });
    renderizarAlertas(alertasFiltradas);
}

function limpiarFiltros() {
    elementos.filtroSeveridad.value = "";
    elementos.filtroEstado.value = "";
    renderizarAlertas(registros);
}

elementos.btnBuscar.addEventListener("click", filtrarAlertas);
elementos.btnLimpiar.addEventListener("click", limpiarFiltros);
document.addEventListener("DOMContentLoaded", cargarDatosMock);
