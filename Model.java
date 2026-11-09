public class Asiento {
    private String fila;
    private int numero;
    private boolean disponible;
    private boolean seleccionado;
    private double distanciaPantalla;
    private double anguloVision;
    private double calificacion;
    private String vista360;

    public Asiento(String fila, int numero, double distanciaPantalla, double anguloVision, double calificacion, String vista360) {
        this.fila = fila;
        this.numero = numero;
        this.disponible = true;
        this.seleccionado = false;
        this.distanciaPantalla = distanciaPantalla;
        this.anguloVision = anguloVision;
        this.calificacion = calificacion;
        this.vista360 = vista360;
    }

    public boolean estaDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void seleccionar() {
        if (disponible) {
            this.seleccionado = true;
        }
    }

    public void desmarcar() {
        this.seleccionado = false;
    }

    public boolean estaSeleccionado() {
        return seleccionado;
    }

    public String getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public String obtenerCaracteristicas() {
        return "Asiento " + fila + numero + 
               " | Distancia: " + distanciaPantalla + "m" +
               " | Ángulo: " + anguloVision + "°" +
               " | Calificación: " + calificacion;
    }
}


---

### 2. Actualización de la Clase Model: `Sala.java`
Mantiene la gestión de la matriz de asientos y el método para localizarlos dentro de la sala[cite: 9].

```java
import java.util.ArrayList;
import java.util.List;

public class Sala {
    private String tamanoPantalla;
    private int filas;
    private List<Asiento> asientos;

    public Sala(String tamanoPantalla, int totalFilas, int asientosPorFila) {
        this.tamanoPantalla = tamanoPantalla;
        this.asientos = new ArrayList<>();
        inicializarAsientos(totalFilas, asientosPorFila);
    }

    private void inicializarAsientos(int totalFilas, int asientosPorFila) {
        String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (int i = 0; i < totalFilas && i < letras.length; i++) {
            for (int j = 1; j <= asientosPorFila; j++) {
                double distancia = 2.0 + (i * 0.7);
                double angulo = 90.0 - (Math.abs(j - (asientosPorFila / 2.0)) * 4.0);
                double calificacion = 10.0 - (i * 0.4);
                
                Asiento asiento = new Asiento(letras[i], j, distancia, angulo, calificacion, "vista_" + letras[i] + j + ".jpg");
                asientos.add(asiento);
            }
        }
    }

    public List<Asiento> mostrarAsientos() {
        return asientos;
    }

    public Asiento buscarAsiento(String fila, int numero) {
        for (Asiento a : asientos) {
            if (a.getFila().equalsIgnoreCase(fila) && a.getNumero() == numero) {
                return a;
            }
        }
        return null;
    }
}


---

### 3. Actualización del Controller: `AsientoController.java`
Coordina la lógica entre la vista y el modelo, permitiendo agregar o remover múltiples asientos seleccionados de forma segura[cite: 13].

```java
import java.util.ArrayList;
import java.util.List;

public class AsientoController {
    private Sala sala;
    private List<Asiento> asientosSeleccionados;

    public AsientoController(Sala sala) {
        this.sala = sala;
        this.asientosSeleccionados = new ArrayList<>();
    }

    public void cargarMapa() {
        // Método base requerido por el diseño MVC del proyecto
        sala.mostrarAsientos();
    }

    public boolean seleccionarAsiento(String fila, int numero) {
        Asiento asiento = sala.buscarAsiento(fila, numero);
        if (asiento != null) {
            if (asiento.estaDisponible() && !asiento.estaSeleccionado()) {
                asiento.seleccionar();
                asientosSeleccionados.add(asiento);
                return true;
            }
        }
        return false;
    }

    public boolean deseleccionarAsiento(String fila, int numero) {
        Asiento asiento = sala.buscarAsiento(fila, numero);
        if (asiento != null && asiento.estaSeleccionado()) {
            asiento.desmarcar();
            asientosSeleccionados.remove(asiento);
            return true;
        }
        return false;
    }

    public List<Asiento> getAsientosSeleccionados() {
        return asientosSeleccionados;
    }

