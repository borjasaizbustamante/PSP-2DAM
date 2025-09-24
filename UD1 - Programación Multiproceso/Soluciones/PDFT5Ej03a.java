package solucionesUD1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Detecta si el proceso notepad se está ejecutando, y en caso afirmativo, lo
 * elimina de la ejecución (mata el proceso)
 * 
 * @author Rubén Serrano
 */
public class PDFT5Ej03a {

	public static void main(String[] args) {
		System.out.println("Process running...");

		ProcessBuilder processBuilder = null;
		Process process = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try {
			// Creamos el proceso para ejecutar el CMD
			processBuilder = new ProcessBuilder("cmd.exe");
			process = processBuilder.start();
			outputStream = process.getOutputStream();

			// Escribimos comandos en el CMD
			PrintWriter writer = new PrintWriter(outputStream, true);

			// Imprime mensaje en la consola
			writer.println("echo Terminamos el notepad.exe");

			// "Mata" al proceso notepad
			writer.println("taskkill /IM notepad.exe /F");
			writer.flush();

			// Leemos la salida del CMD
			inputStream = process.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
				}
			} catch (Exception e) {
			}
			try {
				if (null != inputStreamReader) {
					inputStreamReader.close();
				}
			} catch (Exception e) {
			}
			try {
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (Exception e) {
			}
			try {
				if (null != outputStream) {
					outputStream.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
