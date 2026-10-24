// Pruebas del catálogo de secciones de src/js/api/secciones.js.
// Correr con: npm test (desde frontend/)

import { describe, it, expect } from "vitest";
import { armarCatalogoSecciones, mensajeErrorHttp } from "./secciones.js";

describe("armarCatalogoSecciones", () => {
  const cursos = [
    { id: 1, code: "PRG2", name: "Programación II" },
    { id: 2, code: "CAL2", name: "Cálculo II" },
  ];

  it("usa los ids reales de las secciones y el nombre del curso", () => {
    const secciones = [
      { id: 3, courseId: 1, sectionCode: "B" },
      { id: 1, courseId: 2, sectionCode: "A" },
    ];

    expect(armarCatalogoSecciones(secciones, cursos)).toEqual([
      { sectionId: 1, courseName: "Cálculo II", sectionCode: "A" },
      { sectionId: 3, courseName: "Programación II", sectionCode: "B" },
    ]);
  });

  it("ordena por sección dentro del mismo curso", () => {
    const secciones = [
      { id: 2, courseId: 1, sectionCode: "B" },
      { id: 1, courseId: 1, sectionCode: "A" },
    ];

    expect(armarCatalogoSecciones(secciones, cursos).map((s) => s.sectionId)).toEqual([1, 2]);
  });

  it("muestra 'Curso #id' si el curso no viene en la lista", () => {
    const resultado = armarCatalogoSecciones([{ id: 5, courseId: 99, sectionCode: "A" }], cursos);
    expect(resultado[0].courseName).toBe("Curso #99");
  });

  it("devuelve lista vacía si no hay secciones", () => {
    expect(armarCatalogoSecciones([], cursos)).toEqual([]);
    expect(armarCatalogoSecciones(null, null)).toEqual([]);
  });
});

describe("mensajeErrorHttp", () => {
  it("indica que no hay datos para esta sección en un 404", () => {
    expect(mensajeErrorHttp(404, "evolución")).toBe("No hay datos de evolución para esta sección.");
  });

  it("muestra el código HTTP real en errores del servidor", () => {
    expect(mensajeErrorHttp(500, "evolución")).toContain("HTTP 500");
  });

  it("muestra el código HTTP real en otros errores", () => {
    expect(mensajeErrorHttp(400, "evolución")).toContain("HTTP 400");
  });

  it("indica problema de conexión cuando no hubo respuesta", () => {
    expect(mensajeErrorHttp(undefined, "evolución")).toContain("No se pudo conectar");
  });
});
