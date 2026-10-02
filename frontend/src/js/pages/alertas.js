const API_BASE = "http://localhost:8080/api/v1";

const elementos = {
  contador: document.getElementById("contador-resultados"),
  contenedorResultados: document.getElementById("contenedor-resultados"),
  filtroSeveridad: document.getElementById("filtro-severidad"),
  filtroEstado: document.getElementById("filtro-estado"),
  btnBuscar: document.getElementById("btn-buscar"),
  btnLimpiar: document.getElementById("btn-limpiar"),
};

let registros = [];

async function cargarAlertas() {
  try {
    console.info("ALERTAS_CARGA_INICIADA");
    // size=100: el endpoint pagina (default 20), y para esta vista simple
    // todavia no implementamos controles de pagina en el UI.
    const respuesta = await fetch(`${API_BASE}/reports/alerts?size=100`);
    if (!respuesta.ok) {
      throw new Error(`La API respondió un error (HTTP ${respuesta.status})`);
    }
    const datos = await respuesta.json();
    registros = datos.alerts || [];
    renderizarAlertas(registros);
    console.info("ALERTAS_CARGA_EXITOSA", registros.length, "alertas");
  } catch (error) {
    console.error("ALERTAS_CARGA_ERROR", error);
    elementos.contenedorResultados.innerHTML = `
      <div class="mensaje-estado">
        No se pudieron cargar las alertas. Verifica que el backend esté
        corriendo en ${API_BASE} y que no esté bloqueando la petición por CORS
        (revisa la consola del navegador).
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
            (severidadSeleccionada === "" || alerta.riskLevel === severidadSeleccionada) &&
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
document.addEventListener("DOMContentLoaded", cargarAlertas);
