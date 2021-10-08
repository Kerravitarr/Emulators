package MyMatch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import javax.swing.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;

/*import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;*/

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import java.util.concurrent.locks.LockSupport;



public abstract class EmusDeff extends javax.swing.JFrame {

	protected abstract class Controller {
		protected long val;
		protected Mode mode;
		protected int seed;
		protected String Name;

		/**
		 * Описывает любой контроллер устройства
		 * @param DelSeed То, в каких пределах может изменяться сид - начальное зерно генерации
		 * @param name - Имя датчика
		 */
		public Controller(int DelSeed, String name) {
			val = 0;
			mode = Mode.STEP;
			seed = (int) (Math.random() * DelSeed);// - DelSeed / 2;
			Name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return Name;
		}

		protected abstract String getStrToBoard();

		final public long getValue() {
			return val;
		}

		final public void setMode(Mode mode) {
			this.mode = mode;
			if (this.mode == Mode.RESTART) {
				this.mode = Mode.STEP;
				val = 0;
			}
		}

		final public void setValue(int value) {
			if (value == -1 || value == 999_999) {
				mode = Mode.UNDEFENDED;
			} else if (value == -2) {
				mode = Mode.RND;
			} else if (value == -3) {
				mode = Mode.STEP;
			} else {
				mode = Mode.CONST;
			}
			val = value;
		}

		public void step() {
			if (mode == Mode.RND) {
				val = (int) (Math.random() * Integer.MAX_VALUE);
			} else if (mode == Mode.STEP) {
				val += seed;
			} else if (mode == Mode.NULL) {
				val = 0;
			}
		}
	}

	protected static enum Mode {
		RND, STEP, UNDEFENDED, NULL, RESTART, CONST
	}
	
	
	protected class FastList extends JList<String>{
	    private static DefaultListModel<String> items = new DefaultListModel<String>();
	    public FastList() {
	    	super(items);
	    }
	    
	    public void add(String val) {
	    	items.addElement(val);
	    }
	    
	    public void update(String val, int index) {
			try {
				items.set(index, val);
			} catch (ArrayIndexOutOfBoundsException e) {
				items.add(index, val);
			}
	    }
	    
	    public String[] getItems() {
	    	String[] myArray = new String[items.getSize()];
			for (int i = 0; i < items.getSize(); i++) {
				myArray[i] = String.valueOf(items.getElementAt(i));
			}
	        return myArray;
	    }
	    
	    public void regenerate(int maxIndex) {
	    	while(items.getSize() > maxIndex)
	    		items.remove(items.getSize()-1);
	    }
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static SerialPort serialPortMinor;

	private ArrayList<EmusListener> listeners = new ArrayList<>();

	protected String portName;

	protected javax.swing.JMenu Menu;
	private javax.swing.JMenu MenuPort;

	protected javax.swing.JMenuBar MenuRaw;
	/** Непосредственно область, в которой происходит отображение всех устрйоств */
	protected FastList TextList;

	protected Setings Se;
	private String Name;

	private boolean isStop = false;

	private int Period;
	private boolean isBlok = false;

	protected int numKontr = 0;
	protected int startContr = 1;
	/** Число контроллеров в одном устройстве */
	protected int numContrInDev = 1;
	private int contrPerLine = 1;

	private javax.swing.JMenuItem setModeContrSetZeroAll;
	private javax.swing.JMenuItem setModeContrSetRandomAll;
	private javax.swing.JMenuItem setModeContrSetStepByStep;
	private javax.swing.JMenuItem setModeContrSetErr;
	private javax.swing.JMenuItem setModeContrSetReset;
	private javax.swing.JMenuItem setModeContrSetZero;
	private javax.swing.JMenuItem setModeContrSetConst;
	private javax.swing.JMenuItem setCountDev;
	private javax.swing.JMenuItem setStartDev;
	private javax.swing.JMenu menuForAll;
	/**Вектор всех девайсов. Каждый девайс - строка его контроллеров*/
	protected Vector<Controller[]> deviseVector = new Vector<>();

	/**
	 * Creates new
	 */

