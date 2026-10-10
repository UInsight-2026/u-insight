// Pruebas de las funciones de formato de src/js/utils/formatters.js.
// Correr con: npm test (desde frontend/)

import { describe, it, expect } from "vitest";
import {
  valorODisponible,
  formatearModa,
  formatearDispersion,
  formatearTendencia,
} from "./formatters.js";

describe("valorODisponible", () => {
  it("devuelve 'No disponible' cuando el valor es null", () => {
    expect(valorODisponible(null)).toBe("No disponible");
  });

  it("devuelve 'No disponible' cuando el valor es undefined", () => {
    expect(valorODisponible(undefined)).toBe("No disponible");
  });

  it("devuelve el valor tal cual cuando es un número, incluido 0", () => {
    expect(valorODisponible(72.5)).toBe(72.5);
    expect(valorODisponible(0)).toBe(0);
  });
});

describe("formatearModa", () => {
  it("devuelve 'No disponible' cuando la moda es un arreglo vacío", () => {
    expect(formatearModa([])).toBe("No disponible");
  });

  it("devuelve 'No disponible' cuando la moda es null", () => {
    expect(formatearModa(null)).toBe("No disponible");
  });

  it("une un solo valor de moda", () => {
    expect(formatearModa([73])).toBe("73");
  });

  it("une varios valores cuando hay más de una moda", () => {
    expect(formatearModa([70, 75])).toBe("70, 75");
  });
});

describe("formatearDispersion", () => {
  it("devuelve 'No disponible' cuando dispersion es null", () => {
    expect(formatearDispersion(null)).toBe("No disponible");
  });

  it("muestra la desviación estándar y traduce la clasificación", () => {
    const resultado = formatearDispersion({
      standardDeviation: 11.16,
      classification: "MODERATE_DISPERSION",
    });
    expect(resultado).toBe("11.16 (Dispersión moderada)");
  });

  it("si la clasificación no está en el catálogo, la muestra tal cual (no la esconde)", () => {
    const resultado = formatearDispersion({
      standardDeviation: 5,
      classification: "ALGO_NUEVO",
    });
    expect(resultado).toBe("5 (ALGO_NUEVO)");
  });
});

describe("formatearTendencia", () => {
  it("devuelve 'No disponible' cuando tendencia es null", () => {
    expect(formatearTendencia(null)).toBe("No disponible");
  });

  it("muestra la clasificación con el cambio promedio cuando viene", () => {
    const resultado = formatearTendencia({
      classification: "NEGATIVE",
      averageChange: -7.5,
    });
    expect(resultado).toBe("Descendente (-7.5 pts promedio)");
  });

  it("con INSUFFICIENT_DATA (averageChange null) muestra solo la clasificación, sin pts", () => {
    const resultado = formatearTendencia({
      classification: "INSUFFICIENT_DATA",
      averageChange: null,
    });
    expect(resultado).toBe("Datos insuficientes");
  });
});
