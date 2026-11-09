package com.example.demo.servicio;

import com.example.demo.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioService() {
        // Cuenta de demostración para probar la entrega localmente.
        usuarios.add(new Usuario("joseline", "Joseline Mazariegos", "Cine2026!"));
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        if (nombreUsuario == null) return null;
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario.trim())) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = buscarUsuario(nombreUsuario);
        if (usuario != null && usuario.autenticarse(contrasena)) return usuario;
        return null;
    }
}
