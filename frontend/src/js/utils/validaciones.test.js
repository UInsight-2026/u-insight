// Pruebas de las validaciones de los formularios de Intervenciones
// (src/js/utils/validaciones.js).
// Correr con: npm test (desde frontend/)

import { describe, it, expect } from "vitest";
import { validarNuevaIntervencion, validarNuevoSeguimiento } from "./validaciones.js";

describe("validarNuevaIntervencion", () => {
  const payloadValido = () => ({
    alertId: "5",
    type: "TUTORING",
    responsible: "Jafet",
    startDate: "2026-10-09",
    description: "Reunión de seguimiento académico.",
  });

  it("no devuelve errores cuando todos los campos son válidos", () => {
    expect(validarNuevaIntervencion(payloadValido())).toEqual([]);
  });

  it("exige la alerta asociada", () => {
    const errores = validarNuevaIntervencion({ ...payloadValido(), alertId: "" });
    expect(errores).toContain("Debes seleccionar la alerta asociada.");
  });

  it("exige el tipo de intervención", () => {
    const errores = validarNuevaIntervencion({ ...payloadValido(), type: "" });
    expect(errores).toContain("Debes seleccionar un tipo de intervención.");
  });

  it("exige el responsable y no acepta solo espacios", () => {
    const errores = validarNuevaIntervencion({ ...payloadValido(), responsible: "   " });
    expect(errores).toContain("El responsable es obligatorio.");
  });

  it("rechaza un responsable con más de 150 caracteres", () => {
    const errores = validarNuevaIntervencion({ ...payloadValido(), responsible: "a".repeat(151) });
    expect(errores).toContain("El responsable no puede superar 150 caracteres.");
  });

  it("exige la fecha de inicio", () => {
    const errores = validarNuevaIntervencion({ ...payloadValido(), startDate: "" });
    expect(errores).toContain("La fecha de inicio es obligatoria.");
  });

  it("rechaza una descripción con más de 500 caracteres", () => {
    const errores = validarNuevaIntervencion({ ...payloadValido(), description: "a".repeat(501) });
    expect(errores).toContain("La descripción no puede superar 500 caracteres.");
  });

  it("acumula varios errores a la vez cuando faltan varios campos", () => {
    const errores = validarNuevaIntervencion({
      alertId: "",
      type: "",
      responsible: "",
      startDate: "",
      description: "",
    });
    expect(errores.length).toBe(5);
  });
});

describe("validarNuevoSeguimiento", () => {
  const intervencionAbierta = {
    id: 1,
    status: "PLANNED",
    startDate: "2026-10-01",
  };

  const payloadValido = () => ({
    interventionId: "1",
    followUpDate: "2026-10-09",
    observation: "Avance positivo.",
  });

  it("no devuelve errores cuando todos los campos son válidos", () => {
    expect(validarNuevoSeguimiento(payloadValido(), intervencionAbierta)).toEqual([]);
  });

  it("exige seleccionar una intervención", () => {
    const errores = validarNuevoSeguimiento({ ...payloadValido(), interventionId: "" }, undefined);
    expect(errores).toContain("Debes seleccionar la intervención a la que pertenece el seguimiento.");
  });

  it("rechaza el seguimiento si la intervención ya no existe", () => {
    const errores = validarNuevoSeguimiento(payloadValido(), undefined);
    expect(errores).toContain("La intervención seleccionada ya no existe.");
  });

  it("no permite agregar seguimientos a una intervención completada o cancelada", () => {
    const errores = validarNuevoSeguimiento(payloadValido(), { ...intervencionAbierta, status: "COMPLETED" });
    expect(errores).toContain("No se pueden agregar seguimientos a una intervención completada o cancelada.");
  });

  it("exige la fecha de seguimiento", () => {
    const errores = validarNuevoSeguimiento({ ...payloadValido(), followUpDate: "" }, intervencionAbierta);
    expect(errores).toContain("La fecha de seguimiento es obligatoria.");
  });

  it("rechaza una fecha de seguimiento anterior a la fecha de inicio de la intervención", () => {
    const errores = validarNuevoSeguimiento(
      { ...payloadValido(), followUpDate: "2026-09-01" },
      intervencionAbierta
    );
    expect(errores).toContain(
      "La fecha de seguimiento no puede ser anterior a la fecha de inicio de la intervención."
    );
  });

  it("exige la observación y no acepta solo espacios", () => {
    const errores = validarNuevoSeguimiento({ ...payloadValido(), observation: "   " }, intervencionAbierta);
    expect(errores).toContain("La observación es obligatoria.");
  });
});
