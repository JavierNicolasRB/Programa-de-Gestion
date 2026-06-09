package aventuras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Controlador implements ActionListener {
	private final Vista vista;
	private final Modelo modelo;
	private String seccionActiva = "Monitores"; 
	private String usuarioActual = "Invitado"; // Variable necesaria para el log

	private boolean modoEdicion = false;
	private int idFkEditando1 = -1; 
	private int idFkEditando2 = -1; 

	public Controlador(Vista vista, Modelo modelo) {
		this.vista = vista;
		this.modelo = modelo;

		this.vista.btnEntrar.addActionListener(this);
		this.vista.btnLimpiar.addActionListener(this);
		this.vista.btnTabMonitores.addActionListener(this);
		this.vista.btnTabAventureros.addActionListener(this);
		this.vista.btnTabActividades.addActionListener(this);
		this.vista.btnTabParticipan.addActionListener(this);
		this.vista.btnTabAyuda.addActionListener(this); 
		this.vista.btnTabSalir.addActionListener(this);
		this.vista.btnAlta.addActionListener(this);
		this.vista.btnBaja.addActionListener(this);
		this.vista.btnModificacion.addActionListener(this);
		this.vista.btnConsulta.addActionListener(this);
		this.vista.btnGuardarMonitor.addActionListener(this);
		this.vista.btnGuardarAventurero.addActionListener(this);
		this.vista.btnGuardarActividad.addActionListener(this);
		this.vista.btnGuardarParticipacion.addActionListener(this);
		this.vista.btnConfirmarBaja.addActionListener(this);
		this.vista.btnConfirmarMod.addActionListener(this); 
		this.vista.btnExportarPDF.addActionListener(this);
	}

	public void openHelp(String nombreFichero) {
		try {
			java.io.File file = new java.io.File(nombreFichero);
			String rutaAbsoluta = file.getAbsolutePath();
			ProcessBuilder pb = new ProcessBuilder("hh.exe", rutaAbsoluta);
			pb.start();
		} catch (IOException e) {
			vista.mostrarAviso("No se pudo abrir el archivo de ayuda.");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == vista.btnEntrar) {
			String user = vista.txtUsuario.getText().trim();
			String pass = vista.txtClave.getText().trim();
			String tipoUsuario = modelo.validarLogin(user, pass);

			if (tipoUsuario != null) {
				usuarioActual = user; // Guardamos el usuario al loguear
				Logger.log(usuarioActual, "Acceso al Sistema");
				vista.prepararLayoutPrincipal();
				seccionActiva = "Monitores";
				vista.resaltarTab(vista.btnTabMonitores);

				if (tipoUsuario.equalsIgnoreCase("administrador")) {
					vista.setMenuAdmin(); 
					vista.mostrarAviso("¡Bienvenido!");
				} else if (tipoUsuario.equalsIgnoreCase("basico")) {
					vista.setMenuBasico(); 
				}
			} else { 
				vista.mostrarAviso("Acceso denegado."); 
			}
		}

		else if (src == vista.btnExportarPDF) {
			modelo.exportToPDF(vista.lstTablaConsulta);
		}
		else if (src == vista.btnLimpiar) {
			vista.txtUsuario.setText(""); vista.txtClave.setText(""); vista.txtUsuario.requestFocus();
		}
		
		else if (src == vista.btnTabAyuda) { 
			vista.resaltarTab(vista.btnTabAyuda);
			openHelp("ayuda.chm"); 
		}

		else if (src == vista.btnTabMonitores) { seccionActiva = "Monitores"; vista.resaltarTab(vista.btnTabMonitores); }
		else if (src == vista.btnTabAventureros) { seccionActiva = "Aventureros"; vista.resaltarTab(vista.btnTabAventureros); }
		else if (src == vista.btnTabActividades) { seccionActiva = "Actividades"; vista.resaltarTab(vista.btnTabActividades); }
		else if (src == vista.btnTabParticipan) { seccionActiva = "Participaciones"; vista.resaltarTab(vista.btnTabParticipan); }
		else if (src == vista.btnTabSalir) { 
			Logger.log(usuarioActual, "Salida del Sistema");
			vista.txtUsuario.setText(""); vista.txtClave.setText(""); vista.mostrarLogin(); 
		}

		else if (src == vista.btnConsulta) {
			vista.lstTablaConsulta.removeAll();
			ArrayList<String> datos = new ArrayList<>();
			if (seccionActiva.equals("Monitores")) { vista.lblTituloConsulta.setText("Consulta de Monitores"); datos = modelo.consultarMonitores(); }
			else if (seccionActiva.equals("Aventureros")) { vista.lblTituloConsulta.setText("Consulta de Aventureros"); datos = modelo.consultarAventureros(); }
			else if (seccionActiva.equals("Actividades")) { vista.lblTituloConsulta.setText("Consulta de Actividades"); datos = modelo.consultarActividades(); }
			else if (seccionActiva.equals("Participaciones")) { vista.lblTituloConsulta.setText("Consulta de Participaciones"); datos = modelo.consultarParticipaciones(); }

			for (String linea : datos) { vista.lstTablaConsulta.add(linea); }
			vista.dlgConsulta.setLocationRelativeTo(vista.ventana); vista.dlgConsulta.setVisible(true);
		}

		
		    
		else if (src == vista.btnAlta) {
			modoEdicion = false;
			if (seccionActiva.equals("Monitores")) {
				vista.btnGuardarMonitor.setLabel("Confirmar Registro");
				vista.txtMonNombre.setText(""); vista.txtMonApellidos.setText(""); vista.txtMonEmail.setText(""); vista.txtMonSalary.setText("");
				vista.dlgFormMonitor.setLocationRelativeTo(vista.ventana); vista.dlgFormMonitor.setVisible(true);
			} 
			else if (seccionActiva.equals("Aventureros")) {
				vista.btnGuardarAventurero.setLabel("log Aventurero");
				vista.txtAveNombre.setText(""); vista.txtAveApellidos.setText(""); vista.txtAveEmail.setText(""); vista.txtAveTelefono.setText("");
				vista.dlgFormAventurero.setLocationRelativeTo(vista.ventana); vista.dlgFormAventurero.setVisible(true);
			}
			else if (seccionActiva.equals("Actividades")) {
				vista.btnGuardarActividad.setLabel("log Actividad");
				vista.txtActNombre.setText(""); vista.txtActPrecio.setText(""); vista.txtActDuration.setText("");
				vista.choMonitoresActDlg.removeAll(); vista.choMonitoresActDlg.add("Seleccionar Monitor...");
				for (String m : modelo.obtenerMonitores()) vista.choMonitoresActDlg.add(m);
				vista.dlgFormActividad.setLocationRelativeTo(vista.ventana); vista.dlgFormActividad.setVisible(true);
			}
			else if (seccionActiva.equals("Participaciones")) {
				vista.btnGuardarParticipacion.setLabel("Confirmar");
				vista.choAventurerosDlg.removeAll(); vista.choAventurerosDlg.add("Elegir Aventurero...");
				for (String av : modelo.obtenerAventureros()) vista.choAventurerosDlg.add(av);
				vista.choActividadesDlg.removeAll(); vista.choActividadesDlg.add("Elegir Actividad...");
				for (String ac : modelo.obtenerActividades()) vista.choActividadesDlg.add(ac);
				vista.txtHoraDlg.setText("");
				vista.dlgFormPart.setLocationRelativeTo(vista.ventana); vista.dlgFormPart.setVisible(true);
			}
		}

		else if (src == vista.btnModificacion) {
			vista.choSeleccionGeneral.removeAll();
			vista.choSeleccionGeneral.add("Seleccione elemento a MODIFICAR...");
			vista.btnConfirmarBaja.setVisible(false); 
			vista.btnConfirmarMod.setVisible(true);   

			if (seccionActiva.equals("Participaciones")) { for (String p : modelo.obtenerParticipaciones()) vista.choSeleccionGeneral.add(p); }
			else if (seccionActiva.equals("Aventureros")) { for (String a : modelo.obtenerAventureros()) vista.choSeleccionGeneral.add(a); }
			else if (seccionActiva.equals("Actividades")) { for (String ac : modelo.obtenerActividades()) vista.choSeleccionGeneral.add(ac); }
			else if (seccionActiva.equals("Monitores")) { for (String m : modelo.obtenerMonitores()) vista.choSeleccionGeneral.add(m); }

			vista.dlgSeleccion.setLocationRelativeTo(vista.ventana); vista.dlgSeleccion.setVisible(true);
		}

		else if (src == vista.btnConfirmarMod) {
			if (vista.choSeleccionGeneral.getSelectedIndex() > 0) {
				String item = vista.choSeleccionGeneral.getSelectedItem();
				modoEdicion = true;
				vista.dlgSeleccion.setVisible(false);

				if (seccionActiva.equals("Monitores")) {
					idFkEditando1 = Integer.parseInt(item.split(" - ")[0]);
					String[] datos = modelo.buscarMonitorPorId(idFkEditando1);
					if(datos != null) {
						vista.txtMonNombre.setText(datos[0]); vista.txtMonApellidos.setText(datos[1]); vista.txtMonEmail.setText(datos[2]); vista.txtMonSalary.setText(datos[3]);
						vista.btnGuardarMonitor.setLabel("Guardar Cambios");
						vista.dlgFormMonitor.setLocationRelativeTo(vista.ventana); vista.dlgFormMonitor.setVisible(true);
					}
				} 
				else if (seccionActiva.equals("Aventureros")) {
					idFkEditando1 = Integer.parseInt(item.split(" - ")[0]);
					String[] datos = modelo.buscarAventureroPorId(idFkEditando1);
					if(datos != null) {
						vista.txtAveNombre.setText(datos[0]); vista.txtAveApellidos.setText(datos[1]); vista.txtAveEmail.setText(datos[2]); vista.txtAveTelefono.setText(datos[3]);
						vista.btnGuardarAventurero.setLabel("Guardar Cambios");
						vista.dlgFormAventurero.setLocationRelativeTo(vista.ventana); vista.dlgFormAventurero.setVisible(true);
					}
				}
				else if (seccionActiva.equals("Actividades")) {
					idFkEditando1 = Integer.parseInt(item.split(" - ")[0]);
					String[] datos = modelo.buscarActividadPorId(idFkEditando1);
					if(datos != null) {
						vista.txtActNombre.setText(datos[0]); vista.txtActPrecio.setText(datos[1]); vista.txtActDuration.setText(datos[2]);
						vista.choMonitoresActDlg.removeAll(); vista.choMonitoresActDlg.add("Seleccionar Monitor...");
						for (String m : modelo.obtenerMonitores()) vista.choMonitoresActDlg.add(m);

						for(int i=0; i<vista.choMonitoresActDlg.getItemCount(); i++) {
							if(vista.choMonitoresActDlg.getItem(i).startsWith(datos[3] + " -")) { vista.choMonitoresActDlg.select(i); break; }
						}
						vista.btnGuardarActividad.setLabel("Guardar Cambios");
						vista.dlgFormActividad.setLocationRelativeTo(vista.ventana); vista.dlgFormActividad.setVisible(true);
					}
				}
				else if (seccionActiva.equals("Participaciones")) {
					String part = item.split(" \\| ")[0];
					idFkEditando1 = Integer.parseInt(part.split("-")[0]); 
					idFkEditando2 = Integer.parseInt(part.split("-")[1]); 

					vista.choAventurerosDlg.removeAll(); vista.choAventurerosDlg.add("Elegir Aventurero...");
					for (String av : modelo.obtenerAventureros()) vista.choAventurerosDlg.add(av);
					for(int i=0; i<vista.choAventurerosDlg.getItemCount(); i++) {
						if(vista.choAventurerosDlg.getItem(i).startsWith(idFkEditando1 + " -")) { vista.choAventurerosDlg.select(i); break; }
					}

					vista.choActividadesDlg.removeAll(); vista.choActividadesDlg.add("Elegir Actividad...");
					for (String ac : modelo.obtenerActividades()) vista.choActividadesDlg.add(ac);
					for(int i=0; i<vista.choActividadesDlg.getItemCount(); i++) {
						if(vista.choActividadesDlg.getItem(i).startsWith(idFkEditando2 + " -")) { vista.choActividadesDlg.select(i); break; }
					}

					vista.txtHoraDlg.setText(modelo.buscarHoraParticipacion(idFkEditando1, idFkEditando2));
					vista.btnGuardarParticipacion.setLabel("Guardar Cambios");
					vista.dlgFormPart.setLocationRelativeTo(vista.ventana); vista.dlgFormPart.setVisible(true);
				}
			}
		}

		else if (src == vista.btnBaja) {
			vista.choSeleccionGeneral.removeAll();
			vista.choSeleccionGeneral.add("Seleccione elemento a dar de BAJA...");
			vista.btnConfirmarMod.setVisible(false); 
			vista.btnConfirmarBaja.setVisible(true);  

			if (seccionActiva.equals("Participaciones")) { for (String p : modelo.obtenerParticipaciones()) vista.choSeleccionGeneral.add(p); }
			else if (seccionActiva.equals("Aventureros")) { for (String a : modelo.obtenerAventureros()) vista.choSeleccionGeneral.add(a); }
			else if (seccionActiva.equals("Actividades")) { for (String ac : modelo.obtenerActividades()) vista.choSeleccionGeneral.add(ac); }
			else if (seccionActiva.equals("Monitores")) { for (String m : modelo.obtenerMonitores()) vista.choSeleccionGeneral.add(m); }

			vista.dlgSeleccion.setLocationRelativeTo(vista.ventana); vista.dlgSeleccion.setVisible(true);
		}

		else if (src == vista.btnConfirmarBaja) {
			if (vista.choSeleccionGeneral.getSelectedIndex() > 0) {
				String item = vista.choSeleccionGeneral.getSelectedItem();
				if (seccionActiva.equals("Participaciones")) {
					String part = item.split(" \\| ")[0];
					int idAve = Integer.parseInt(part.split("-")[0]); int idAct = Integer.parseInt(part.split("-")[1]);
					if (modelo.bajaParticipacion(idAve, idAct)) {
						Logger.log(usuarioActual, "Baja: Participacion " + idAve + "-" + idAct);
						vista.mostrarAviso("Baja completada.");
					}
				} else {
					int id = Integer.parseInt(item.split(" - ")[0]);
					boolean ok = false;
					if (seccionActiva.equals("Aventureros")) ok = modelo.bajaAventurero(id);
					else if (seccionActiva.equals("Actividades")) ok = modelo.bajaActividad(id);
					else if (seccionActiva.equals("Monitores")) ok = modelo.bajaMonitor(id);
					if (ok) {
						Logger.log(usuarioActual, "Baja: " + item);
						vista.mostrarAviso("Eliminado con éxito."); 
					} else vista.mostrarAviso("No se pudo eliminar.");
				}
				vista.dlgSeleccion.setVisible(false);
			}
		}

		else if (src == vista.btnGuardarMonitor) {
			try {
				String nom = vista.txtMonNombre.getText().trim(); String ape = vista.txtMonApellidos.getText().trim(); String em  = vista.txtMonEmail.getText().trim();
				double sal = Double.parseDouble(vista.txtMonSalary.getText().trim());
				boolean exito = modoEdicion ? modelo.modificarMonitor(idFkEditando1, nom, ape, em, sal) : modelo.altaMonitor(nom, ape, em, sal);
				if (exito) { 
					Logger.log(usuarioActual, (modoEdicion ? "Modificacion" : "Alta") + ": Monitor " + nom);
					vista.dlgFormMonitor.setVisible(false); vista.mostrarAviso("Datos guardados."); 
				}
				else { vista.mostrarAviso("Error en base de datos."); }
			} catch (Exception ex) { vista.mostrarAviso("Salario no válido."); }
		}

		else if (src == vista.btnGuardarAventurero) {
			String nom = vista.txtAveNombre.getText().trim(); String ape = vista.txtAveApellidos.getText().trim(); String em  = vista.txtAveEmail.getText().trim(); String tel = vista.txtAveTelefono.getText().trim();
			boolean exito = modoEdicion ? modelo.modificarAventurero(idFkEditando1, nom, ape, em, tel) : modelo.altaAventurero(nom, ape, em, tel);
			if (exito) { 
				Logger.log(usuarioActual, (modoEdicion ? "Modificacion" : "Alta") + ": Aventurero " + nom);
				vista.dlgFormAventurero.setVisible(false); vista.mostrarAviso("Aventurero guardado."); 
			}
			else { vista.mostrarAviso("Error al guardar."); }
		}

		else if (src == vista.btnGuardarActividad) {
			try {
				String nom = vista.txtActNombre.getText().trim(); 
				double pre = Double.parseDouble(vista.txtActPrecio.getText().trim()); 
				double dur = Double.parseDouble(vista.txtActDuration.getText().trim()); 
				int idMon = Integer.parseInt(vista.choMonitoresActDlg.getSelectedItem().split(" - ")[0]);
				boolean exito = modoEdicion ? modelo.modificarActividad(idFkEditando1, nom, pre, dur, idMon) : modelo.altaActividad(nom, pre, dur, idMon);
				if (exito) { 
					Logger.log(usuarioActual, (modoEdicion ? "Modificacion" : "Alta") + ": Actividad " + nom);
					vista.dlgFormActividad.setVisible(false); vista.mostrarAviso("Actividad guardada."); 
				}
				else { vista.mostrarAviso("Error."); }
			} catch (Exception ex) { vista.mostrarAviso("Formatos numéricos erróneos."); }
		}

		else if (src == vista.btnGuardarParticipacion) {
			if (vista.choAventurerosDlg.getSelectedIndex() > 0 && vista.choActividadesDlg.getSelectedIndex() > 0) {
				int idAveNuevo = Integer.parseInt(vista.choAventurerosDlg.getSelectedItem().split(" - ")[0]);
				int idActNuevo = Integer.parseInt(vista.choActividadesDlg.getSelectedItem().split(" - ")[0]);
				String h  = vista.txtHoraDlg.getText().trim();
				boolean exito = modoEdicion ? modelo.modificarParticipacion(idFkEditando1, idFkEditando2, idAveNuevo, idActNuevo, h) : modelo.altaParticipacion(idAveNuevo, idActNuevo, h);
				if (exito) { 
					Logger.log(usuarioActual, (modoEdicion ? "Modificacion" : "Alta") + ": Participacion");
					vista.dlgFormPart.setVisible(false); vista.mostrarAviso("Participación procesada."); 
				}
				else { vista.mostrarAviso("Error en la operación."); }
			} else { vista.mostrarAviso("Seleccione elementos correctos."); }
		}
	}
}