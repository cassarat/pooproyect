# Cine Nova · Proyecto de POO

Equipo: Nathalia Cáceres, Rodrigo Rivas y Joseline Mazariegos.

Aplicación en Java 17, Spring Boot y SQLite. Muestra una sala de 70 asientos (A–G, 1–10), recomienda grupos consecutivos y permite confirmar una selección mediante una compra simulada.

## Ejecutar en Windows

Instala un JDK 17 o superior y abre PowerShell en la carpeta que contiene este README:

```powershell
cd spring
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

En CMD usa `mvnw.cmd` sin el prefijo `.\`. La primera ejecución necesita Internet para descargar Maven y las dependencias. También puedes importar `spring/pom.xml` en tu IDE.

Abre [http://localhost:8080](http://localhost:8080). Las páginas se abren desde el servidor, no con doble clic sobre los HTML.

## Ejecutar en Linux o macOS

```bash
cd spring
bash mvnw test
bash mvnw spring-boot:run
```

## Probar el cambio solicitado

1. Abre **Mapa de Asientos**. Debe mostrar 70 lugares: 57 disponibles y 13 ocupados en rojo, con una × y deshabilitados.
2. Abre **Recomendador Inteligente**, elige 2 personas y pulsa **Buscar Asientos**. Se recomiendan lugares consecutivos de una misma fila, siempre libres.
3. Introduce un presupuesto total de 14.99 USD para Parasite: no alcanza para 2 personas. Con 15.00 USD sí alcanza.
4. Usa **Ver y seleccionar estos asientos en el mapa**. La selección se resalta y se puede modificar.
5. Pulsa **Continuar a boletos** y confirma la compra simulada. Al regresar al mapa, esos asientos están ocupados y dejan de aparecer en las recomendaciones.
6. Detén y vuelve a iniciar el servidor desde `spring`: la ocupación debe conservarse.

Ocupados iniciales: **A3, B4, B5, C5, C6, D4, D5, E7, E8, F2, F3, G9 y G10**.

## Cómo funciona

Mapa, recomendador, simulación y compra consultan la misma tabla `asiento` en `spring/cine.db`. La selección temporal del visitante permanece en su pestaña; se guarda la ocupación al confirmar. Otro visitante puede ocupar un asiento antes de que confirmes: el servidor vuelve a verificarlo y devuelve un conflicto sin confirmar parcialmente el grupo.

La aplicación representa **una sala de demostración compartida**. La disponibilidad no está separada por película ni por horario. Los horarios y películas conservan la interfaz del proyecto y determinan la tarifa; no representan inventarios independientes.

El presupuesto es opcional y corresponde al total del grupo en USD, como la cartelera original. Parasite cuesta 7.50 USD por boleto y El pianista 7.00 USD. No se implementan pagos reales. La vista del asiento sigue siendo una simulación orientativa.

## Código y documentación

- [Guía de los cambios](docs/GUIA_CAMBIOS.md).
- [Material propuesto para la sección de Rodrigo de la entrega 5](docs/ENTREGA5_RODRIGO.md).
- [Qué se verificó y qué falta ejecutar](docs/VERIFICACION.md).

El texto original de `Model.java` se conserva como `docs/referencia-seleccion-original.md`: contenía varias clases y Markdown y era material de referencia, fuera del proyecto Maven.

Cuenta de demostración existente: usuario `joseline`, contraseña `Cine2026!`.

## Datos de demostración

Este ZIP incluye la base preparada con los 70 asientos. Trabaja desde una carpeta nueva para conservar tu copia anterior. La semilla de `data.sql` usa `INSERT OR IGNORE`, por lo que reiniciar no libera asientos ya ocupados.

Para volver a los datos iniciales, detén el servidor, guarda una copia de tu `spring/cine.db` si la necesitas y vuelve a extraer la base incluida en el ZIP. Las pruebas de integración usan una base temporal independiente.