	public EmusDeff(Setings aThis, String Name, int period) {
		// this();
		Se = aThis;
		this.Name = Name;
		Period = period;
		initComponents();

		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				println("Запущен эмулятор " + Name);

				setTitle(Name);

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
					portName = Se.getString("LastPort");
				} catch (EnumConstantNotPresentException e) {
					if (portNames2.length != 0) {
						portName = portNames2[portNames2.length - 1];
					} else {
						portName = portNames1[portNames1.length - 1];
					}
				}

				if (portNames1.length + portNames2.length != 0) {

					for (String element : portNames1) {
						JRadioButtonMenuItem JG = new JRadioButtonMenuItem(
								element, element.equals(portName));
						JG.addActionListener(evt -> JG(evt));
						MenuPort.add(JG);
					}
					for (String element : portNames2) {
						JRadioButtonMenuItem JG = new JRadioButtonMenuItem(
								element, element.equals(portName));
						JG.addActionListener(evt -> JG(evt));
						MenuPort.add(JG);
					}
					println("Эмулятор подключён к " + portName);
				} else {
					portName = "";
					println("Эмулятор не смог подключится ни к чему, потому что нет ни одного порта");
				}
				init();
			}

		}, 10);
	}
	/**
	 * Добавляет контроллеры на лист
	 * @param numDev - число девайсов, состоящих из контроллеров
	 * @param startContr - адрес первого контроллера
	 * @param contrPerDev - Сколько контроллеров содержится в одном девайсе (у них один адрес)
	 * @param contrPerLine - Сколько контроллеров расположить на линии
	 */
	final protected void addContr(int numDev, int startContr, int contrPerDev,
			int contrPerLine) {
		numKontr = numDev;
		numContrInDev = contrPerDev;
		this.contrPerLine = contrPerLine;
		this.startContr = startContr;
	}

	final public void addListener(EmusListener listener) {
		listeners.add(listener);
	}

	/**
	 * Отправить событие в поток
	 *
	 * @param e
	 *            - событие
	 */
	final protected void dispatchEvent(EmusEvent e) {
		for (EmusListener listener : listeners) {
			listener.onServerChanged(e);
		}
	}

	protected abstract void firstStep();

	final public void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
		isStop = true;
		try {
			serialPortMinor.closePort();
		} catch (Exception ex) {
			print("Ошибка закрытия порта" + ex.toString());
		}
	}
	
	//Именно тут выставляется таймаут компорта
	private class BufferIn implements ActionListener{
		Timer timer = new Timer(15, this);
		@Override
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			if(lenght == 0) return;
			
			String data = "";
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0 ; i < lenght ; i++) {
				data += (char) msg[i];
				sb.append(String.format("%02X ", msg[i]));
			}
			
			lenght = 0;
			
			dispatchEvent(
					new EmusEvent(data, EmusEvent.Type.COM_IN_S));
			dispatchEvent(new EmusEvent(sb.toString(),
					EmusEvent.Type.COM_IN_X));
			msgIn(data, sb.toString());
		}
		public void add(byte[] bytes) {
			if(!timer.isRunning()) LockSupport.parkNanos(1000);
			timer.restart();
			
			if (msg.length <= lenght + bytes.length) {
				byte localM[] = new byte[lenght + bytes.length + 10];
				System.arraycopy(msg, 0, localM, 0, msg.length);
				msg = localM;
			}
			
			for(int i = 0 ; i < bytes.length; i++) {
				msg[lenght++] = bytes[i];
			}
		}
		
		byte[] msg = new byte[0];
		int lenght = 0;
		
	}
	BufferIn buffer = new BufferIn();
	
	final private void init() {
		serialPortMinor = SerialPort.getCommPort(portName);//new SerialPort(portName);
		try {
			// Открываем порт
			serialPortMinor.openPort();
			serialPortMinor.addDataListener(new SerialPortDataListener() {
				@Override
				public int getListeningEvents() {return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;}

				@Override
				public void serialEvent(SerialPortEvent event) {
					if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
				         return;
					byte[] newData = new byte[serialPortMinor.bytesAvailable()];
					serialPortMinor.readBytes(newData, newData.length);
					buffer.add(newData);
				}
			});
		} catch (Exception ex) {
			println("Возникла ошибка открытия порта, критично -> " + ex.toString());
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	final private void initComponents() {

		TextList = new FastList();
		TextList.setAutoscrolls(true);
		MenuRaw = new javax.swing.JMenuBar();
		Menu = new javax.swing.JMenu();
		MenuPort = new javax.swing.JMenu();
		javax.swing.JMenuItem jMenuItem3 = new javax.swing.JMenuItem();
		setModeContrSetZeroAll = new javax.swing.JMenuItem();
		menuForAll = new javax.swing.JMenu();
		setModeContrSetRandomAll = new javax.swing.JMenuItem();
		setModeContrSetStepByStep = new javax.swing.JMenuItem();
		setModeContrSetErr = new javax.swing.JMenuItem();
		setModeContrSetReset = new javax.swing.JMenuItem();
		setModeContrSetZero = new javax.swing.JMenuItem();
		setModeContrSetConst = new javax.swing.JMenuItem();
		setCountDev = new javax.swing.JMenuItem();
		setStartDev = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		Menu.setText("Меню");

		MenuPort.setText("Порт");

		jMenuItem3.setText("Добавить порт");
		jMenuItem3.addActionListener(evt -> jMenuItem3ActionPerformed(evt));
		MenuPort.add(jMenuItem3);

		MenuRaw.add(Menu);
		MenuRaw.add(MenuPort);

		setModeContrSetZeroAll.setText("Устновить значение");
		setModeContrSetZeroAll.addActionListener(
				evt -> jMenusetModeContrSetZeroAllActionPerformed(evt));
		Menu.add(setModeContrSetZeroAll);

		menuForAll.setText("Для всех");

		setModeContrSetRandomAll.setText("Случайные");
		setModeContrSetRandomAll.addActionListener(
				evt -> jMenusetModeContrPerformed(evt, Mode.RND));
		menuForAll.add(setModeContrSetRandomAll);

		setModeContrSetStepByStep.setText("По шагам");
		setModeContrSetStepByStep.addActionListener(
				evt -> jMenusetModeContrPerformed(evt, Mode.STEP));
		menuForAll.add(setModeContrSetStepByStep);

		setModeContrSetErr.setText("Отказ");
		setModeContrSetErr.addActionListener(
				evt -> jMenusetModeContrPerformed(evt, Mode.UNDEFENDED));
		menuForAll.add(setModeContrSetErr);

		setModeContrSetReset.setText("Рестарт");
		setModeContrSetReset.addActionListener(
				evt -> jMenusetModeContrPerformed(evt, Mode.RESTART));
		menuForAll.add(setModeContrSetReset);

		setModeContrSetZero.setText("Установить нули");
		setModeContrSetZero.addActionListener(
				evt -> jMenusetModeContrPerformed(evt, Mode.NULL));
		menuForAll.add(setModeContrSetZero);

		setModeContrSetConst.setText("Зафиксировать все");
		setModeContrSetConst.addActionListener(
				evt -> jMenusetModeContrPerformed(evt, Mode.CONST));
		menuForAll.add(setModeContrSetConst);

		Menu.add(menuForAll);

		setCountDev.setText("Число устройств");
		setCountDev.addActionListener(evt -> jMenuCountDevPerformed(evt));
		Menu.add(setCountDev);

		setStartDev.setText("Адрес первого устойства");
		setStartDev.addActionListener(evt -> jMenuAdrFirstContdPerformed(evt));
		Menu.add(setStartDev);

		setJMenuBar(MenuRaw);

		newInitComponents();
/*
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(TextList, javax.swing.GroupLayout.DEFAULT_SIZE,
						791, Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(TextList, javax.swing.GroupLayout.DEFAULT_SIZE,
						168, Short.MAX_VALUE));*/

		add(new JScrollPane(TextList));
		pack();
	}// </editor-fold>//GEN-END:initComponents

	private final void JG(java.awt.event.ActionEvent evt) {
		portName = evt.getActionCommand();
		Se.set("LastPort", portName);
		println("Эмулятор переподключён к " + portName);
		for (Component i : MenuPort.getMenuComponents()) {
			if (i instanceof JRadioButtonMenuItem) {
				if (((JRadioButtonMenuItem) i).getText().equals(portName)) {
					((JRadioButtonMenuItem) i).setSelected(true);
				} else {
					((JRadioButtonMenuItem) i).setSelected(false);
				}
			}
		}
		try {
			serialPortMinor.closePort();
		} catch (Exception ex) {
			println("Возникла ошибка закрытия порта, не критично -> " + ex.toString());
		}
		init();
	}

	private void jMenuAdrFirstContdPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem2ActionPerformed
		String res = JOptionPane.showInputDialog(this,
				"Введите адрес первого устройства");
		if (res == null) {
			return;
		}

		startContr = Integer.parseInt(res);
	}// GEN-LAST:event_jMenuItem2ActionPerformed

	private void jMenuCountDevPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem2ActionPerformed
		String res = JOptionPane.showInputDialog(this,
				"Введите число контроллеров у каждого драйвера");
		if (res == null) {
			return;
		}

		numKontr = Integer.parseInt(res);
		regenerateController();

	}// GEN-LAST:event_jMenuItem2ActionPerformed

	private final void jMenuItem3ActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem3ActionPerformed
		String result = JOptionPane.showInputDialog(this,
				"<html><h2>Введите название COM порта");
		JRadioButtonMenuItem JG = new JRadioButtonMenuItem(result, false);
		JG.addActionListener(evt1 -> JG(evt1));
		MenuPort.add(JG);

		ArrayList<String> PortMPAB = new ArrayList<>();
		for (Component i : MenuPort.getMenuComponents()) {
			if (i instanceof JRadioButtonMenuItem) {
				PortMPAB.add(((JRadioButtonMenuItem) i).getText());
			}
		}
		ArrayList<String> dedupped = new ArrayList<>(
				new LinkedHashSet<>(PortMPAB));

		Se.set("PortList", dedupped.toArray(new String[dedupped.size()]));

	}

	private void jMenusetModeContrPerformed(ActionEvent evt, Mode mode) {
		for (int i = 0; i < numKontr; i++) {
			Controller[] dataDriv = deviseVector.get(i);
			for (Controller element : dataDriv) {
				element.setMode(mode);
			}
		}
	}

	private void jMenusetModeContrSetZeroAllActionPerformed(ActionEvent evt) {
		String[] wariant = new String[deviseVector.size()];
		for (int i = 0; i < deviseVector.size(); i++) {
			wariant[i] = "Драйвер № " + (i + startContr);
		}
		int NumChanal, NumDriver;
		String res = (String) JOptionPane.showInputDialog(null,
				"Выбор драйвера", "Номер драйвера:", JOptionPane.PLAIN_MESSAGE,
				null, wariant, wariant[0]);
		if (res == null) {
			return;
		}
		for (NumDriver = 0; NumDriver < deviseVector.size(); NumDriver++) {
			if (wariant[NumDriver].equals(res)) {
				break;
			}
		}
		Controller[] Value = deviseVector.get(NumDriver);
		if (Value.length > 1) {
			wariant = new String[Value.length];
			for (int i = 0; i < Value.length; i++) {
				wariant[i] = "Канал № " + (i + 1) + " ("
						+ (NumDriver * 8 + i + 1) + ")";
			}
			res = (String) JOptionPane.showInputDialog(null, "Выбор канала",
					"Номер канала:", JOptionPane.PLAIN_MESSAGE, null, wariant,
					wariant[0]);
			if (res == null) {
				return;
			}
			for (NumChanal = 0; NumChanal < Value.length; NumChanal++) {
				if (wariant[NumChanal].equals(res)) {
					break;
				}
			}
		} else {
			NumChanal = 0;
		}
		res = JOptionPane.showInputDialog(this,
				"Драйвер № " + (NumDriver + startContr) + ", канал № "
						+ (NumChanal + 1)
						+ ".\n Ввести целое число, от 0 до 99_999.\n"
						+ "Если ввести -1 - будет неопределённость \n"
						+ "Если ввести -2 будут случайные числа каждый раз");
		if (res == null) {
			return;
		}
		Value[NumChanal].setValue(Integer.parseInt(res));
	}

	protected abstract void msgIn(String data_S, String string_H);

	protected abstract void newInitComponents();

	protected abstract void newStep();

	public final void print(String msg) {
		dispatchEvent(new EmusEvent(msg, EmusEvent.Type.PRINT));
	}

	public final void println(String msg) {
		dispatchEvent(new EmusEvent(msg, EmusEvent.Type.PRINTLN));
	}
	public final void println() {
		println("");
	}



	/**
	 * Функцию должен реализовать каждый, уважающий себя драйвер
	 * @param numDev - Порядковый нормер девайса ((состоящего из группы контроллеров))
	 * @param numContrInDev - Порядковый номер контроллера в устройстве
	 * @return
	 */
	protected abstract Controller getController(int numDev, int numContrInDev);
	
	final private void regenerateController() {
		deviseVector.removeAllElements();
		for (int i = 0; i < numKontr; i++) {
			Controller[] a = new Controller[numContrInDev];
			for (int j = 0; j < a.length; j++) {
				a[j] = getController(i, j);
			}
			deviseVector.add(a);
		}
	}

	final private void regenerateControllerString() {
		int index = 0;
		for (int i = 0; i < numKontr; i++) {
			String num = toStrWithZero(i + startContr, 2);
			Controller[] dataDriv = deviseVector.get(i);
			String name = dataDriv[0].getName() + "_" + num + ".__";
			String _name = "";
			for (int j = 0; j < name.length() - 3; j++) {
				_name += "_";
			}
			_name += ".__";
			for (int j = 0; j < dataDriv.length; j++) {
				if (j % contrPerLine == 0 && j > 0) {
					TextList.update(_name, index++);
					name = _name;
				}
				if (dataDriv.length == 1) {
					num = "";
				} else {
					num = toStrWithZero(i * dataDriv.length + j + 1, 2);
				}
				if (j > 0) {
					name += "_";
				}
				name += num + "=" + dataDriv[j].getStrToBoard();
			}
			TextList.update(name, index++);
		}
		TextList.regenerate(index);
		repaint();
	}

	final protected void regenerateControllerValue() {
		for (int i = 0; i < numKontr; i++) {
			for (Controller a : deviseVector.get(i)) {
				a.step();
			}
		}

	}

	final public void removeListener(EmusListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible) {
			isStop = false;
			timerRun(Period);

			regenerateController();
			regenerateControllerString();
			firstStep();
		} else {
			isStop = true;
		}
	}

	private void timerRun(int period) {
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (isBlok) {
					return;
				}
				isBlok = true;
				newStep();
				regenerateControllerString();

				Dimension d = new Dimension();
				String[] list = TextList.getItems();
				int width, height;
				if (list.length == 0) {
					height = 30;
					width = 150;
				} else {
					String max = Collections.max(Arrays.asList(list),
							Comparator.comparing(s -> s.length()));
					BufferedImage img = new BufferedImage(1, 1,
							BufferedImage.TYPE_INT_ARGB);
					FontMetrics fm = img.getGraphics()
							.getFontMetrics(TextList.getFont());
					width = fm.stringWidth(max);
					height = list.length * 20;
				}
				d.height = height + 70;
				d.width = width + 30;
				
				if(d.width < 200)
					d.width = 200;
				if(d.width > java.awt.Toolkit.getDefaultToolkit().getScreenSize().width)
					d.width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
				if(d.height > java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 50)
					d.height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 50;
				setSize(d);

				if (isStop) {
					this.cancel();
				}
				isBlok = false;
			}
		}, 1000, period);
	}

	// Преобразует число в строку с определённым числом нулей
	final public String toStrWithZero(int num, int zer) {
		String out = "";
		for (int i = 0; i < zer; i++) {
			out = num % 10 + out;
			num = num / 10;
		}
		return out;
	}

	final public String toStrWithZero(String num, int zer) {
		String out = "";
		if (num.length() > zer) {
			num = num.substring(num.length() - zer);
		}
		for (int i = 0; i < zer; i++) {
			if (num.length() > i) {
				out = out + num.substring(i, i + 1);
			} else {
				out = "0" + out;
			}
		}
		return out;
	}
	final public String toHexStrWithZero(int num, int zer) {
		return toStrWithZero(Integer.toHexString(num).toUpperCase(), zer);
	}
	
	// Преобразует число в строку с определённым числом нулей
	final public String toBeautifulStr(long num) {
		String out = "";
		for (int i = 0; num > 0; i++) {
			if(i % 3 == 0 && i > 0)
				out = " " + out;
			out = num % 10 + out;
			num = num / 10;
		}
		return out;
	}
	final public String toBeautifulStr(long num, int zer) {
		String out = "";
		for (int i = 0; zer > 0; i++, zer--) {
			if(i % 3 == 0 && i > 0)
				out = " " + out;
			out = num % 10 + out;
			num = num / 10;
		}
		return out;
	}
	
	final public boolean writeBytes(byte[] buffer) {return writeBytes(buffer,true);}
	final public boolean writeBytes(byte[] buffer, boolean isPrint) {
		try {
			if (serialPortMinor.writeBytes(buffer,buffer.length) != -1) {
				String data = "";
				for (byte element : buffer) {
					data += " " + String.format("%02X ", element);
				}
				if(isPrint) println("Out -> " + data);
				return true;
			}
		} catch (Exception ex) {
			println("Возникла ошибка -> " + ex.toString());
		}
		return false;
	}

	final public boolean writeString(String string) {return writeString(string,true);}
	final public boolean writeString(String string, boolean isPrint) {
		try {
			if (isPrint)
				println("Out -> " + string);
			
			return writeBytes(string.getBytes(),isPrint);
		} catch (Exception ex) {
			println("Возникла ошибка -> " + ex.toString());
			return false;
		}
	}
	/**
	 * Проверяет устройство на существование
	 * @param adrContr - Адрес устройства
	 * @return true, если устройство существует
	 */
	final protected boolean isExist(int adrContr) {
		return startContr <= adrContr && adrContr < startContr + numKontr;
	}
	final protected Controller[] getDevise(int adrContr) {
		if (isExist(adrContr))
			return deviseVector.get(adrContr - startContr);
		else
			return null;
	}
	

	/**
	 * Преобразует строку формата HH HH HH в массив байт
	 * @param string_H - строка с данными
	 * @return - готовый массив
	 */
	final protected byte[] hexToStr(String string_H) {
		String bytes[] = string_H.split(" ");
		byte[] array_h = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			array_h[i] = (byte) Integer.parseInt(bytes[i], 16);
		return array_h;
	}
	/**
	 * Преобразует строку формата SSSSS в массив байт
	 * @param string_S - строка с данными
	 * @return - готовый массив
	 */
	final protected byte[] AsciiToStr(String string_S) {
		String bytes[] = string_S.split("");
		byte[] array_h = new byte[bytes.length / 2];
		for (int i = 0; i < bytes.length/ 2; i++)
			array_h[i] = (byte) (Integer.parseInt(bytes[i*2 + 0] + bytes[i*2 + 1], 16));
		return array_h;
	}
	
	final protected boolean validMsgMB(byte[] msg) {
		int[] crc = MyMatch.calculateCRC(msg, 0, msg.length - 2);
		if((byte)crc[0] != (byte)msg[msg.length - 2] || (byte)crc[1] != (byte)msg[msg.length - 1]) {
			println("Не совпал CRC! Пришло 0х" + toHexStrWithZero(msg[msg.length - 1], 2)
					+ " 0х" + toHexStrWithZero(msg[msg.length - 2], 2)
					+ ", а ожидалось 0х" + toHexStrWithZero(crc[1], 2)
					+ " 0х" + toHexStrWithZero(crc[0], 2));
			return false;
		}
		return true;
	}
	final protected String HexToAscii(byte array_hex[]) {
		String data = "";
		for (byte element : array_hex) {
			data += String.format("%02X", element);
		}
		return data;
	}
}


















