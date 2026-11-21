'use strict';
const formularioRecomendacion = document.getElementById('formulario-recomendacion');
const resultado = document.getElementById('resultado');
const botonRecomendar = document.getElementById('boton-recomendar');
const moneda = new Intl.NumberFormat('es-GT', { style: 'currency', currency: 'USD' });
let consultaActual = 0;

function mensajeResultado(texto) {
    const mensaje = document.createElement('p');
    mensaje.className = 'mensaje';
    mensaje.textContent = texto;
    resultado.replaceChildren(mensaje);
}

async function cargarCarteleraRecomendacion() {
    try {
        const respuesta = await fetch('/api/peliculas');
        if (!respuesta.ok) throw new Error();
        const peliculas = await respuesta.json();
        const lista = document.getElementById('pelicula');
        lista.replaceChildren();
        peliculas.forEach(pelicula => {
            const opcion = document.createElement('option');
            opcion.value = pelicula.id;
            opcion.textContent = pelicula.titulo + ' · ' + moneda.format(pelicula.precio) + ' por persona';
            lista.append(opcion);
        });
        const guardada = sessionStorage.getItem('cineNovaPelicula');
        if (peliculas.some(pelicula => pelicula.id === guardada)) lista.value = guardada;
        botonRecomendar.disabled = false;
    } catch (error) {
        mensajeResultado('No se pudo cargar la cartelera. Recarga la página para reintentar.');
    }
}

formularioRecomendacion.addEventListener('input', () => {
    ++consultaActual;
    mensajeResultado('Preferencias actualizadas. Busca de nuevo para obtener una recomendación.');
});

formularioRecomendacion.addEventListener('submit', async evento => {
    evento.preventDefault();
    if (!formularioRecomendacion.reportValidity()) return;
    const consulta = ++consultaActual;
    const parametros = new URLSearchParams({
        cantidad: document.getElementById('cantidad').value,
        peliculaId: document.getElementById('pelicula').value
    });
    const presupuesto = document.getElementById('presupuesto').value;
    if (presupuesto !== '') parametros.set('presupuesto', presupuesto);
    botonRecomendar.disabled = true;
    mensajeResultado('Buscando asientos disponibles y consecutivos…');
    try {
        const respuesta = await fetch('/api/recomendaciones/mejores?' + parametros, { cache: 'no-store' });
        const datos = await respuesta.json();
        if (consulta !== consultaActual) return;
        if (!respuesta.ok) throw new Error(datos.error || 'Verifica las preferencias ingresadas.');
        mensajeResultado(datos.mensaje);
        if (!datos.asientos || datos.asientos.length === 0) return;
        const total = document.createElement('p');
        total.className = 'mensaje';
        total.textContent = 'Total del grupo: ' + moneda.format(datos.total)
            + ' (' + moneda.format(datos.precioUnitario) + ' por boleto).';
        resultado.append(total);
        const contenedor = document.createElement('div');
        contenedor.className = 'seats-container';
        datos.asientos.forEach(asiento => {
            const tarjeta = document.createElement('article');
            tarjeta.className = 'asiento-tarjeta';
            const titulo = document.createElement('h2');
            titulo.className = 'asiento-id';
            titulo.textContent = asiento.id;
            tarjeta.append(titulo);
            ['Distancia: ' + asiento.distanciaPantalla + ' m',
                'Ángulo: ' + asiento.anguloVision + '°',
                'Conveniencia: ' + asiento.nivelConveniencia + '/5'].forEach(texto => {
                const linea = document.createElement('p');
                linea.className = 'asiento-stat';
                linea.textContent = texto;
                tarjeta.append(linea);
            });
            contenedor.append(tarjeta);
        });
        resultado.append(contenedor);
        const enlace = document.createElement('a');
        enlace.className = 'volver';
        enlace.textContent = 'Ver y seleccionar estos asientos en el mapa →';
        enlace.href = '/mapa-asientos.html?' + new URLSearchParams({
            seleccion: datos.asientos.map(asiento => asiento.id).join(',')
        });
        enlace.addEventListener('click', () => sessionStorage.setItem('cineNovaPelicula', parametros.get('peliculaId')));
        resultado.append(enlace);
    } catch (error) {
        if (consulta === consultaActual) mensajeResultado(error instanceof TypeError
            ? 'No se pudo conectar con el recomendador. Intenta nuevamente.' : error.message);
    } finally {
        botonRecomendar.disabled = false;
    }
});
cargarCarteleraRecomendacion();
