package ejercicios;

public class HiloDurmiente extends Thread {

	private String name = null;

	public HiloDurmiente(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Soy el bucle " + name + " y estoy trabajando");
				int delay = (int) (Math.floor(Math.random() * (10)) + 1);
				sleep(delay * 1000);
				this.interrupt();
			} catch (InterruptedException e) {
				System.out.println("Bucle " + name + " despierto!");
			}
		}
	}

	public static void main(String[] args) {

		new HiloDurmiente("One").start();
		new HiloDurmiente("Two").start();
		new HiloDurmiente("Three").start();
		new HiloDurmiente("Four").start();
		new HiloDurmiente("Five").start();
	}
}