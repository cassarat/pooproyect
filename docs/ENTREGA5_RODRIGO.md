# Entrega 5 · Sección de Rodrigo

La sección de Rodrigo del PDF está vacía y no especifica una tarea concreta. Esta propuesta completa disponibilidad, presupuesto, selección y pruebas, tomando como referencia las responsabilidades previas y el cambio solicitado. Adapta el texto a lo que decidan presentar como equipo.

## Requisitos funcionales cubiertos por el código

- **Disponibilidad compartida — prioridad alta.** El mapa, el recomendador y la simulación consultan los asientos guardados en SQLite.
- **Selección única y múltiple — prioridad alta.** Los asientos ocupados se muestran deshabilitados y los disponibles pueden agregarse o quitarse de la selección.
- **Confirmación de asientos — prioridad alta.** Se verifica nuevamente la disponibilidad y se persiste la ocupación del grupo en una transacción.
- **Recomendación según ocupación — prioridad alta.** Se excluyen los lugares ocupados y se buscan grupos consecutivos dentro de una fila.
- **Presupuesto — prioridad media.** Se compara el presupuesto total con la tarifa de la película y la cantidad de personas.
- **Pruebas funcionales — prioridad alta.** Se agregaron casos para grupos, ocupados, presupuesto, persistencia, conflictos y reversión de la selección.

## Planificación propuesta

Estas son estimaciones para documentar o revisar el trabajo; no son horas efectivamente trabajadas ni fechas retroactivas.

| Tarea | Descripción | Estimación | Responsable |
| --- | --- | --- | --- |
| Revisar la integración | Identificar las diferencias entre los asientos en memoria y SQLite. | 1 h | Rodrigo |
| Integrar disponibilidad | Conectar Sala al repositorio y utilizar una semilla de 70 asientos. | 1.5 h | Rodrigo |
| Completar selección y confirmación | Validar ocupación y guardar el grupo de manera transaccional. | 2 h | Rodrigo |
| Integrar presupuesto | Usar cantidad y tarifa de la película para comprobar el límite total. | 1 h | Rodrigo |
| Corregir la interfaz | Conectar mapa, recomendación y compra con los estados reales. | 1.5 h | Rodrigo |
| Ejecutar pruebas y documentar | Ejecutar Maven y la prueba manual del README en la computadora del equipo. | 1.5 h | Rodrigo |

Añade las fechas de trabajo y de terminación que correspondan realmente.

## Puntos técnicos para explicar

- Antes existían dos fuentes de disponibilidad: SQLite y una lista en memoria. La lista nueva deja de generarse en producción y se consulta el repositorio.
- Filtrar un asiento ocupado no basta si el lugar cambia de estado antes de confirmar. Por eso se comprueba y actualiza con una condición en la base de datos.
- Un grupo no puede saltarse el asiento ocupado que separa dos lugares libres: se verifica que sus números sean consecutivos.
- El presupuesto representa el total del grupo. Para 2 personas en Parasite, el costo es 15.00 USD.
- INSERT OR IGNORE permite completar los datos iniciales sin liberar las reservas que ya existen.

## Reflexión y registro de tiempo

Completa esta parte con tu experiencia después de ejecutar el proyecto. Puedes comentar cómo identificaste las dos fuentes de datos, qué aprendiste de las transacciones y qué dificultades encontraste al integrar las vistas.

No se incluyen horas trabajadas, comentarios de usuarios, videos ni resultados de Maven inventados. Consulta VERIFICACION.md para distinguir las comprobaciones realizadas de las pendientes.
