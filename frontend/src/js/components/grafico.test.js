// Pruebas de src/js/components/grafico.js (crearContenedorGrafico).
// Correr con: npm test (desde frontend/)

import { describe, it, expect } from "vitest";
import { crearContenedorGrafico } from "./grafico.js";

describe("crearContenedorGrafico", () => {
  it("muestra el mensaje de 'sin datos' cuando puntos es un arreglo vacío", () => {
    const html = crearContenedorGrafico("Promedios", []);
    expect(html).toContain("No hay suficientes datos para graficar.");
    expect(html).toContain("<h3>Promedios</h3>");
  });

  it("muestra el mensaje de 'sin datos' cuando puntos es null", () => {
    const html = crearContenedorGrafico("Promedios", null);
    expect(html).toContain("No hay suficientes datos para graficar.");
  });

  it("dibuja una sola barra al 100% cuando hay un único punto", () => {
    const html = crearContenedorGrafico("Promedios", [{ label: "Parcial 1", value: 80 }]);
    expect(html).toContain("height: 100%");
    expect(html).toContain(">80<");
    expect(html).toContain("Parcial 1");
  });

  it("calcula la altura de cada barra como porcentaje del valor máximo", () => {
    const html = crearContenedorGrafico("Promedios", [
      { label: "Parcial 1", value: 50 },
      { label: "Parcial 2", value: 100 },
    ]);
    expect(html).toContain("height: 50%");
    expect(html).toContain("height: 100%");
  });

  it("muestra 's/d' y una barra al 0% cuando el valor es null o undefined", () => {
    const html = crearContenedorGrafico("Promedios", [
      { label: "Parcial 1", value: null },
      { label: "Parcial 2", value: 90 },
    ]);
    expect(html).toContain("height: 0%");
    expect(html).toContain("s/d");
  });

  it("usa 1 como máximo cuando todos los valores son null/undefined (evita dividir entre 0)", () => {
    const html = crearContenedorGrafico("Promedios", [
      { label: "Parcial 1", value: null },
      { label: "Parcial 2", value: undefined },
    ]);
    expect(html).toContain("height: 0%");
    expect(html).not.toContain("NaN");
  });
});
