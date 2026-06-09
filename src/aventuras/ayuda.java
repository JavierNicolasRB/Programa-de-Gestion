package aventuras;

import java.io.IOException;
public class ayuda
{
	public static void main(String[] args)
	{
		try
		{
			ProcessBuilder pb = new ProcessBuilder("hh.exe", "ayuda.chm");
			pb.start();
			System.out.println("Abriendo el archivo CHM...");
		}
		catch (IOException e)
		{
			System.err.println("Error al intentar abrir el archivo CHM: " + e.getMessage());
		}
	}
}