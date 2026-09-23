function crearTarjeta(titulo, descripcion) {
    return `
        <div class="card">
            <h3>${titulo}</h3>
            <p>${descripcion}</p>
        </div>
    `;
}

async function cargarResumen() {
    
    const respuesta = await fetch("src/data/inicio.mock.json");
    const datos = await respuesta.json();

    const contenedor = document.getElementById("resumen-container");
    contenedor.innerHTML = `
    <div class="card-container">
     ${crearTarjeta("Periodo", datos.periodo)}
     ${crearTarjeta("Alertas", datos.alertasActivas)}
     ${crearTarjeta("En riesgo", datos.seccionesEnRiesgo)}
     ${crearTarjeta("Tendencia", datos.tendenciaGeneral)}
    </div>
    `;

}
cargarResumen();