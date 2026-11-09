package com.example.demo.controlador;

import com.example.demo.servicio.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {
    private final UsuarioService servicio = new UsuarioService();
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UsuarioController(servicio)).build();

    @Test
    void buscaUsuariosYVerificaContrasenas() {
        assertNotNull(servicio.buscarUsuario(" JOSELINE "));
        assertNull(servicio.buscarUsuario("desconocido"));
        assertNull(servicio.buscarUsuario(null));
        assertNull(servicio.autenticar("joseline", "incorrecta"));
        assertNull(servicio.autenticar("joseline", null));
        assertNotNull(servicio.autenticar("joseline", "Cine2026!"));
    }

    @Test
    void iniciaConsultaYCierraSesionSinExponerContrasena() throws Exception {
        MockHttpSession sesion = new MockHttpSession();
        String idAnterior = sesion.getId();
        mvc.perform(post("/api/usuarios/login").session(sesion).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreUsuario\":\"joseline\",\"contrasena\":\"Cine2026!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("joseline"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.clave").doesNotExist())
                .andExpect(jsonPath("$.sal").doesNotExist());
        assertNotEquals(idAnterior, sesion.getId());
        mvc.perform(get("/api/usuarios/actual").session(sesion))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Joseline Mazariegos"));
        mvc.perform(post("/api/usuarios/logout").session(sesion)).andExpect(status().isNoContent());
        assertTrue(sesion.isInvalid());
        mvc.perform(get("/api/usuarios/actual")).andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaDatosVaciosYCredencialesIncorrectas() throws Exception {
        mvc.perform(get("/api/usuarios/actual")).andExpect(status().isUnauthorized());
        for (String contenido : new String[]{"{}", "null", "{\"nombreUsuario\":\"\",\"contrasena\":\"x\"}"}) {
            mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON).content(contenido))
                    .andExpect(status().isBadRequest());
        }
        for (String nombre : new String[]{"joseline", "desconocido"}) {
            mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombreUsuario\":\"" + nombre + "\",\"contrasena\":\"incorrecta\"}"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
