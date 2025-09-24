package solucionesUD1;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Detecta si el proceso notepad se está ejecutando, y en caso afirmativo, lo
 * elimina de la ejecución (mata el proceso)
 * 
 * @author Ibai Manso
 */
public class PDFT5Ej03b {

	private void doKillNotepad() throws Exception {

		// 1) Lanza notepad (mejor pasar la ruta completa para evitar ambigüedades)
		ProcessBuilder pb = new ProcessBuilder("C:\\Windows\\System32\\notepad.exe");
		Process p = pb.start();
		long pid = p.pid();
		System.out.println("PID Java (lanzado): " + pid);

		// espera para que la UI aparezca
		Thread.sleep(3000);

		// 2) Intento "suave" y luego forzado sobre el Process que lanzamos
		System.out.println("Intentando destroy()...");
		p.destroy();
		boolean exited = p.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
		System.out.println("Exited tras destroy(): " + exited + ", isAlive: " + p.isAlive());

		if (!exited && p.isAlive()) {
			System.out.println("Intentando destroyForcibly()...");
			p.destroyForcibly();
			// espera breve
			p.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
			System.out.println("isAlive tras destroyForcibly: " + p.isAlive());
		}

		// 3) Si la ventana sigue abierta (p.isAlive() false o la ventana visible),
		// buscamos procesos notepad.exe y matamos el más nuevo (o todos si prefieres).
		List<ProcessHandle> notepads = ProcessHandle.allProcesses()
				.filter(ph -> ph.info().command().isPresent()
						&& ph.info().command().get().toLowerCase().endsWith("notepad.exe"))
				.collect(Collectors.toList());

		if (!notepads.isEmpty()) {

			System.out.println("Instancias notepad encontradas: " + notepads.size());
			// Elegimos el más reciente (por startInstant) — suele ser el que acabamos de
			// abrir
			ProcessHandle target = notepads.stream()
					.max(Comparator.comparing(ph -> ph.info().startInstant().orElse(Instant.EPOCH))).get();

			System.out.println(
					"PID objetivo: " + target.pid() + " (command: " + target.info().command().orElse("unknown") + ")");

			// Intento de cierre suave
			target.destroy();
			Thread.sleep(1000);

			// Si sigue vivo, forzamos con destroyForcibly()
			if (target.isAlive()) {
				System.out.println("Forzando con destroyForcibly() sobre PID " + target.pid());
				target.destroyForcibly();
				Thread.sleep(1000);
			}

			// Si aún sigue vivo (problemas de permisos), uso taskkill (Windows)
			if (target.isAlive()) {
				System.out.println("Usando taskkill /F /T sobre PID " + target.pid());
				// Ejecuta: taskkill /PID <pid> /T /F
				Process taskkill = new ProcessBuilder("taskkill", "/PID", Long.toString(target.pid()), "/T", "/F")
						.inheritIO() // opcional: ver salida en consola
						.start();
				taskkill.waitFor();
				System.out.println("taskkill finalizado, exit: " + taskkill.exitValue());
			}

			System.out.println("Estado final del PID " + target.pid() + ": isAlive=" + target.isAlive());
		} else {
			System.out.println("No se han detectado procesos notepad.exe tras los intentos anteriores.");
		}
	}

	public static void main(String[] args) {
		try {
			new PDFT5Ej03b().doKillNotepad();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
