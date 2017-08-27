package br.edu.fema.jduino.framework;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class Serial implements SerialPortEventListener {

	private static final int TIME_OUT = 2000;

	private SerialPort serialPort = null;

	private BufferedReader input = null;
	private OutputStream output = null;

	private String portName = null;
	private int baudRate = -1;

	public Serial(String portName, int baudRate) {
		super();

		this.portName = portName;
		this.baudRate = baudRate;
	}

	public Serial initialize() {
		CommPortIdentifier port = null;

		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currentPort = (CommPortIdentifier) portEnum.nextElement();

			if (currentPort.getName().equals(this.portName)) {
				port = currentPort;
				break;
			}
		}

		if (port == null) {
			System.out.println("Could not find the Arduino port.");

			return null;
		}

		try {
			serialPort = (SerialPort) port.open(this.getClass().getName(), TIME_OUT);
			serialPort.setSerialPortParams(this.baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

			output = serialPort.getOutputStream();

			serialPort.addEventListener(this);

			serialPort.notifyOnDataAvailable(true);

			Thread.sleep(2000);

			System.out.println("Successfully connected to the Arduino port.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();

			serialPort.close();
		}
	}

	private OnReceive onReceive = null;

	public interface OnReceive {

		public void process(String value);

	};

	public void setOnReceive(OnReceive onReceive) {
		this.onReceive = onReceive;
	}

	@Override
	public void serialEvent(SerialPortEvent spe) {
		if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = null;

				if (input.ready()) {
					inputLine = input.readLine();

					if (onReceive != null) {
						onReceive.process(inputLine);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void write(String valor) {
		try {
			output.write(valor.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
