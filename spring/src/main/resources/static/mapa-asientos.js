'use strict';
const seleccionados = new Set();
let mapa = [];
let detalleActual = 0;

function guardarSeleccion() {
    sessionStorage.setItem('cineNovaSeleccion', JSON.stringify([...seleccionados]));
    document.getElementById('seleccion-resumen').textContent = seleccionados.size
        ? 'Tu selección: ' + [...seleccionados].join(', ')
        : 'Elige uno o varios asientos disponibles.';
    document.getElementById('continuar-compra').disabled = seleccionados.size === 0;
    document.querySelectorAll('.seat-button').forEach(boton => {
        const elegido = seleccionados.has(boton.dataset.id);
        boton.classList.toggle('selected', elegido);
        boton.setAttribute('aria-pressed', String(elegido));
    });
}

async function verCaracteristicas(idAsiento) {
    const consulta = ++detalleActual;
    try {
        const respuesta = await fetch('/api/asientos/' + encodeURIComponent(idAsiento) + '/caracteristicas');
        if (!respuesta.ok) throw new Error('No se pudo consultar el asiento.');
        const asiento = await respuesta.json();
        if (consulta !== detalleActual) return;
        document.getElementById('lbl-id').textContent = asiento.id;
        document.getElementById('lbl-distancia').textContent = asiento.distanciaPantalla;
        document.getElementById('lbl-angulo').textContent = asiento.anguloVision;
        document.getElementById('lbl-conveniencia').textContent = asiento.nivelConveniencia;
        document.getElementById('lbl-estado').textContent = asiento.disponible ? 'Disponible' : 'Ocupado';
        document.getElementById('lbl-estado').style.color = asiento.disponible ? '#38bdf8' : '#ef4444';
        document.getElementById('lbl-simulacion').textContent =
            'Simulación orientativa: ' + asiento.distanciaPantalla + ' m y ' + asiento.anguloVision + '°.';
        document.getElementById('panel-caracteristicas').style.display = 'block';
        if (!asiento.disponible) {
            seleccionados.delete(asiento.id);
            await cargarMapa();
            document.getElementById('seleccion-resumen').textContent = 'El asiento ' + asiento.id + ' está ocupado.';
        }
    } catch (error) {
        if (consulta === detalleActual) document.getElementById('seleccion-resumen').textContent = error.message;
    }
}

function cerrarPanel() {
    ++detalleActual;
    document.getElementById('panel-caracteristicas').style.display = 'none';
}

async function cargarMapa() {
    const actualizar = document.getElementById('actualizar-mapa');
    actualizar.disabled = true;
    try {
        const respuesta = await fetch('/api/asientos', { cache: 'no-store' });
        if (!respuesta.ok) throw new Error('No se pudo cargar la sala.');
        mapa = await respuesta.json();
        const disponibles = new Set(mapa.filter(a => a.disponible).map(a => a.id));
        for (const id of seleccionados) if (!disponibles.has(id)) seleccionados.delete(id);
        const grid = document.getElementById('grid-asientos');
        grid.replaceChildren();
        const filas = new Map();
        mapa.forEach(asiento => {
            if (!filas.has(asiento.fila)) {
                const fila = document.createElement('div');
                fila.className = 'seat-row';
                const etiqueta = document.createElement('span');
                etiqueta.className = 'row-label';
                etiqueta.textContent = asiento.fila;
                fila.append(etiqueta);
                filas.set(asiento.fila, fila);
                grid.append(fila);
            }
            const boton = document.createElement('button');
            boton.type = 'button';
            boton.className = 'seat-button' + (asiento.disponible ? '' : ' occupied');
            boton.dataset.id = asiento.id;
            boton.textContent = asiento.disponible ? asiento.numero : '×';
            boton.disabled = !asiento.disponible;
            boton.title = 'Asiento ' + asiento.id + (asiento.disponible ? ': disponible' : ': ocupado');
            boton.setAttribute('aria-label', boton.title);
            boton.onclick = () => {
                if (seleccionados.has(asiento.id)) seleccionados.delete(asiento.id);
                else seleccionados.add(asiento.id);
                guardarSeleccion();
                verCaracteristicas(asiento.id);
            };
            filas.get(asiento.fila).append(boton);
        });
        guardarSeleccion();
        document.getElementById('estado-sala').textContent =
            mapa.length + ' asientos · ' + disponibles.size + ' disponibles · '
            + (mapa.length - disponibles.size) + ' ocupados';
    } catch (error) {
        document.getElementById('estado-sala').textContent = 'No se pudo actualizar la sala. Intenta de nuevo.';
        document.getElementById('continuar-compra').disabled = true;
    } finally {
        actualizar.disabled = false;
    }
}

try {
    const parametro = new URLSearchParams(location.search).get('seleccion');
    const guardados = parametro === null
        ? JSON.parse(sessionStorage.getItem('cineNovaSeleccion') || '[]')
        : parametro.split(',');
    if (Array.isArray(guardados)) guardados.forEach(id => {
        if (typeof id === 'string' && /^[A-G](10|[1-9])$/.test(id)) seleccionados.add(id);
    });
} catch (error) {
    sessionStorage.removeItem('cineNovaSeleccion');
}
document.getElementById('actualizar-mapa').addEventListener('click', cargarMapa);
document.getElementById('limpiar-seleccion').addEventListener('click', () => {
    seleccionados.clear();
    guardarSeleccion();
    cerrarPanel();
});
document.getElementById('continuar-compra').addEventListener('click', () => {
    guardarSeleccion();
    location.href = '/?comprar=1';
});
document.addEventListener('visibilitychange', () => { if (!document.hidden) cargarMapa(); });
cargarMapa();
