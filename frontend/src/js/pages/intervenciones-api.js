/*
\src/js/pages/intervenciones.js
Vista Intervenciones: crear intervención (POST real) e historial (iterando alertas).
Pendientes del backend:
- No hay endpoint para listar TODAS las intervenciones: el historial itera alertas conocidas.
- Seguimientos no existe aún: esa parte sigue en mock.
- PATCH /interventions/{id}/status no está implementado: el estado no se cambia desde el front.
*/

const API_BASE = "/api/v1"; 

const TIPOS_INTERVENCION = {
  TUTORING: "Tutoría",
  ACADEMIC_PLAN: "Plan académico",
  PARENT_MEETING: "Reunión con padres",
};

const ALERTAS_CONOCIDAS = [10];

async function crearIntervencion(alertId, { type, description, responsible, startDate }) {
  const res = await fetch(`${API_BASE}/alerts/${alertId}/interventions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ type, description, responsible, startDate }),
  });
  if (!res.ok) throw new Error(`POST interventions falló (${res.status})`);
  return res.json();
}

async function listarIntervencionesPorAlerta(alertId) {
  const res = await fetch(`${API_BASE}/alerts/${alertId}/interventions`);
  if (!res.ok) throw new Error(`GET interventions falló (${res.status})`);
  return res.json();
}

const form = document.getElementById("form-intervencion");
const msg = document.getElementById("mensaje-estado");
const tbody = document.getElementById("historial-body");

function escapar(texto) {
  const d = document.createElement("div");
  d.textContent = texto ?? "";
  return d.innerHTML;
}

function poblarSelects() {
  form.alertId.innerHTML =
    `<option value="">Selecciona una alerta</option>` +
    ALERTAS_CONOCIDAS.map((id) => `<option value="${id}">Alerta ${id}</option>`).join("");

  form.type.innerHTML =
    `<option value="">Selecciona un tipo</option>` +
    Object.entries(TIPOS_INTERVENCION)
      .map(([valor, etiqueta]) => `<option value="${valor}">${etiqueta}</option>`)
      .join("");
}

async function cargarHistorial() {
  tbody.innerHTML = `<tr><td colspan="6">Cargando...</td></tr>`;
  try {
    const resultados = await Promise.allSettled(
      ALERTAS_CONOCIDAS.map((id) => listarIntervencionesPorAlerta(id))
    );

    const filas = resultados.flatMap((r, i) => {
      if (r.status === "rejected") {
        console.error(`Error cargando alerta ${ALERTAS_CONOCIDAS[i]}:`, r.reason);
        return [];
      }
      return r.value;
    });

    console.info(`Historial cargado: ${filas.length} intervenciones`);

    tbody.innerHTML = filas.length
      ? filas
          .map(
            (x) => `
        <tr>
          <td>${escapar(x.alertId)}</td>
          <td>${escapar(TIPOS_INTERVENCION[x.type] ?? x.type)}</td>
          <td>${escapar(x.description)}</td>
          <td>${escapar(x.responsible)}</td>
          <td>${escapar(x.startDate)}</td>
          <td>${escapar(x.status)}</td>
        </tr>`
          )
          .join("")
      : `<tr><td colspan="6">Sin intervenciones registradas.</td></tr>`;
  } catch (err) {
    console.error("Error al cargar historial:", err);
    tbody.innerHTML = `<tr><td colspan="6">Error al cargar el historial.</td></tr>`;
  }
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const alertId = form.alertId.value;
  const datos = {
    type: form.type.value,
    description: form.description.value.trim(),
    responsible: form.responsible.value.trim(),
    startDate: form.startDate.value,
  };

  if (!alertId || !datos.type || !datos.description || !datos.responsible || !datos.startDate) {
    console.error("Acción rechazada: faltan campos obligatorios");
    msg.textContent = "Completa todos los campos.";
    return;
  }

  msg.textContent = "Guardando...";
  try {
    const creada = await crearIntervencion(alertId, datos);
    console.info("Intervención creada:", creada.id);
    msg.textContent = `Intervención #${creada.id} creada (${creada.status}).`;
    form.reset();
    await cargarHistorial();
  } catch (err) {
    console.error("Error al crear intervención:", err);
    msg.textContent = "No se pudo crear la intervención.";
  }
});

poblarSelects();
cargarHistorial();
