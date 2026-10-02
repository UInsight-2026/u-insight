// Lógica de la vista Evolución (pendiente: Semana 3)

async function cargarEvolucion() {
  try {
    console.info("Cargando evolución...");

    const respuesta = await fetch("src/data/evolucion.mock.json");
    const datos = await respuesta.json();
    console.info("Datos de evolucion obtenidos", datos);

    const contenedor = document.getElementById("evolucion-container");

    datos.forEach((evolucion) => {
      console.log("Se cargarán los siguientes datos:", evolucion);
      let grafico;

    if (!evolucion.points || evolucion.points.length < 2) {
        console.info("validando cantidad de puntos para graficar:");

        grafico = `
            <div class="mensaje-insuficiente">
                No hay suficientes datos para mostrar la evolución.
            </div>
        `;

    } else {

        grafico = crearContenedorGrafico(
            evolucion.courseName,
            evolucion.points
        );

    }

      contenedor.innerHTML += `
       <section class="evolucion-card">

         <div class="evolucion-info">
            <span class="clasificacion ${evolucion.classification.toLowerCase()}">
                ${evolucion.classification}
            </span>

            <span class="promedio">
                Cambio promedio:
                ${evolucion.averageChange ?? "N/D"}
            </span>
         </div>
      </div>

      ${grafico}

    </section>`;
    console.info("Sección agregada al HTML");
    });

    console.info(datos);
  } catch (error) {
    console.error("Error al cargar evolución:", error);
  }
  console.info("Vista de evolución cargada correctamente.");
}
cargarEvolucion();
