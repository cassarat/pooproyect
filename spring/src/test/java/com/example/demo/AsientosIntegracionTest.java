package com.example.demo;

import com.example.demo.modelo.AsientoRepository;
import com.example.demo.modelo.Sala;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AsientosIntegracionTest {
    private static final Path BASE = crearBaseTemporal();
    private static final Set<String> OCUPADOS = Set.of("A3", "B4", "B5", "C5", "C6", "D4", "D5",
            "E7", "E8", "F2", "F3", "G9", "G10");
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired Sala sala;
    @Autowired AsientoRepository repositorio;
    @Autowired DataSource dataSource;

    private static Path crearBaseTemporal() {
        try {
            Path ruta = Files.createTempFile("cine-integracion-", ".db");
            ruta.toFile().deleteOnExit();
            return ruta;
        } catch (java.io.IOException error) {
            throw new IllegalStateException(error);
        }
    }

    @DynamicPropertySource
    static void baseDePruebas(DynamicPropertyRegistry propiedades) {
        propiedades.add("spring.datasource.url", () -> "jdbc:sqlite:" + BASE);
    }

    @BeforeEach
    void restaurarSalaDePrueba() {
        var asientos = repositorio.findAll();
        asientos.forEach(a -> a.setDisponible(!OCUPADOS.contains(a.getId())));
        repositorio.saveAll(asientos);
    }

    @Test
    void mapaRecomendacionYSimulacionCompartenDisponibilidad() throws Exception {
        mvc.perform(get("/api/asientos"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(70))
                .andExpect(jsonPath("$[2].id").value("A3"))
                .andExpect(jsonPath("$[2].disponible").value(false));
        JsonNode asientos = json.readTree(mvc.perform(get("/api/recomendaciones/mejores?cantidad=2"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("asientos");
        assertEquals(2, asientos.size());
        assertEquals(asientos.get(0).get("fila"), asientos.get(1).get("fila"));
        assertEquals(asientos.get(0).get("numero").asInt() + 1, asientos.get(1).get("numero").asInt());
        for (JsonNode asiento : asientos) assertFalse(OCUPADOS.contains(asiento.get("id").asText()));
        mvc.perform(get("/api/vista-asiento?fila=A&numero=3")).andExpect(status().isConflict());
        mvc.perform(get("/api/asientos/ABC/caracteristicas")).andExpect(status().isBadRequest());
    }

    @Test
    void confirmacionPersisteYLaSemillaNoLiberaAsientos() throws Exception {
        mvc.perform(post("/api/boletos/compra").contentType(MediaType.APPLICATION_JSON)
                .content(compra(List.of("a1", " A2 "))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reserva.totalPagar").value(15.0));
        assertFalse(new Sala(repositorio).buscarAsiento("A", 1).isDisponible());
        // Volver a ejecutar la misma inicialización que al arrancar no borra reservas.
        new ResourceDatabasePopulator(new ClassPathResource("data.sql")).execute(dataSource);
        try (var conexion = DriverManager.getConnection("jdbc:sqlite:" + BASE);
             var consulta = conexion.createStatement();
             var resultado = consulta.executeQuery("select disponible from asiento where id = 'A1'")) {
            assertTrue(resultado.next());
            assertEquals(0, resultado.getInt(1));
        }
        mvc.perform(post("/api/boletos/compra").contentType(MediaType.APPLICATION_JSON)
                .content(compra(List.of("A1")))).andExpect(status().isConflict());
    }

    @Test
    void conflictoRevierteTodaLaSeleccion() throws Exception {
        mvc.perform(post("/api/boletos/compra").contentType(MediaType.APPLICATION_JSON)
                .content(compra(List.of("A1", "A3")))).andExpect(status().isConflict());
        assertTrue(sala.buscarAsiento("A", 1).isDisponible(), "No debe quedar una compra parcial");
    }

    @Test
    void rechazaSeleccionesInvalidasYTarifasInventadas() throws Exception {
        for (List<String> ids : List.of(List.<String>of(), List.of("A1", "a1"), List.of("Z1"), List.of("A0"))) {
            mvc.perform(post("/api/boletos/compra").contentType(MediaType.APPLICATION_JSON)
                    .content(compra(ids))).andExpect(status().isBadRequest());
        }
        mvc.perform(post("/api/boletos/compra").contentType(MediaType.APPLICATION_JSON)
                .content(compra(List.of("A1")).replace("parasite", "inventada")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/boletos/compra").contentType(MediaType.APPLICATION_JSON)
                .content(compra(List.of("A1")).replace("15:30", "00:01")))
                .andExpect(status().isBadRequest());
        assertTrue(sala.buscarAsiento("A", 1).isDisponible());
    }

    @Test
    void presupuestoEsTotalDelGrupoYUsaPrecioDeCartelera() throws Exception {
        mvc.perform(get("/api/recomendaciones/mejores?cantidad=2&presupuesto=14.99"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.asientos.length()").value(0));
        mvc.perform(get("/api/recomendaciones/mejores?cantidad=2&presupuesto=15.00"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.asientos.length()").value(2));
        mvc.perform(get("/api/recomendaciones/mejores?cantidad=2&peliculaId=ElPianista&presupuesto=14"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(14.0))
                .andExpect(jsonPath("$.asientos.length()").value(2));
        for (String parametros : List.of("cantidad=0", "cantidad=11", "cantidad=1&presupuesto=-1",
                "cantidad=2&presupuesto=NaN", "cantidad=1&peliculaId=inventada")) {
            mvc.perform(get("/api/recomendaciones/mejores?" + parametros)).andExpect(status().isBadRequest());
        }
    }

    @Test
    void dosConfirmacionesConcurrentesNoOcupanDosVecesElMismoAsiento() throws Exception {
        var ejecutor = Executors.newFixedThreadPool(2);
        Callable<Boolean> confirmar = () -> {
            try { sala.confirmarSeleccion(List.of("A1")); return true; }
            catch (IllegalStateException conflicto) { return false; }
        };
        try {
            var resultados = ejecutor.invokeAll(List.of(confirmar, confirmar));
            int exitos = 0;
            for (var resultado : resultados) if (resultado.get()) exitos++;
            assertEquals(1, exitos);
        } finally { ejecutor.shutdownNow(); }
    }

    @Test
    void salaLlenaNoProduceRecomendaciones() throws Exception {
        var asientos = repositorio.findAll();
        asientos.forEach(a -> a.setDisponible(false));
        repositorio.saveAll(asientos);
        mvc.perform(get("/api/recomendaciones/mejores?cantidad=1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.asientos.length()").value(0));
    }

    private String compra(List<String> ids) throws Exception {
        return json.writeValueAsString(java.util.Map.of("peliculaId", "parasite", "horario", "15:30",
                "asientos", ids, "nombreCliente", "Prueba"));
    }
}
