package aventuras;

public class Principal {
    public static void main(String[] args) {
        // 1. Creamos la vista y el modelo de forma independiente
        Vista vista = new Vista();
        Modelo modelo = new Modelo();
        
        // 2. Se los pasamos al controlador para que vigile los botones
        new Controlador(vista, modelo);
        
        // 3. ¡ESTO ES LO QUE TE FALTA! Forzamos a la ventana a arrancar en el Login
        vista.mostrarLogin();
        
    }
}
