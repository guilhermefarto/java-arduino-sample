package br.edu.fema.jduino.main;

import java.util.Date;
import java.util.Scanner;
import br.edu.fema.jduino.framework.Serial;

public class Main {

	public static void main(String[] args) {

		Serial serial = new Serial("COM1", 9600).initialize();

		if (serial != null) {
			serial.setOnReceive(new Serial.OnReceive() {

				@Override
				public void process(String value) {
					System.out.println(new Date() + " > Recebido (Arduino): " + value);
				}
			});

			Scanner scanner = new Scanner(System.in);

			String command = "";

			// ACENDER;11
			// APAGAR;11
			// ACENDER;11?100
			// ACENDER
			// APAGAR

			System.out.println("Waiting for commands...");

			while (!command.equalsIgnoreCase("exit")) {
				command = scanner.nextLine();
				// command = JOptionPane.showInputDialog("Command");

				System.out.println(new Date() + " > Enviado (Java): " + command);

				serial.write(command);
			}

			serial.close();

			scanner.close();

		}
	}

}
