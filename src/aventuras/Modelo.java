package aventuras;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class Modelo {
	// ÚNICO usuario de conexión técnica a MySQL con sus 4 permisos
	private final String url = "jdbc:mysql://localhost:3306/AventurasDB";
	private final String user = "admin";
	private final String password = "1234";

	private Connection getConexion() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	// --- COMPROBACIÓN DE LOGIN CONTRA LA TABLA DE LA BD ---
	public String validarLogin(String usuario, String clave) {
		String sql = "SELECT TipoUsuario FROM Usuarios WHERE NombreUsuario = ? AND ClaveEncriptadaUsuario = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, usuario);
			ps.setString(2, clave);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// Devuelve el rol de la tabla: 'administrador' o 'basico'
					return rs.getString("TipoUsuario"); 
				}
			}
		} catch (SQLException e) { 
			System.out.println("Error Login DB: " + e.getMessage()); 
		}
		return null; // Si las credenciales no existen en la tabla, devuelve null
	}

	// =========================================================================
	// --- CONSULTAS GENERALES ---
	// =========================================================================
	public ArrayList<String> consultarMonitores() {
		ArrayList<String> lista = new ArrayList<>();
		String sql = "SELECT idMonitor, NombreMonitor, ApellidoMonitor, EmailMonitor, SalarioMonitor FROM Monitores";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				// Reducimos los márgenes de 12, 15 y 20 a espacios más ajustados (8, 10, 15)
				lista.add(String.format("%-2d | %-10s | %-12s | %-16s | %5.2f€", 
						rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDouble(5)));
			}
		} catch (SQLException e) { lista.add("Error al consultar."); }
		return lista;
	}

	public ArrayList<String> consultarAventureros() {
		ArrayList<String> lista = new ArrayList<>();
		String sql = "SELECT idAventurero, NombreAventurero, ApellidoAventurero, EmailAventurero, TelefonoAventurero FROM Aventureros";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				lista.add(String.format("%-2d | %-10s | %-12s | %-16s | %-9s", 
						rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
			}
		} catch (SQLException e) { lista.add("Error al consultar."); }
		return lista;
	}

	public ArrayList<String> consultarActividades() {
		ArrayList<String> lista = new ArrayList<>();
		String sql = "SELECT a.idActividad, a.NombreActividad, a.PrecioActividad, a.DuracionActividad, m.NombreMonitor " +
				"FROM Actividades a LEFT JOIN Monitores m ON a.idMonitorFK = m.idMonitor";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				lista.add(String.format("%-2d | %-14s | %5.2f€ | %3.1fh | M: %s", 
						rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getString(5)));
			}
		} catch (SQLException e) { lista.add("Error al consultar."); }
		return lista;
	}

	public ArrayList<String> consultarParticipaciones() {
		ArrayList<String> lista = new ArrayList<>();
		String sql = "SELECT p.idAventureroFK, p.idActividadFK, av.NombreAventurero, ac.NombreActividad, p.HoraActividad " +
				"FROM Participan p JOIN Aventureros av ON p.idAventureroFK = av.idAventurero " +
				"JOIN Actividades ac ON p.idActividadFK = ac.idActividad";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				lista.add(String.format("%d-%d | Ave: %-10s | Act: %-10s | Hor: %s", 
						rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5)));
			}
		} catch (SQLException e) { lista.add("Error al consultar."); }
		return lista;
	}

	// =========================================================================
	// --- ALTAS ---
	// =========================================================================
	public boolean altaMonitor(String nom, String ape, String em, double sal) {
		String sql = "INSERT INTO Monitores (NombreMonitor, ApellidoMonitor, EmailMonitor, SalarioMonitor) VALUES (?, ?, ?, ?)";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nom); ps.setString(2, ape); ps.setString(3, em); ps.setDouble(4, sal);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	public boolean altaAventurero(String nom, String ape, String em, String tel) {
		String sql = "INSERT INTO Aventureros (NombreAventurero, ApellidoAventurero, EmailAventurero, TelefonoAventurero) VALUES (?, ?, ?, ?)";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nom); ps.setString(2, ape); ps.setString(3, em); ps.setString(4, tel);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	public boolean altaActividad(String nom, double pre, double dur, int idMon) {
		String sql = "INSERT INTO Actividades (NombreActividad, PrecioActividad, DuracionActividad, idMonitorFK) VALUES (?, ?, ?, ?)";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nom); ps.setDouble(2, pre); ps.setDouble(3, dur); ps.setInt(4, idMon);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	public boolean altaParticipacion(int idAve, int idAct, String hora) {
		String sql = "INSERT INTO Participan (idAventureroFK, idActividadFK, HoraActividad) VALUES (?, ?, ?)";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idAve); ps.setInt(2, idAct); ps.setString(3, hora);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	// =========================================================================
	// --- BAJAS ---
	// =========================================================================
	public boolean bajaMonitor(int id) {
		String sql = "DELETE FROM Monitores WHERE idMonitor = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
		catch (SQLException e) { return false; }
	}

	public boolean bajaAventurero(int id) {
		String sql = "DELETE FROM Aventureros WHERE idAventurero = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
		catch (SQLException e) { return false; }
	}

	public boolean bajaActividad(int id) {
		String sql = "DELETE FROM Actividades WHERE idActividad = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
		catch (SQLException e) { return false; }
	}

	public boolean bajaParticipacion(int idAve, int idAct) {
		String sql = "DELETE FROM Participan WHERE idAventureroFK = ? AND idActividadFK = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) { ps.setInt(1, idAve); ps.setInt(2, idAct); return ps.executeUpdate() > 0; }
		catch (SQLException e) { return false; }
	}

	// =========================================================================
	// --- MODIFICACIONES ---
	// =========================================================================
	public boolean modificarMonitor(int id, String nom, String ape, String em, double sal) {
		String sql = "UPDATE Monitores SET NombreMonitor = ?, ApellidoMonitor = ?, EmailMonitor = ?, SalarioMonitor = ? WHERE idMonitor = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nom); ps.setString(2, ape); ps.setString(3, em); ps.setDouble(4, sal); ps.setInt(5, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	public boolean modificarAventurero(int id, String nom, String ape, String em, String tel) {
		String sql = "UPDATE Aventureros SET NombreAventurero = ?, ApellidoAventurero = ?, EmailAventurero = ?, TelefonoAventurero = ? WHERE idAventurero = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nom); ps.setString(2, ape); ps.setString(3, em); ps.setString(4, tel); ps.setInt(5, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	public boolean modificarActividad(int id, String nom, double pre, double dur, int idMon) {
		String sql = "UPDATE Actividades SET NombreActividad = ?, PrecioActividad = ?, DuracionActividad = ?, idMonitorFK = ? WHERE idActividad = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nom); ps.setDouble(2, pre); ps.setDouble(3, dur); ps.setInt(4, idMon); ps.setInt(5, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { return false; }
	}

	public boolean modificarParticipacion(int idAveViejo, int idActViejo, int idAveNuevo, int idActNuevo, String hora) {
		if (bajaParticipacion(idAveViejo, idActViejo)) { return altaParticipacion(idAveNuevo, idActNuevo, hora); }
		return false;
	}

	// =========================================================================
	// --- BÚSQUEDAS UNITARIAS ---
	// =========================================================================
	public String[] buscarMonitorPorId(int id) {
		String sql = "SELECT NombreMonitor, ApellidoMonitor, EmailMonitor, SalarioMonitor FROM Monitores WHERE idMonitor = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return new String[]{ rs.getString(1), rs.getString(2), rs.getString(3), String.valueOf(rs.getDouble(4)) };
			}
		} catch (SQLException e) { }
		return null;
	}

	public String[] buscarAventureroPorId(int id) {
		String sql = "SELECT NombreAventurero, ApellidoAventurero, EmailAventurero, TelefonoAventurero FROM Aventureros WHERE idAventurero = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return new String[]{ rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4) };
			}
		} catch (SQLException e) { }
		return null;
	}

	public String[] buscarActividadPorId(int id) {
		String sql = "SELECT NombreActividad, PrecioActividad, DuracionActividad, idMonitorFK FROM Actividades WHERE idActividad = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return new String[]{ rs.getString(1), String.valueOf(rs.getDouble(2)), String.valueOf(rs.getDouble(3)), String.valueOf(rs.getInt(4)) };
			}
		} catch (SQLException e) { }
		return null;
	}

	public String buscarHoraParticipacion(int idAve, int idAct) {
		String sql = "SELECT HoraActividad FROM Participan WHERE idAventureroFK = ? AND idActividadFK = ?";
		try (Connection con = getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idAve); ps.setInt(2, idAct);
			try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString(1); }
		} catch (SQLException e) { }
		return "";
	}

	// =========================================================================
	// --- CHOICES ---
	// =========================================================================
	public ArrayList<String> obtenerMonitores() {
		ArrayList<String> res = new ArrayList<>();
		String sql = "SELECT idMonitor, NombreMonitor, ApellidoMonitor FROM Monitores";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) res.add(rs.getInt(1) + " - " + rs.getString(2) + " " + rs.getString(3));
		} catch (SQLException e) { }
		return res;
	}

	public ArrayList<String> obtenerAventureros() {
		ArrayList<String> res = new ArrayList<>();
		String sql = "SELECT idAventurero, NombreAventurero, ApellidoAventurero FROM Aventureros";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) res.add(rs.getInt(1) + " - " + rs.getString(2) + " " + rs.getString(3));
		} catch (SQLException e) { }
		return res;
	}

	public ArrayList<String> obtenerActividades() {
		ArrayList<String> res = new ArrayList<>();
		String sql = "SELECT idActividad, NombreActividad FROM Actividades";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) res.add(rs.getInt(1) + " - " + rs.getString(2));
		} catch (SQLException e) { }
		return res;
	}

	public ArrayList<String> obtenerParticipaciones() {
		ArrayList<String> res = new ArrayList<>();
		String sql = "SELECT p.idAventureroFK, p.idActividadFK, av.NombreAventurero, ac.NombreActividad FROM Participan p " +
				"JOIN Aventureros av ON p.idAventureroFK = av.idAventurero JOIN Actividades ac ON p.idActividadFK = ac.idActividad";
		try (Connection con = getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) res.add(rs.getInt(1) + "-" + rs.getInt(2) + " | " + rs.getString(3) + " en " + rs.getString(4));
		} catch (SQLException e) { }
		return res;
	}
	
	
	/**
	 * Exporta el contenido de un TextArea a un PDF. Abre un diálogo para seleccionar la ruta.
	 *
	 * @param txtaConsulta TextArea con el contenido a exportar
	 * @return true si el PDF se ha generado correctamente, false en caso de error o cancelación
	 */
	public boolean exportToPDF(java.awt.List lstConsulta) {
	    if (lstConsulta == null || lstConsulta.getItemCount() == 0) {
	        return false;
	    }

	    String[] lines = lstConsulta.getItems();
	    
	    int firstDataIndex = 0;
	    while (firstDataIndex < lines.length && lines[firstDataIndex].trim().isEmpty()) {
	        firstDataIndex++;
	    }
	    if (firstDataIndex >= lines.length) {
	        return false;
	    }

	    // 1. Leemos la primera fila de datos para deducir qué lista estamos viendo
	    String joinedFirstRow = lines[firstDataIndex].toLowerCase();
	    String title = "Reporte";
	    java.util.List<String> headers = new ArrayList<>();

	    // 2. Asignamos el Título del PDF y las Cabeceras de las columnas según el contenido
	    if (joinedFirstRow.contains("ave:") && joinedFirstRow.contains("act:")) {
	        title = "Participaciones";
	        headers = java.util.Arrays.asList("IDs", "Aventurero", "Actividad", "Hora");
	    } else if (joinedFirstRow.contains("m:") || (joinedFirstRow.contains("h ") && joinedFirstRow.contains("€"))) {
	        title = "Actividades";
	        headers = java.util.Arrays.asList("ID", "Nombre", "Precio", "Duración", "Monitor");
	    } else if (joinedFirstRow.contains("€")) {
	        title = "Monitores";
	        headers = java.util.Arrays.asList("ID", "Nombre", "Apellidos", "Email", "Salario");
	    } else {
	        title = "Aventureros";
	        headers = java.util.Arrays.asList("ID", "Nombre", "Apellidos", "Email", "Teléfono");
	    }

	    FileDialog fd = new FileDialog((Frame) null, "Guardar PDF", FileDialog.SAVE);

	    SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
	    String now = sdf.format(new java.util.Date());

	    fd.setFile("consulta_" + title.replace(" ", "_") + "_" + now + ".pdf");
	    fd.setVisible(true);

	    String directory = fd.getDirectory();
	    String filename = fd.getFile();
	    if (directory == null || filename == null) {
	        return false;
	    }

	    String fullPath = directory + filename;
	    if (!fullPath.toLowerCase().endsWith(".pdf")) {
	        fullPath = fullPath + ".pdf";
	    }

	    try (FileOutputStream fos = new FileOutputStream(fullPath);
	         PdfWriter writer = new PdfWriter(fos);
	         PdfDocument pdfDoc = new PdfDocument(writer);
	         Document document = new Document(pdfDoc)) {

	        PdfFont font;
	        try {
	            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	        } catch (Exception ex) {
	            font = PdfFontFactory.createFont();
	        }
	        document.setFont(font);

	        document.add(new Paragraph(title).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
	        document.add(new Paragraph(" "));

	        Table table = new Table(headers.size());
	        table.setWidth(UnitValue.createPercentValue(100));
	        for (String header : headers) {
	            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
	        }

	        // 3. ATENCIÓN: Empezamos desde 'firstDataIndex' (NO +1) para no perder la primera fila de datos
	        for (int row = firstDataIndex; row < lines.length; row++) {
	            String line = lines[row].trim();
	            if (line.isEmpty()) {
	                continue;
	            }

	            java.util.List<String> columns = new ArrayList<>();
	            for (String part : lines[row].split("\\|")) {
	                String value = part.trim();
	                // 4. Limpiamos los prefijos visuales para que las celdas del PDF queden perfectas
	                value = value.replace("Ave:", "").replace("Act:", "").replace("Hor:", "").replace("M:", "").trim();
	                
	                if (!value.isEmpty()) {
	                    columns.add(value);
	                }
	            }

	            for (int col = 0; col < headers.size(); col++) {
	                String cellText = col < columns.size() ? columns.get(col) : "";
	                table.addCell(new Cell().add(new Paragraph(cellText)));
	            }
	        }

	        document.add(table);
	        
	        // --- Mensaje de éxito en AWT puro ---
	        java.awt.Dialog dialog = new java.awt.Dialog((java.awt.Frame) null, "Exportación Exitosa", true);
	        dialog.setLayout(new java.awt.BorderLayout());
	        
	        java.awt.Panel panelCentro = new java.awt.Panel(new java.awt.GridLayout(2, 1));
	        panelCentro.add(new java.awt.Label("El archivo PDF se ha guardado correctamente en:", java.awt.Label.CENTER));
	        
	        java.awt.TextField txtPath = new java.awt.TextField(fullPath);
	        txtPath.setEditable(false);
	        panelCentro.add(txtPath);
	        
	        dialog.add(panelCentro, java.awt.BorderLayout.CENTER);
	        
	        java.awt.Button okButton = new java.awt.Button("Aceptar");
	        okButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	                dialog.dispose();
	            }
	        });
	        
	        java.awt.Panel panelSur = new java.awt.Panel();
	        panelSur.add(okButton);
	        dialog.add(panelSur, java.awt.BorderLayout.SOUTH);
	        
	        dialog.setSize(450, 130);
	        dialog.setLocationRelativeTo(null);
	        dialog.setVisible(true);

	        return true;
	    } catch (IOException ex) {
	        Logger.error("System","Error exportando a PDF: " + ex.getMessage());
	        return false;
	    } catch (Exception ex) {
	        Logger.error("System","Error iText: " + ex.getMessage());
	        return false;
	    }
	}	
}

