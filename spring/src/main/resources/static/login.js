'use strict';
const formulario = document.getElementById('formulario');
const sesion = document.getElementById('sesion');
const mensaje = document.getElementById('mensaje');
const entrar = document.getElementById('entrar');

function mostrarUsuario(usuario) {
    formulario.hidden = true;
    sesion.hidden = false;
    document.getElementById('bienvenida').textContent = `Hola, ${usuario.nombre}`;
    formulario.reset();
}

formulario.addEventListener('submit', async function (evento) {
    evento.preventDefault();
    entrar.disabled = true;
    mensaje.textContent = 'Verificando datos…';
    try {
        const respuesta = await fetch('/api/usuarios/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nombreUsuario: document.getElementById('usuario').value,
                contrasena: document.getElementById('contrasena').value
            })
        });
        if (!respuesta.ok) {
            mensaje.textContent = respuesta.status === 401 ? 'Usuario o contraseña incorrectos.'
                : respuesta.status === 400 ? 'Completa los datos correctamente.' : 'No se pudo iniciar sesión.';
            return;
        }
        mostrarUsuario(await respuesta.json());
        mensaje.textContent = 'Sesión iniciada correctamente.';
    } catch (error) {
        mensaje.textContent = 'No se pudo conectar con el servidor. Intenta otra vez.';
    } finally {
        entrar.disabled = false;
    }
});

document.getElementById('salir').addEventListener('click', async function () {
    try {
        const respuesta = await fetch('/api/usuarios/logout', { method: 'POST' });
        if (!respuesta.ok) throw new Error();
        sesion.hidden = true;
        formulario.hidden = false;
        mensaje.textContent = 'Sesión cerrada.';
    } catch (error) {
        mensaje.textContent = 'No se pudo cerrar la sesión. Intenta otra vez.';
    }
});

async function consultarSesion() {
    entrar.disabled = true;
    try {
        const respuesta = await fetch('/api/usuarios/actual');
        if (respuesta.ok) mostrarUsuario(await respuesta.json());
        else if (respuesta.status !== 401) mensaje.textContent = 'No se pudo consultar la sesión.';
    } catch (error) {
        mensaje.textContent = 'No se pudo conectar con el servidor.';
    } finally {
        entrar.disabled = false;
    }
}
consultarSesion();
