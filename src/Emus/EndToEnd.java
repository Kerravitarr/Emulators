package Emus;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import MyMatch.EmusDeff;
import MyMatch.Setings;

/**
 *
 * @author Terran
 */
public class EndToEnd extends EmusDeff {
	static boolean isFirst = true;
	/**Меню выбора порта*/
	javax.swing.JMenu MenuPort2;
	/**Меню выбора скорости*/
	javax.swing.JMenu MenuSpeed;
	private static final long serialVersionUID = 1L;
	private SerialPort serialPortMinorSecond;
	String portNameSecond;
	/**Скорость работы порта*/
	int portSpeed = 9600;

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
			serialPortMinorSecond.writeBytes(array_h,array_h.length);
		} catch (Exception ex) {
			println("Возникла ошибка -> " + ex.toString());
		}
	}
	
	protected void newInitComponents() {
		if(!isFirst) return ;
		

		new java.util.Timer().schedule(new TimerTask() {
			private void addSpeed(JMenu menuSpeed, int i) {
				JRadioButtonMenuItem JG = new JRadioButtonMenuItem(Integer.toString(i), i == 9600);
				JG.addActionListener(evt -> NS(evt));
				menuSpeed.add(JG);
			}
			@Override
			public void run() {
		
			MenuPort2 = new javax.swing.JMenu();
			MenuPort2.setText("Порт2");
			MenuRaw.add(MenuPort2);
			Vector<String> ports2 = new Vector<>();
			for(SerialPort sp : SerialPort.getCommPorts()) {
				ports2.add(sp.getSystemPortName());
			}
			String[] portNames1 = ports2.toArray(new String[ports2.size()]);
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
			try {
				portSpeed = Se.getInt("portSpeed");
			} catch (EnumConstantNotPresentException e) {
				portSpeed = 9600;
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
				init();
			} else {
				portNameSecond = "";
				println("Эмулятор не смог подключится ни к чему, потому что нет ни одного порта");
			}
			
	
			MenuSpeed = new javax.swing.JMenu();
			MenuSpeed.setText("Скорость порта");
			MenuPort2.add(MenuSpeed);
			
			addSpeed(MenuSpeed,50);
			addSpeed(MenuSpeed,75);
			addSpeed(MenuSpeed,110);
			addSpeed(MenuSpeed,134);
			addSpeed(MenuSpeed,150);
			addSpeed(MenuSpeed,200);
			addSpeed(MenuSpeed,300);
			addSpeed(MenuSpeed,600);
			addSpeed(MenuSpeed,1200);
			addSpeed(MenuSpeed,1800);
			addSpeed(MenuSpeed,2400);
			addSpeed(MenuSpeed,4800);
			addSpeed(MenuSpeed,9600);
			addSpeed(MenuSpeed,19200);
			addSpeed(MenuSpeed,38400);
			addSpeed(MenuSpeed,57600);
			addSpeed(MenuSpeed,115200);
				}
	
				

		}, 1000);
	}
	private final void JG(java.awt.event.ActionEvent evt) {
		portNameSecond = evt.getActionCommand();
		Se.set("LastPortSecond", portNameSecond);
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

	private final void NS(ActionEvent evt) {
		portSpeed = Integer.parseInt(evt.getActionCommand());
		Se.set("LastPortSpeed", portSpeed);
		for (Component i : MenuSpeed.getMenuComponents()) {
			if (i instanceof JRadioButtonMenuItem) {
				boolean is_select = ((JRadioButtonMenuItem) i).getText().equals(Integer.toString(portSpeed));
				((JRadioButtonMenuItem) i).setSelected(is_select);
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
		println("Эмулятор подключется выходом к " + portNameSecond + " на скорости " + portSpeed);
		serialPortMinorSecond = SerialPort.getCommPort(portNameSecond);//new SerialPort(portName);
		try {
			// Открываем порт
			serialPortMinorSecond.openPort();
			serialPortMinorSecond.setBaudRate(portSpeed);
			//serialPortMinorSecond.setParity(SerialPort.NO_PARITY);
			serialPortMinorSecond.setNumStopBits(SerialPort.ONE_STOP_BIT);
			serialPortMinorSecond.setNumDataBits(8);
			serialPortMinorSecond.addDataListener(new SerialPortDataListener() {
				@Override
				public int getListeningEvents() {return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;}

				@Override
				public void serialEvent(SerialPortEvent event) {
					if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
				         return;
					
					
					String data = "";
					StringBuilder sb = new StringBuilder();
					byte[] newData = new byte[serialPortMinorSecond.bytesAvailable()];
					int bt = serialPortMinorSecond.readBytes(newData, newData.length);
					for (byte b : newData) {
						data += (char) b;
						sb.append(String.format("%02X ", b));
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
			});
		} catch (Exception ex) {
			println("Возникла ошибка открытия порта, критично -> " + ex.toString());
		}
	}

	@Override
	protected void newStep() {
	}
}