    public String consultarCaracteristicas(String fila, int numero) {
        Asiento asiento = sala.buscarAsiento(fila, numero);
        if (asiento != null) {
            return asiento.obtenerCaracteristicas();
        }
        return "Asiento no encontrado.";
    }
}


---

### 4. Actualización de la Vista: `MapaAsientosVista.java`
Gestiona la interacción con el usuario para mostrar el mapa y permitir la selección de uno o varios asientos de manera fluida[cite: 11].

```java
import java.util.List;
import java.util.Scanner;

public class MapaAsientosVista {
    private AsientoController controller;
    private Scanner scanner;

    public MapaAsientosVista(AsientoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMapa() {
        System.out.println("\n========== PANTALLA DE CINE ==========");
        System.out.println("[D] = Disponible | [X] = Ocupado | [S] = Seleccionado\n");
        
        String filaActual = "";
        for (Asiento a : controller.getAsientosSeleccionados().isEmpty() ? 
             obtenerTodosLosAsientosDeSala() : obtenerTodosLosAsientosDeSala()) {
            // Nota: Aquí se imprimen los estados de los asientos ordenados por fila
            if (!a.getFila().equals(filaActual)) {
                filaActual = a.getFila();
                System.out.print("\nFila " + filaActual + ": ");
            }
            String estado = "[D]";
            if (!a.estaDisponible()) {
                estado = "[X]";
            } else if (a.estaSeleccionado()) {
                estado = "[S]";
            }
            System.out.print(a.getNumero() + estado + " ");
        }
        System.out.println("\n");
    }

    private List<Asiento> obtenerTodosLosAsientosDeSala() {
        // Auxiliar para listar la sala completa
        return new Sala("", 0, 0).mostrarAsientos(); // O conectar directamente con la instancia de Sala si se prefiere
    }

    public void iniciarInterfazSeleccionMultiple() {
        int opcion = 0;
        do {
            System.out.println("\n--- GESTIÓN DE SELECCIÓN DE ASIENTOS ---");
            System.out.println("1. Seleccionar un asiento");
            System.out.println("2. Quitar selección de un asiento");
            System.out.println("3. Ver lista de asientos seleccionados");
            System.out.println("4. Consultar características de un asiento");
            System.out.println("5. Finalizar y confirmar selección");
            System.out.print("Seleccione una opción: ");
            
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese la fila (ej. A, B, C): ");
                    String fSel = scanner.nextLine().trim();
                    System.out.print("Ingrese el número de asiento: ");
                    int nSel = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (controller.seleccionarAsiento(fSel, nSel)) {
                        System.out.println("¡Asiento " + fSel + nSel + " agregado a su selección!");
                    } else {
                        System.out.println("No se pudo seleccionar el asiento (verifique disponibilidad).");
                    }
                    break;
                case 2:
                    System.out.print("Ingrese la fila del asiento a desmarcar: ");
                    String fDes = scanner.nextLine().trim();
                    System.out.print("Ingrese el número de asiento: ");
                    int nDes = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (controller.deseleccionarAsiento(fDes, nDes)) {
                        System.out.println("Asiento " + fDes + nDes + " removido de la selección.");
                    } else {
                        System.out.println("No se pudo deseleccionar.");
                    }
                    break;
                case 3:
                    List<Asiento> seleccionados = controller.getAsientosSeleccionados();
                    System.out.println("\n--- SUS ASIENTOS ELEGIDOS ---");
                    if (seleccionados.isEmpty()) {
                        System.out.println("Aún no ha seleccionado ningún asiento.");
                    } else {
                        for (Asiento a : seleccionados) {
                            System.out.println("- " + a.obtenerCaracteristicas());
                        }
                    }
                    break;
                case 4:
                    System.out.print("Ingrese la fila a consultar: ");
                    String fCon = scanner.nextLine().trim();
                    System.out.print("Ingrese el número: ");
                    int nCon = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(controller.consultarCaracteristicas(fCon, nCon));
                    break;
                case 5:
                    System.out.println("Proceso de selección concluido con éxito.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 5);
    }
}
