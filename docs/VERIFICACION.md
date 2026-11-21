# Verificación

## Comprobaciones realizadas en el entorno de preparación

| Comprobación | Resultado y alcance |
| --- | --- |
| Algoritmo Java real de RecomendadorAsientos | Se ejecutaron 10,240 combinaciones: los 1,024 patrones posibles de ocupación de una fila de 10 asientos, con tamaños de grupo de 1 a 10. Se comprobó que no aparezcan ocupados, que no se salten huecos y que se devuelva un grupo completo exactamente cuando existe. |
| Criterios adicionales de Java | Se comprobó la preferencia por ángulo frontal, la separación de filas, la tarifa total y el rechazo de cantidades/presupuestos inválidos. |
| SQLite real, en copias de prueba | Se verificaron los 70 asientos y 13 ocupados iniciales, la conservación del estado tras reabrir, la semilla idempotente, la reversión de una actualización fallida y dos actualizaciones concurrentes del mismo asiento. |
| JavaScript | node --check aceptó los 5 archivos JS y el script de la página principal. |
| HTML y recursos | Sin identificadores duplicados; un head/body por página; referencias a los recursos locales y a los elementos DOM de las tres vistas modificadas comprobadas. |
| Sintaxis de las fuentes Java | Se comprobaron las 17 fuentes de producción con nivel de lenguaje 17 y declaraciones auxiliares para las APIs externas. Esto no comprueba las implementaciones reales de Spring, Hibernate o JPA. |

## Verificación pendiente en la computadora del equipo

No fue posible ejecutar la suite Maven: el entorno no permitió descargar Maven desde repo.maven.apache.org. Por ello no se afirma que la compilación completa de Spring Boot ni las 26 pruebas JUnit hayan pasado.

Tampoco fue posible abrir Chromium por una restricción del entorno. La revisión de interfaz realizada fue estática; la prueba visual y el recorrido completo en el navegador quedan pendientes.

Desde la carpeta spring, ejecuta:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Después recorre los pasos de prueba manual del README en [http://localhost:8080](http://localhost:8080).

## Pruebas JUnit incluidas

La suite contiene 26 métodos @Test entre los casos existentes y los añadidos. Las nuevas pruebas de integración cubren:

- Mapa, recomendaciones y simulación leyendo la misma disponibilidad.
- Recomendaciones de una misma fila y sin asientos ocupados.
- Persistencia de una confirmación y conservación al ejecutar otra vez la semilla.
- Conflicto al volver a ocupar el mismo asiento.
- Reversión completa si un miembro del grupo ya está ocupado.
- Datos inválidos, duplicados y película/horario inexistentes.
- Presupuesto insuficiente, exacto y tarifa de otra película.
- Dos confirmaciones concurrentes sobre el mismo lugar.
- Sala completamente ocupada.

AsientosIntegracionTest crea un archivo SQLite temporal. DemoApplicationTests usa una base en memoria. Ninguna de estas pruebas necesita modificar la base de demostración entregada.
