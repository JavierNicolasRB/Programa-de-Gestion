package aventuras;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;

public class Vista {
    public Frame ventana = new Frame("AventurasDB - Panel de Control");

    // Componentes del LOGIN
    public Panel panelLogin = new Panel();
    public TextField txtUsuario = new TextField(12);
    public TextField txtClave = new TextField(12);
    public Button btnEntrar = new Button("Entrar");
    public Button btnLimpiar = new Button("Limpiar");
    
    public Button btnTabAyuda = new Button("Ayuda"); 

    // --- DISEÑO MENÚ PRINCIPAL ---
    public Panel panelTabs = new Panel(new GridLayout(1, 6, 5, 0)); 
    public Button btnTabMonitores = new Button("Monitores");
    public Button btnTabAventureros = new Button("Aventureros");
    public Button btnTabActividades = new Button("Actividades");
    public Button btnTabParticipan = new Button("Participaciones");
    public Button btnTabSalir = new Button("Salir");

    // Bloque central de acciones
    public Panel panelAcciones = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 30));
    public Panel contenedorVertical = new Panel(new GridLayout(4, 1, 0, 8)); 
    public Button btnAlta = new Button("Alta");
    public Button btnBaja = new Button("Baja");
    public Button btnModificacion = new Button("Modificación");
    public Button btnConsulta = new Button("Consulta");

    // --- DIÁLOGOS Y COMPONENTES (CON CAMPOS DE FECHA AÑADIDOS) ---
    public Dialog dlgFormMonitor = new Dialog(ventana, "Datos Monitor", true);
    public TextField txtMonNombre = new TextField(20);
    public TextField txtMonApellidos = new TextField(20);
    public TextField txtMonEmail = new TextField(20);
    public TextField txtMonSalary = new TextField(10); 
    public TextField txtMonFechaIngreso = new TextField(10); // NUEVO
    public Button btnGuardarMonitor = new Button("Confirmar Registro");

    public Dialog dlgFormAventurero = new Dialog(ventana, "Alta Aventurero", true);
    public TextField txtAveNombre = new TextField(20);
    public TextField txtAveApellidos = new TextField(20);
    public TextField txtAveEmail = new TextField(20);
    public TextField txtAveTelefono = new TextField(15);
    public TextField txtAveFechaNac = new TextField(10); // NUEVO
    public Button btnGuardarAventurero = new Button("Registrar Aventurero");

    public Dialog dlgFormActividad = new Dialog(ventana, "Alta Actividad", true);
    public TextField txtActNombre = new TextField(20);
    public TextField txtActPrecio = new TextField(10);
    public TextField txtActDuration = new TextField(10); 
    public Choice choMonitoresActDlg = new Choice();
    public TextField txtActFechaAct = new TextField(10); // NUEVO
    public Button btnGuardarActividad = new Button("Registrar Actividad");

    public Dialog dlgFormPart = new Dialog(ventana, "Nueva Participación", true);
    public Choice choAventurerosDlg = new Choice();
    public Choice choActividadesDlg = new Choice();
    public TextField txtHoraDlg = new TextField(10);
    public TextField txtPartFechaInsc = new TextField(10); // NUEVO
    public Button btnGuardarParticipacion = new Button("Confirmar");

    public Dialog dlgSeleccion = new Dialog(ventana, "Seleccionar Registro", true);
    public Choice choSeleccionGeneral = new Choice();
    public Button btnConfirmarBaja = new Button("Confirmar Baja");
    public Button btnConfirmarMod = new Button("Editar Registro"); 

    public Dialog dlgConsulta = new Dialog(ventana, "Consulta de Registros", true);
    public Label lblTituloConsulta = new Label("Consulta", Label.CENTER);
    public List lstTablaConsulta = new List();
    public ScrollPane scrollConsulta = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    public Button btnExportarPDF = new Button("Exportar a PDF");

    public Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    public Label lblMensaje = new Label("");
    
    public Dialog dlgExitoPDF = new Dialog(ventana, "Exportación Exitosa", true);
    public TextField txtRutaPDF = new TextField();
    public Button btnCerrarExitoPDF = new Button("Aceptar");

    public Vista() {
        panelLogin.setLayout(new GridLayout(4, 1, 0, 8));
        txtClave.setEchoChar('*');

        Panel filaUser = new Panel(new FlowLayout(FlowLayout.CENTER));
        filaUser.add(new Label("Usuario:")); filaUser.add(txtUsuario);
        panelLogin.add(filaUser);

        Panel filaPass = new Panel(new FlowLayout(FlowLayout.CENTER));
        filaPass.add(new Label("Contraseña:")); filaPass.add(txtClave);
        panelLogin.add(filaPass);

        Panel filaBtn = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        filaBtn.add(btnEntrar);
        filaBtn.add(btnLimpiar);
        panelLogin.add(filaBtn);

        contenedorVertical.setPreferredSize(new Dimension(160, 150));
        contenedorVertical.add(btnAlta);
        contenedorVertical.add(btnBaja);
        contenedorVertical.add(btnModificacion);
        contenedorVertical.add(btnConsulta);

        Color amarilloMenu = new Color(255, 255, 160);
        btnAlta.setBackground(amarilloMenu);
        btnBaja.setBackground(amarilloMenu);
        btnModificacion.setBackground(amarilloMenu);
        btnConsulta.setBackground(amarilloMenu);

        configurarDialogos();
    }

    public void mostrarLogin() {
        ventana.removeAll();
        ventana.setSize(350, 200);
        ventana.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        ventana.add(panelLogin);
        ventana.validate();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    public void prepararLayoutPrincipal() {
        ventana.removeAll();
        ventana.setSize(600, 400);
        ventana.setLayout(new BorderLayout());
        ventana.setLocationRelativeTo(null);

        panelTabs.removeAll();
        panelTabs.add(btnTabMonitores);
        panelTabs.add(btnTabAventureros);
        panelTabs.add(btnTabActividades);
        panelTabs.add(btnTabParticipan);
        panelTabs.add(btnTabAyuda);
        panelTabs.add(btnTabSalir); 

        panelAcciones.removeAll();
        panelAcciones.add(contenedorVertical);

        ventana.add(panelTabs, BorderLayout.NORTH);
        ventana.add(panelAcciones, BorderLayout.CENTER);
        ventana.validate();
    }

    public void resaltarTab(Button botonActivo) {
        btnTabMonitores.setBackground(Color.LIGHT_GRAY);
        btnTabAventureros.setBackground(Color.LIGHT_GRAY);
        btnTabActividades.setBackground(Color.LIGHT_GRAY);
        btnTabParticipan.setBackground(Color.LIGHT_GRAY);
        btnTabAyuda.setBackground(Color.LIGHT_GRAY);
        btnTabSalir.setBackground(Color.LIGHT_GRAY);
        botonActivo.setBackground(new Color(255, 255, 160));
    }

    public void setMenuBasico() {
        btnAlta.setEnabled(true);
        btnBaja.setEnabled(false);
        btnModificacion.setEnabled(false);
        btnConsulta.setEnabled(false);
    }

    public void setMenuAdmin() {
        btnAlta.setEnabled(true);
        btnBaja.setEnabled(true);
        btnModificacion.setEnabled(true);
        btnConsulta.setEnabled(true);
    }

    public void mostrarAviso(String texto) {
        lblMensaje.setText(texto);
        dlgMensaje.setLocationRelativeTo(ventana);
        dlgMensaje.setVisible(true);
    }

    private void configurarDialogos() {
        dlgConsulta.setLayout(new BorderLayout(10, 10));
        dlgConsulta.setSize(550, 380);
        Panel panelSurPDF = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelSurPDF.add(btnExportarPDF);
        scrollConsulta.add(lstTablaConsulta);
        dlgConsulta.add(lblTituloConsulta, BorderLayout.NORTH);
        dlgConsulta.add(scrollConsulta, BorderLayout.CENTER); 
        dlgConsulta.add(panelSurPDF, BorderLayout.SOUTH);

        dlgFormMonitor.setLayout(new FlowLayout()); dlgFormMonitor.setSize(300, 360);
        dlgFormMonitor.add(new Label("Nombre:")); dlgFormMonitor.add(txtMonNombre);
        dlgFormMonitor.add(new Label("Apellidos:")); dlgFormMonitor.add(txtMonApellidos);
        dlgFormMonitor.add(new Label("Email:")); dlgFormMonitor.add(txtMonEmail);
        dlgFormMonitor.add(new Label("Salario (€):")); dlgFormMonitor.add(txtMonSalary);
        dlgFormMonitor.add(new Label("Fecha Ingreso (dd/MM/yyyy):")); dlgFormMonitor.add(txtMonFechaIngreso); // NUEVO
        dlgFormMonitor.add(btnGuardarMonitor); 

        dlgFormAventurero.setLayout(new FlowLayout()); dlgFormAventurero.setSize(300, 360);
        dlgFormAventurero.add(new Label("Nombre:")); dlgFormAventurero.add(txtAveNombre);
        dlgFormAventurero.add(new Label("Apellidos:")); dlgFormAventurero.add(txtAveApellidos);
        dlgFormAventurero.add(new Label("Email:")); dlgFormAventurero.add(txtAveEmail);
        dlgFormAventurero.add(new Label("Teléfono:")); dlgFormAventurero.add(txtAveTelefono);
        dlgFormAventurero.add(new Label("Fecha Nac. (dd/MM/yyyy):")); dlgFormAventurero.add(txtAveFechaNac); // NUEVO
        dlgFormAventurero.add(btnGuardarAventurero); 

        dlgFormActividad.setLayout(new FlowLayout()); dlgFormActividad.setSize(300, 360);
        dlgFormActividad.add(new Label("Nombre Actividad:")); dlgFormActividad.add(txtActNombre);
        dlgFormActividad.add(new Label("Precio (€):")); dlgFormActividad.add(txtActPrecio);
        dlgFormActividad.add(new Label("Duración (h):")); dlgFormActividad.add(txtActDuration); 
        dlgFormActividad.add(new Label("Monitor:")); dlgFormActividad.add(choMonitoresActDlg);
        dlgFormActividad.add(new Label("Fecha Act. (dd/MM/yyyy):")); dlgFormActividad.add(txtActFechaAct); // NUEVO
        dlgFormActividad.add(btnGuardarActividad); 

        dlgFormPart.setLayout(new FlowLayout()); dlgFormPart.setSize(350, 360);
        dlgFormPart.add(new Label("Aventurero:")); dlgFormPart.add(choAventurerosDlg);
        dlgFormPart.add(new Label("Actividad:")); dlgFormPart.add(choActividadesDlg);
        dlgFormPart.add(new Label("Hora (HH:MM):")); dlgFormPart.add(txtHoraDlg);
        dlgFormPart.add(new Label("Fecha Insc. (dd/MM/yyyy):")); dlgFormPart.add(txtPartFechaInsc); // NUEVO
        dlgFormPart.add(btnGuardarParticipacion);

        dlgSeleccion.setLayout(new FlowLayout()); dlgSeleccion.setSize(380, 160);
        dlgSeleccion.add(new Label("Seleccione elemento:"));
        dlgSeleccion.add(choSeleccionGeneral); 
        dlgSeleccion.add(btnConfirmarBaja);
        dlgSeleccion.add(btnConfirmarMod);

        dlgMensaje.setLayout(new FlowLayout()); dlgMensaje.setSize(300, 120);
        dlgMensaje.add(lblMensaje);
        
        dlgExitoPDF.setLayout(new BorderLayout());
        Panel panelCentro = new Panel(new GridLayout(2, 1));
        panelCentro.add(new Label("El archivo PDF se ha guardado correctamente en:", Label.CENTER));
        txtRutaPDF.setEditable(false);
        panelCentro.add(txtRutaPDF);
        dlgExitoPDF.add(panelCentro, BorderLayout.CENTER);
        Panel panelSur = new Panel();
        panelSur.add(btnCerrarExitoPDF);
        dlgExitoPDF.add(panelSur, BorderLayout.SOUTH);
        dlgExitoPDF.setSize(450, 130);
    }
}
