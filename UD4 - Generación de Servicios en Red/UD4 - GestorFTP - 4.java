package ftpClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class GestorFTP {

	private FTPClient ftpClient = null;

	private static final String SERVER = "ftp.uni-rostock.de";
	private static final int PORT = 21;
	private static final String USER = "anonymous";
	private static final String PASS = "anonymous@domain.com";

	public GestorFTP() {
		ftpClient = new FTPClient();
	}

	/**
	 * Connects to the FTP server
	 * 
	 * @throws SocketException
	 * @throws IOException
	 */
	private void conectar() throws SocketException, IOException {
		ftpClient.connect(SERVER, PORT);
		int respuesta = ftpClient.getReplyCode();

		// Trying to connect
		if (!FTPReply.isPositiveCompletion(respuesta)) {
			ftpClient.disconnect();
			throw new IOException("Error al conectar con " + SERVER + ":" + PORT);
		}

		boolean credencialesOk = ftpClient.login(USER, PASS);
		if (!credencialesOk) {
			throw new IOException("User or Pass incorrectos");
		}

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	@SuppressWarnings("unused")
	private boolean uploadFile(String path) throws IOException {
		boolean ret = false;
		File localFile = new File(path);
		InputStream intputStream = new FileInputStream(localFile);
		ret = ftpClient.storeFile(localFile.getName(), intputStream);
		intputStream.close();
		return ret;
	}

	private boolean downloadFile(String fichero, String path) throws IOException {
		boolean ret = false;
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));
		ret = ftpClient.retrieveFile(fichero, outputStream);
		outputStream.close();
		return ret;
	}

	private FTPFile[] listDirectories() throws IOException {
		// Lists files and directories
		return ftpClient.listFiles();
	}

	private void disconnect() throws IOException {
		ftpClient.disconnect();
	}

	private void printFileDetails(FTPFile[] files) {
		DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (FTPFile file : files) {
			String details = file.getName();
			if (file.isDirectory()) {
				details = "[" + details + "]";
			}
			details += "\t\t" + file.getSize();
			details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
			System.out.println(details);
		}
	}

	public static void main(String[] args) {
		GestorFTP gestorFTP = new GestorFTP();

		try {

			// Connect to FTP Server...
			gestorFTP.conectar();
			System.out.println("Conectado a " + SERVER + ":" + PORT);

			// List the contents of the remote folder
			System.out.println("Contents of the remote folder: ");
			FTPFile[] files = gestorFTP.listDirectories();
			gestorFTP.printFileDetails(files);

			// Downloading files...
			System.out.println("Descargando fichero... ");
			if (gestorFTP.downloadFile("robots.txt", "c://Trastero/robots.txt")) {
				System.out.println("Fichero decargado");
			} else {
				System.out.println("Error descargando el fichero");
			}

			gestorFTP.disconnect();
		} catch (Exception e) {
			System.out.println("Doh! " + e.getMessage());
		}
	}
}
