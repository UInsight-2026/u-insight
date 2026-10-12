# Módulo A4 — Secciones e Inscripciones

## Descripción
Módulo responsable de construir la relación académica principal en U-Insight:
**Período + Curso + Docente → Sección → Estudiantes Inscritos**.

## Endpoints Principales
- `POST /api/v1/sections` - Crear sección académica
- `GET /api/v1/sections` - Listar secciones
- `GET /api/v1/sections/{id}` - Detalle de sección
- `POST /api/v1/sections/{id}/enrollments` - Inscribir estudiante en sección
- `GET /api/v1/sections/{id}/enrollments` - Consultar estudiantes inscritos

## Reglas de Negocio
1. Curso, Período y Docente deben existir antes de crear una sección.
2. No se permite duplicar inscripciones de un mismo estudiante en la misma sección.
3. Una sección en estado CERRADO no permite nuevas inscripciones.
