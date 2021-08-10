package Emus;

import java.awt.Component;
import java.util.TimerTask;

import javax.swing.JRadioButtonMenuItem;

import MyMatch.EmusDeff;
import MyMatch.EmusEvent;
import MyMatch.MyMatch;
import MyMatch.Setings;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author Terran
 */
public class EndToEnd extends EmusDeff {
	static boolean isFirst = true;
	javax.swing.JMenu MenuPort2;
	private static final long serialVersionUID = 1L;
	private SerialPort serialPortMinorSecond;
	String portNameSecond;

	public EndToEnd(Setings Se) {
		super(Se, "Сквозной канал, труба с логом", 500);
		if(isFirst) {
			isFirst = false;
		}
	}

	@Override
	protected void firstStep() {
		newStep();
	}
	@Override
	protected Controller getController(int numContr, int numPart) {
		return null;
	}
	@Override
	protected void msgIn(String data_S, String string_H) {
		println("Hex " + portName + " -> " + portNameSecond + " " + string_H);


		String bytes[] = string_H.split(" ");
		byte array_h[] = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			array_h[i] = (byte) Integer.parseInt(bytes[i], 16);
		}
		try {
			serialPortMinorSecond.writeBytes(array_h);
		} catch (SerialPortException ex) {
			println("Возникла ошибка -> " + ex.toString());
		}
	}
	
	protected void newInitComponents() {
		if(!isFirst) return ;
		

		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
		
		MenuPort2 = new javax.swing.JMenu();
		MenuPort2.setText("Порт2");
		MenuRaw.add(MenuPort2);
		String[] portNames1 = SerialPortList.getPortNames();
		String[] portNames2 = {};
		try {
			portNames2 = Se.getStringMs("PortList");
		} catch (EnumConstantNotPresentException e) {
		}
		try {
			portNameSecond = Se.getString("LastPortSecond");
		} catch (EnumConstantNotPresentException e) {
			portNameSecond = "";
		}

		if (portNames1.length + portNames2.length != 0) {

			for (String element : portNames1) {
				JRadioButtonMenuItem JG = new JRadioButtonMenuItem(
						element, element.equals(portNameSecond));
				JG.addActionListener(evt -> JG(evt));
				MenuPort2.add(JG);
			}
			for (String element : portNames2) {
				JRadioButtonMenuItem JG = new JRadioButtonMenuItem(
						element, element.equals(portNameSecond));
				JG.addActionListener(evt -> JG(evt));
				MenuPort2.add(JG);
			}
			println("Эмулятор подключён выходом к " + portNameSecond);
			init();
		} else {
			portNameSecond = "";
			println("Эмулятор не смог подключится ни к чему, потому что нет ни одного порта");
		}
			}

		}, 1000);
	}
	private final void JG(java.awt.event.ActionEvent evt) {
		portNameSecond = evt.getActionCommand();
		Se.set("LastPortSecond", portNameSecond);
		println("Эмулятор переподключён выходом к " + portNameSecond);
		for (Component i : MenuPort2.getMenuComponents()) {
			if (i instanceof JRadioButtonMenuItem) {
				if (((JRadioButtonMenuItem) i).getText().equals(portNameSecond)) {
					((JRadioButtonMenuItem) i).setSelected(true);
				} else {
					((JRadioButtonMenuItem) i).setSelected(false);
				}
			}
		}
		try {
			serialPortMinorSecond.closePort();
		} catch (Exception ex) {
			println("Возникла ошибка -> " + ex.toString());
		}
		init();
	}
	final private void init() {
		serialPortMinorSecond = new SerialPort(portNameSecond);
		try {
			// Открываем порт
			serialPortMinorSecond.openPort();
			// Выставляем параметры
			serialPortMinorSecond.setParams(SerialPort.BAUDRATE_19200,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			// Устанавливаем ивент лисенер и маску
			serialPortMinorSecond.addEventListener(event -> {
				try {
					if (event.isRXCHAR() && event.getEventValue() > 0) {
						String data = "";
						StringBuilder sb = new StringBuilder();
						try {
							// Получаем ответ от устройства, обрабатываем данные
							// и т.д.
							while (true) {
								byte[] bt = serialPortMinorSecond
										.readBytes(event.getEventValue(), 15);
								for (byte b : bt) {
									data += (char) b;
									sb.append(String.format("%02X ", b));
								}
							}
						} catch (SerialPortException ex1) {
							println("упс - " + ex1.toString());
						} catch (SerialPortTimeoutException ex2) {
							// println(ex.toString());
						}
						data = sb.toString();
						println("Hex " + portNameSecond + " -> " + portName + " " + data);
						String bytes[] = data.split(" ");
						byte array_h[] = new byte[bytes.length];
						for (int i = 0; i < bytes.length; i++) {
							array_h[i] = (byte) Integer.parseInt(bytes[i], 16);
						}
						writeBytes(array_h,false);
					}
				} catch (Exception ex) {
					println("Возникла ошибка -> " + ex.toString());
				}
			}, SerialPort.MASK_RXCHAR);
		} catch (Exception ex) {
			println("Возникла ошибка -> " + ex.toString());
		}
	}

	@Override
	protected void newStep() {
	}
}
