# Guía de los cambios

## Causa del problema

La base SQLite original tenía 9 asientos y A3 estaba ocupado. Sin embargo, la clase Sala generaba otros 70 objetos en memoria, todos disponibles. Los controladores del mapa, de la recomendación y de la simulación consultaban esa lista; no utilizaban AsientoRepository.

Además, recomendacion.html contenía dos documentos mezclados, identificadores duplicados, un return fuera de la función correcta y código que intentaba utilizar una variable inexistente. El flujo de compra devolvía éxito sin actualizar ni validar disponibilidad y el selector de compra contenía asientos fijos.

## Solución por responsabilidad de POO

| Componente | Responsabilidad |
| --- | --- |
| Asiento | Entidad persistida con identificación, disponibilidad y datos de visión. |
| AsientoRepository | Lectura de SQLite y actualización condicional para ocupar un asiento libre. |
| Sala | Consultar la misma sala persistida y confirmar grupos dentro de una transacción. |
| PreferenciasUsuario | Validar cantidad de personas y presupuesto total opcional. |
| RecomendadorAsientos | Filtrar ocupados, buscar grupos consecutivos de una fila y comparar puntajes. |
| CarteleraService | Compartir títulos, precios y horarios entre cartelera, recomendaciones y compra. |
| RecomendacionController | Recibir las preferencias y devolver asientos y costo del grupo. |
| BoletoController | Validar película/horario, confirmar los asientos y responder con el comprobante simulado. |
| mapa-asientos.js | Mostrar ocupados, seleccionar o quitar varios asientos y continuar a boletos. |
| recomendacion.js | Consultar las recomendaciones y trasladar la elección al mapa. |
| compra.js | Cargar disponibilidad actual, confirmar y mostrar conflictos o el resultado. |

## Recomendación

1. Rechaza cantidades fuera del intervalo 1–10.
2. Comprueba el presupuesto contra cantidad × precio por boleto. Un presupuesto omitido no limita la búsqueda.
3. Obtiene el estado actual de SQLite.
4. Descarta asientos ocupados y ordena por fila/número.
5. Revisa bloques del tamaño del grupo. Todos deben estar en la misma fila y tener números consecutivos.
6. Compara el puntaje: conveniencia × 100, menos ángulo absoluto × 2, menos la diferencia respecto a 6.5 m.
7. Devuelve el mejor bloque completo o una lista vacía con un mensaje.

Se corrigió el criterio angular: la sala y la simulación definen 0° como vista frontal, por lo que la recomendación ya no usa 45° como objetivo. El puntaje es una regla de demostración del proyecto; no es una medición certificada de comodidad.

## Persistencia y confirmación

La operación para ocupar un asiento usa la condición `id = solicitado AND disponible = true` en la misma actualización. Se procesa la selección en una transacción: si cualquiera deja de estar disponible, se lanza una excepción y se revierte el conjunto.

Los asientos seleccionados en pantalla no se guardan como ocupados antes de confirmar. El estado temporal se conserva en sessionStorage, por pestaña, para pasar del mapa a los boletos.

La semilla cubre 70 asientos y conserva las reservas existentes al reiniciar. Se actualizó la base de demostración incluida para que su geometría y calificaciones coincidan con esa semilla.

## Rutas útiles

| Método y ruta | Uso |
| --- | --- |
| GET /api/asientos | Obtener los 70 asientos y su disponibilidad. |
| GET /api/asientos/A3/caracteristicas | Consultar un asiento, también si está ocupado. |
| GET /api/recomendaciones/mejores?cantidad=2 | Buscar un grupo de 2 con el precio predeterminado de Parasite. |
| GET /api/recomendaciones/mejores?cantidad=2&peliculaId=ElPianista&presupuesto=14 | Buscar con película y presupuesto total. |
| POST /api/boletos/compra | Confirmar asientos y guardar su ocupación. |
| GET /api/vista-asiento?fila=A&numero=3 | Rechazar con 409 un asiento ocupado en la simulación. |

Ejemplo de confirmación:

```json
{
  "peliculaId": "parasite",
  "horario": "15:30",
  "asientos": ["A1", "A2"],
  "nombreCliente": "Rodrigo"
}
```

## Integración con el trabajo del equipo

Se mantuvieron los paquetes com.example.demo, la distribución de 70 asientos, la cartelera, el inicio de sesión y el servicio de simulación. Los dos últimos siguen utilizando sus modelos y controladores. Se corrigieron sus enlaces de navegación hacia el mapa.

En esta copia no se implementó inventario por función. Si se amplía el sistema a varias funciones reales, la ocupación deberá asociarse a función + asiento.

La lista completa de archivos añadidos o modificados está en [ARCHIVOS_CAMBIADOS.md](ARCHIVOS_CAMBIADOS.md).
