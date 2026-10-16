package com.example.demo.controlador;

import com.example.demo.modelo.Sala;
import com.example.demo.servicio.VistaAsientoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class VistaAsientoControllerTest {
    private final Sala sala = new Sala();
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new VistaAsientoController(sala, new VistaAsientoService())).build();

    @Test
    void consultaDatosDeLaSalaSinReservarElAsiento() throws Exception {
        mvc.perform(get("/api/vista-asiento").param("fila", "a").param("numero", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.anguloVision").value(45.0))
                .andExpect(jsonPath("$.distanciaPantalla").value(5.2));
        assertTrue(sala.buscarAsiento("A", 1).isDisponible());
    }

    @Test
    void rechazaOcupadosInexistentesYParametrosInvalidos() throws Exception {
        mvc.perform(get("/api/vista-asiento?fila=B&numero=1")).andExpect(status().isConflict());
        mvc.perform(get("/api/vista-asiento?fila=Z&numero=99")).andExpect(status().isNotFound());
        mvc.perform(get("/api/vista-asiento?fila=A&numero=0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/vista-asiento?fila=A&numero=texto")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/vista-asiento?numero=1")).andExpect(status().isBadRequest());
    }

    @Test
    void utilizaElEstadoActualCompartidoDeLaSala() throws Exception {
        sala.buscarAsiento("A", 1).setDisponible(false);
        mvc.perform(get("/api/vista-asiento?fila=A&numero=1")).andExpect(status().isConflict());
        mvc.perform(get("/api/vista-asiento/asientos"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].disponible").value(false));
    }
}
