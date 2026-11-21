'use strict';
const modalCompra = document.getElementById('modalCompra');
const modalFormContent = document.getElementById('modalFormContent');
const ticketReceipt = document.getElementById('ticketReceipt');
const modalSeatChips = document.getElementById('modalSeatChips');
const confirmarCompra = document.getElementById('compra-confirmar');
const estadoCompra = document.getElementById('compra-estado');
let asientosSeleccionadosModal = [];
let asientosModal = [];
let comprando = false;
let cargaModal = 0;

function actualizarSelectorPeliculas() {
    const select = document.getElementById('selectPelicula');
    select.replaceChildren();
    peliculasData.forEach(pelicula => {
        const opcion = document.createElement('option');
        opcion.value = pelicula.id;
        opcion.textContent = pelicula.titulo + ' ($' + pelicula.precio.toFixed(2) + ')';
        select.append(opcion);
    });
    actualizarPrecioModal();
}

function actualizarPrecioModal() {
    const pelicula = peliculasData.find(p => p.id === document.getElementById('selectPelicula').value);
    const horarios = document.getElementById('selectHorario');
    if (pelicula) {
        const anterior = horarios.value;
        horarios.replaceChildren();
        pelicula.horarios.forEach(hora => {
            const opcion = document.createElement('option');
            opcion.value = hora;
            opcion.textContent = hora;
            horarios.append(opcion);
        });
        if (pelicula.horarios.includes(anterior)) horarios.value = anterior;
        sessionStorage.setItem('cineNovaPelicula', pelicula.id);
    }
    const cantidad = asientosSeleccionadosModal.length;
    const precio = pelicula ? pelicula.precio : 0;
    document.getElementById('resumenAsientosCount').textContent = cantidad;
    document.getElementById('resumenPrecioUnitario').textContent = '$' + precio.toFixed(2);
    document.getElementById('resumenTotal').textContent = '$' + (precio * cantidad).toFixed(2);
    confirmarCompra.disabled = comprando || cantidad === 0 || !pelicula;
}

function initSeatChips() {
    modalSeatChips.replaceChildren();
    asientosModal.forEach(asiento => {
        const chip = document.createElement('button');
        chip.type = 'button';
        chip.className = 'seat-chip' + (asientosSeleccionadosModal.includes(asiento.id) ? ' active' : '');
        chip.textContent = asiento.id + (asiento.disponible ? '' : ' ×');
        chip.disabled = !asiento.disponible || comprando;
        chip.setAttribute('aria-label', asiento.id + (asiento.disponible ? ', disponible' : ', ocupado'));
        chip.setAttribute('aria-pressed', String(asientosSeleccionadosModal.includes(asiento.id)));
        chip.onclick = () => {
            if (asientosSeleccionadosModal.includes(asiento.id)) {
                asientosSeleccionadosModal = asientosSeleccionadosModal.filter(id => id !== asiento.id);
            } else {
                asientosSeleccionadosModal.push(asiento.id);
            }
            sessionStorage.setItem('cineNovaSeleccion', JSON.stringify(asientosSeleccionadosModal));
            initSeatChips();
            actualizarPrecioModal();
        };
        modalSeatChips.append(chip);
    });
}

async function cargarAsientosModal() {
    const respuesta = await fetch('/api/asientos', { cache: 'no-store' });
    if (!respuesta.ok) throw new Error('No se pudo consultar la disponibilidad.');
    asientosModal = await respuesta.json();
    const libres = new Set(asientosModal.filter(asiento => asiento.disponible).map(asiento => asiento.id));
    asientosSeleccionadosModal = asientosSeleccionadosModal.filter(id => libres.has(id));
    sessionStorage.setItem('cineNovaSeleccion', JSON.stringify(asientosSeleccionadosModal));
    initSeatChips();
    actualizarPrecioModal();
}

async function abrirModalCompra(peliculaId) {
    if (comprando) return;
    const consulta = ++cargaModal;
    const select = document.getElementById('selectPelicula');
    const elegida = peliculaId || sessionStorage.getItem('cineNovaPelicula');
    if (peliculasData.some(p => p.id === elegida)) select.value = elegida;
    try {
        const guardados = JSON.parse(sessionStorage.getItem('cineNovaSeleccion') || '[]');
        asientosSeleccionadosModal = Array.isArray(guardados)
            ? [...new Set(guardados.filter(id => typeof id === 'string'))] : [];
    } catch (error) {
        asientosSeleccionadosModal = [];
    }
    modalFormContent.style.display = 'block';
    ticketReceipt.classList.remove('active');
    modalCompra.classList.add('active');
    modalSeatChips.replaceChildren();
    confirmarCompra.disabled = true;
    estadoCompra.textContent = 'Consultando asientos disponibles…';
    actualizarPrecioModal();
    confirmarCompra.disabled = true;
    try {
        await cargarAsientosModal();
        if (consulta !== cargaModal) return;
        estadoCompra.textContent = 'Los asientos con × están ocupados. La selección se confirma al emitir los boletos.';
    } catch (error) {
        estadoCompra.textContent = error.message;
        confirmarCompra.disabled = true;
    }
}

function cerrarModalCompra() {
    if (comprando) return;
    ++cargaModal;
    modalCompra.classList.remove('active');
}

async function procesarCompra() {
    if (comprando || confirmarCompra.disabled) return;
    comprando = true;
    actualizarPrecioModal();
    initSeatChips();
    estadoCompra.textContent = 'Confirmando disponibilidad…';
    try {
        const respuesta = await fetch('/api/boletos/compra', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                peliculaId: document.getElementById('selectPelicula').value,
                horario: document.getElementById('selectHorario').value,
                asientos: asientosSeleccionadosModal,
                nombreCliente: document.getElementById('inputNombre').value.trim(),
                correo: document.getElementById('inputCorreo').value.trim()
            })
        });
        const datos = await respuesta.json();
        if (!respuesta.ok || !datos.exito) {
            if (respuesta.status === 409) await cargarAsientosModal();
            throw new Error(datos.error || 'No se pudo confirmar la selección.');
        }
        const reserva = datos.reserva;
        document.getElementById('ticketCodigo').textContent = reserva.codigoReserva;
        document.getElementById('ticketPelicula').textContent = reserva.pelicula;
        document.getElementById('ticketHorario').textContent = reserva.horario;
        document.getElementById('ticketAsientos').textContent = reserva.asientos.join(', ');
        document.getElementById('ticketTotal').textContent = '$' + reserva.totalPagar.toFixed(2);
        document.getElementById('ticketTitular').textContent = reserva.cliente;
        asientosSeleccionadosModal = [];
        sessionStorage.removeItem('cineNovaSeleccion');
        modalFormContent.style.display = 'none';
        ticketReceipt.classList.add('active');
    } catch (error) {
        estadoCompra.textContent = error instanceof TypeError
            ? 'No se pudo conectar. Actualiza la disponibilidad antes de volver a confirmar.' : error.message;
    } finally {
        comprando = false;
        initSeatChips();
        actualizarPrecioModal();
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    await cargarCartelera();
    actualizarSelectorPeliculas();
    if (new URLSearchParams(location.search).get('comprar') === '1') abrirModalCompra();
});
