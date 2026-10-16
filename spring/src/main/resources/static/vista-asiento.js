'use strict';
const elemento = id => document.getElementById(id);
let consultaActual = 0;
const formato = new Intl.NumberFormat('es-GT', { maximumFractionDigits: 1 });
function limpiarVista() {
    elemento('pantalla').hidden = true;
    elemento('detalles').hidden = true;
    elemento('inicial').hidden = false;
    elemento('escenario').setAttribute('aria-label', 'Selecciona un asiento para ver la simulación');
    document.querySelectorAll('[data-asiento]').forEach(boton => boton.setAttribute('aria-pressed', 'false'));
}
async function mostrarVista(asiento, boton) {
    const consulta = ++consultaActual;
    limpiarVista();
    elemento('estado').textContent = `Cargando la vista de ${asiento.id}…`;
    try {
        const parametros = new URLSearchParams({ fila: asiento.fila, numero: asiento.numero });
        const respuesta = await fetch(`/api/vista-asiento?${parametros}`);
        if (!respuesta.ok) {
            const mensajes = { 404: 'El asiento ya no existe.', 409: 'Este asiento está ocupado. Elige otro.', 422: 'Los datos de este asiento no permiten mostrar la vista.' };
            throw new Error(mensajes[respuesta.status] || 'No se pudo consultar la vista. Intenta nuevamente.');
        }
        const vista = await respuesta.json();
        if (consulta !== consultaActual) return;
        const pantalla = elemento('pantalla');
        pantalla.style.left = `${20 + vista.desplazamientoPorcentaje}%`;
        pantalla.style.transform = `scale(${vista.escala}) rotateY(${vista.rotacionGrados}deg)`;
        pantalla.hidden = false;
        elemento('inicial').hidden = true;
        elemento('detalles').hidden = false;
        elemento('asiento').textContent = vista.id;
        elemento('distancia').textContent = `${formato.format(vista.distanciaPantalla)} m`;
        elemento('angulo').textContent = `${formato.format(vista.anguloVision)}°`;
        elemento('descripcion').textContent = vista.descripcion;
        elemento('estado').textContent = `Vista desde el asiento ${vista.id}`;
        elemento('escenario').setAttribute('aria-label', `${vista.descripcion} desde ${vista.id}, a ${formato.format(vista.distanciaPantalla)} metros y ${formato.format(vista.anguloVision)} grados.`);
        boton.setAttribute('aria-pressed', 'true');
    } catch (error) {
        if (consulta !== consultaActual) return;
        limpiarVista();
        elemento('estado').textContent = error instanceof TypeError ? 'No hay conexión con el servidor. Vuelve a elegir el asiento para reintentar.' : error.message;
    }
}
async function cargarAsientos() {
    ++consultaActual;
    limpiarVista();
    elemento('asientos').replaceChildren();
    elemento('reintentar').hidden = true;
    elemento('estado').textContent = 'Cargando asientos…';
    try {
        const respuesta = await fetch('/api/vista-asiento/asientos');
        if (!respuesta.ok) throw new Error('No se pudo cargar la sala.');
        const asientos = await respuesta.json();
        const filas = new Map();
        asientos.forEach(asiento => {
            if (!filas.has(asiento.fila)) {
                const fila = document.createElement('div');
                fila.className = 'fila';
                const etiqueta = document.createElement('span');
                etiqueta.textContent = asiento.fila;
                fila.append(etiqueta);
                filas.set(asiento.fila, fila);
                elemento('asientos').append(fila);
            }
            const boton = document.createElement('button');
            boton.textContent = asiento.disponible ? asiento.id : `${asiento.id} ×`;
            boton.dataset.asiento = asiento.id;
            boton.disabled = !asiento.disponible;
            boton.setAttribute('aria-pressed', 'false');
            boton.setAttribute('aria-label', `Asiento ${asiento.id}, ${asiento.disponible ? 'disponible' : 'ocupado'}`);
            boton.addEventListener('click', () => mostrarVista(asiento, boton));
            filas.get(asiento.fila).append(boton);
        });
        elemento('estado').textContent = asientos.some(a => a.disponible) ? 'Selecciona un asiento disponible para comenzar.' : 'No hay asientos disponibles en esta sala.';
    } catch (error) {
        elemento('estado').textContent = 'No se pudieron cargar los asientos. Comprueba la conexión y vuelve a intentar.';
        elemento('reintentar').hidden = false;
    }
}
elemento('reintentar').addEventListener('click', cargarAsientos);
cargarAsientos();
