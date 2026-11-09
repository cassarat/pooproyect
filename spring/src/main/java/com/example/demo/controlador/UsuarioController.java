package com.example.demo.controlador;

import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    public record Credenciales(String nombreUsuario, String contrasena) {}

    @PostMapping("/login")
    public Usuario iniciarSesion(@RequestBody Credenciales datos, HttpServletRequest solicitud) {
        if (datos == null || datos.nombreUsuario() == null || datos.nombreUsuario().isBlank()
                || datos.contrasena() == null || datos.contrasena().isBlank()
                || datos.nombreUsuario().length() > 100 || datos.contrasena().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completa usuario y contraseña.");
        }
        Usuario usuario = servicio.autenticar(datos.nombreUsuario(), datos.contrasena());
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos.");
        }
        HttpSession sesion = solicitud.getSession();
        solicitud.changeSessionId();
        sesion.setAttribute("usuario", usuario.getNombreUsuario());
        return usuario;
    }

    @GetMapping("/actual")
    public Usuario usuarioActual(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión primero.");
        }
        return servicio.buscarUsuario((String) sesion.getAttribute("usuario"));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cerrarSesion(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null) sesion.invalidate();
    }
}
