
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import Emus.ATIS;
import Emus.BARS;
import Emus.BISI;
import Emus.BSVU;
import Emus.EndToEnd;
import Emus.I2C;
import Emus.IMSI;
import Emus.INC;
import Emus.ISI;
import Emus.MPAB;
import Emus.MPC_MZ_F;
import Emus.ORION;
import Emus.OVEN;
import Emus.Power_Wizard;
import Emus.UKTRC;
import Emus.VRT;
import MyMatch.EmusDeff;
import MyMatch.EmusEvent;
import MyMatch.EmusListener;
import MyMatch.Setings;

/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. */
/**
 *
 * @author Terran
 */
public class MainScreen extends javax.swing.JFrame implements EmusListener {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(() -> {
			MainScreen ms = new MainScreen();
			ms.setVisible(true);
		});
	}

	private BufferedWriter					LogOut;
	private String							LogOutString	= "";
	Semaphore								sem				= new Semaphore(1);

	public final Setings					Se				= new Setings();

	// Variables declaration - do not modify
	private javax.swing.JButton				AddDelete;

	private javax.swing.JButton				DellDelete;

	private java.awt.List					ListDelete;

	private javax.swing.JMenuItem			UKTRC_Iskl;

	private javax.swing.JCheckBoxMenuItem	jCheckBoxMenuItem1;

	private javax.swing.JDialog				jDialog1;

	private javax.swing.JMenu				jMenu1;

	private javax.swing.JMenu				jMenu2;

	private javax.swing.JMenu				jMenu3;

	private javax.swing.JMenu				jMenu4;

	private javax.swing.JMenuBar			jMenuBar1;

	private javax.swing.JMenuItem			jMenuItem1;

	private javax.swing.JMenuItem			jMenuItem2;

	private javax.swing.JMenuItem			jMenuItem5;

	private javax.swing.JScrollPane			jScrollPane1;

	private javax.swing.JTextArea			jTextArea1;

	private javax.swing.JTextField			newDelete;

	private Vector<JMenuItem>				jMenuDriver		= new Vector<>();
	private Vector<EmusDeff>				driverWindow	= new Vector<>();
	private HashMap<String, EmusDeff> 		driverWindow_hash= new HashMap<>();

	// End of variables declaration
	/**
	 * Creates new form MainEA
	 */
	public MainScreen() {
		initComponents();
	}

	private void AddDeleteActionPerformed(java.awt.event.ActionEvent evt) {
		ListDelete.add(newDelete.getText());
		newDelete.setText("");
		Se.set("UKTRC_delete", ListDelete.getItems());
	}

	public void body() {
	}

	private void DellDeleteActionPerformed(java.awt.event.ActionEvent evt) {
		String del = ListDelete.getSelectedItem();
		ListDelete.remove(del);
		Se.set("UKTRC_delete", ListDelete.getItems());
	}

	private void formWindowOpened(java.awt.event.WindowEvent evt) {
		try {
			LogOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("LogOutAllProg.txt"), "UTF-8"));
		} catch (FileNotFoundException | UnsupportedEncodingException ex) {
		}
		println("Запущенн комплек программ, версия 2.0");
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jDialog1 = new javax.swing.JDialog();
		ListDelete = new java.awt.List();
		AddDelete = new javax.swing.JButton();
		DellDelete = new javax.swing.JButton();
		newDelete = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu2 = new javax.swing.JMenu();
		jMenuItem2 = new javax.swing.JMenuItem();
		jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
		jMenu1 = new javax.swing.JMenu();
		jMenu3 = new javax.swing.JMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenuItem5 = new javax.swing.JMenuItem();
		UKTRC_Iskl = new javax.swing.JMenuItem();

		for (int i = 0; i < 100; i++) { // Условных 100 кнопок
			jMenuDriver.add(new javax.swing.JMenuItem());
			driverWindow.add(null);
		}

		jMenu4 = new javax.swing.JMenu();

		jDialog1.setMinimumSize(new java.awt.Dimension(400, 300));

		AddDelete.setText("Добавить");
		AddDelete.addActionListener(evt -> AddDeleteActionPerformed(evt));

		DellDelete.setText("Удалить");
		DellDelete.addActionListener(evt -> DellDeleteActionPerformed(evt));

		javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
		jDialog1.getContentPane().setLayout(jDialog1Layout);
		jDialog1Layout.setHorizontalGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jDialog1Layout.createSequentialGroup().addContainerGap()
				.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(ListDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(jDialog1Layout.createSequentialGroup().addComponent(DellDelete)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(newDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(AddDelete)))
				.addContainerGap()));
		jDialog1Layout.setVerticalGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jDialog1Layout
						.createSequentialGroup().addContainerGap().addComponent(ListDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jDialog1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(AddDelete).addComponent(DellDelete).addComponent(newDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Моя прелесть");
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				for (EmusDeff element : driverWindow) {
					if (element != null) {
						element.formWindowClosing(null);
					}
				}
				for (EmusDeff element : driverWindow_hash.values()) {
					if (element != null) {
						element.formWindowClosing(null);
					}
				}
			}

			@Override
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		jTextArea1.setColumns(20);
		jTextArea1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
		jTextArea1.setRows(5);
		jTextArea1.setAutoscrolls(true);
		jScrollPane1.setViewportView(jTextArea1);

		jMenu2.setText("Меню");

		jMenuItem2.setText("Очистить лог");
		jMenuItem2.addActionListener(evt -> jMenuItem2ActionPerformed(evt));
		jMenu2.add(jMenuItem2);

		jCheckBoxMenuItem1.setText("Автоматическая промотка");
		jCheckBoxMenuItem1.setSelected(true);
		jCheckBoxMenuItem1.addActionListener(evt -> jCheckBoxMenuItem1ActionPerformed(evt));
		jMenu2.add(jCheckBoxMenuItem1);

		jMenuBar1.add(jMenu2);

		jMenu1.setText("Режим");

		jMenu3.setText("УКТРЦ");

		jMenuItem1.setText("Парсер лога УКТРЦ");
		jMenuItem1.addActionListener(evt -> jMenuItem1ActionPerformed(evt));
		jMenu3.add(jMenuItem1);

		jMenuItem5.setText("Выбрать файл");
		jMenuItem5.addActionListener(evt -> jMenuItem5ActionPerformed(evt));
		jMenu3.add(jMenuItem5);

		UKTRC_Iskl.setText("Список исключений");
		UKTRC_Iskl.addActionListener(evt -> UKTRC_IsklActionPerformed(evt));
		jMenu3.add(UKTRC_Iskl);

		jMenu1.add(jMenu3);

		int i = 0;

		jMenuDriver.get(i).setText("VRT");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("ОРИОН");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("МПАБ");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("ИНС");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("БАРС");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("I2C");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("БИСИ");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("ИСИ");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("МПЦ МЗ Ф");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("Эмулятор УКТРЦ");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu3.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("Эмулятор PowerWizard");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("Сквозной канал");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		jMenuDriver.get(i).setText("ИМСИ");
		jMenuDriver.get(i).addActionListener(evt -> jMenuActionPerformed(evt));
		jMenu1.add(jMenuDriver.get(i));
		i++;

		newEmulator(i++,"ОВЕН");
		newEmulator(i++,"БСВУ");
		newEmulator(i++,"АТИС?!");
		
		jMenuBar1.add(jMenu1);

		jMenu4.setText("Очистить");
		jMenu4.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jMenu4MouseClicked(evt);
			}
		});
		jMenuBar1.add(jMenu4);

		setJMenuBar(jMenuBar1);

		Se.addListener(this);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE));

		pack();
	}// </editor-fold>
	
	
	private void newEmulator(int num, String name) {
		jMenuDriver.get(num).setText(name);
		jMenuDriver.get(num).addActionListener(evt -> jMenuActionPerformed(evt,name));
		jMenu1.add(jMenuDriver.get(num));
	}

	private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
		jTextArea1.setAutoscrolls(jCheckBoxMenuItem1.isSelected());
	}

	private void jMenu4MouseClicked(java.awt.event.MouseEvent evt) {
		jMenuItem2ActionPerformed(null);
		jMenu4.setSelected(false);
	}


	private void jMenuActionPerformed(ActionEvent evt, String name) {
		java.awt.EventQueue.invokeLater(() -> {
			if (!driverWindow_hash.containsKey(name)) {
				switch (name) {
				case "АТИС?!" -> driverWindow_hash.put(name, new ATIS(Se));
				case "БСВУ" -> driverWindow_hash.put(name, new BSVU(Se));
				case "ОВЕН" -> driverWindow_hash.put(name, new OVEN(Se));
				case "ИМСИ" -> driverWindow_hash.put(name, new IMSI(Se));
				case "Сквозной канал" -> driverWindow_hash.put(name, new EndToEnd(Se));
				case "Эмулятор PowerWizard" -> driverWindow_hash.put(name, new Power_Wizard(Se));
				case "Эмулятор УКТРЦ" -> driverWindow_hash.put(name, new UKTRC(Se));
				case "МПЦ МЗ Ф" -> driverWindow_hash.put(name, new MPC_MZ_F(Se));
				case "ИСИ" -> driverWindow_hash.put(name, new ISI(Se));
				case "БИСИ" -> driverWindow_hash.put(name, new BISI(Se));
				case "I2C" -> driverWindow_hash.put(name, new I2C(Se));
				case "БАРС" -> driverWindow_hash.put(name, new BARS(Se));
				case "ИНС" -> driverWindow_hash.put(name, new INC(Se));
				case "МПАБ" -> driverWindow_hash.put(name, new MPAB(Se));
				case "ОРИОН" -> driverWindow_hash.put(name, new ORION(Se));
				case "VRT" -> driverWindow_hash.put(name, new VRT(Se));
				}
				driverWindow_hash.get(name).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				driverWindow_hash.get(name).addListener(this);
			}
			driverWindow_hash.get(name).setVisible(true);
		});
	}
	
	private void jMenuActionPerformed(java.awt.event.ActionEvent evt) {
		int num2 = -1;
		switch (evt.getActionCommand()) {
			// @formatter:off
			case "БСВУ": num2++;
			case "ОВЕН": num2++;
			case "ИМСИ": num2++;
			case "Сквозной канал": num2++;
			case "Эмулятор PowerWizard": num2++;
			case "Эмулятор УКТРЦ": num2++;
			case "МПЦ МЗ Ф": num2++;
			case "ИСИ": num2++;
			case "БИСИ": num2++;
			case "I2C": num2++;
			case "БАРС": num2++;
			case "ИНС": num2++;
			case "МПАБ": num2++;
			case "ОРИОН": num2++;
			case "VRT": num2++;
			// @formatter:on
		}
		final int num = num2;
		java.awt.EventQueue.invokeLater(() -> {
			if (driverWindow.get(num) == null) {
				switch (num) {
					case 14 -> driverWindow.add(num, new BSVU(Se));
					case 13 -> driverWindow.add(num, new OVEN(Se));
					case 12 -> driverWindow.add(num, new IMSI(Se));
					case 11 -> driverWindow.add(num, new EndToEnd(Se));
					case 10 -> driverWindow.add(num, new Power_Wizard(Se));
					case 9 -> driverWindow.add(num, new UKTRC(Se));
					case 8 -> driverWindow.add(num, new MPC_MZ_F(Se));
					case 7 -> driverWindow.add(num, new ISI(Se));
					case 6 -> driverWindow.add(num, new BISI(Se));
					case 5 -> driverWindow.add(num, new I2C(Se));
					case 4 -> driverWindow.add(num, new BARS(Se));
					case 3 -> driverWindow.add(num, new INC(Se));
					case 2 -> driverWindow.add(num, new MPAB(Se));
					case 1 -> driverWindow.add(num, new ORION(Se));
					case 0 -> driverWindow.add(num, new VRT(Se));
				}
				driverWindow.get(num).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				driverWindow.get(num).addListener(this);
			}
			driverWindow.get(num).setVisible(true);
		});
	}

	private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {

		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				parseUKTRCLog();
			}
		}, 1000);
	}

	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			LogOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("LogOutAllProg.txt"), "UTF-8"));
		} catch (FileNotFoundException | UnsupportedEncodingException ex) {
		}
		jTextArea1.setText("");
	}

	private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {
		String numFileUKTRC = "";
		try {
			numFileUKTRC = Se.getString("numFileUKTRC");
		} catch (EnumConstantNotPresentException e) {
			numFileUKTRC = "";
		}
		JFileChooser fileopen = new JFileChooser(numFileUKTRC + "");
		fileopen.setSelectedFile(new File(numFileUKTRC));
		int ret = fileopen.showDialog(null, "Открыть файл");
		if (ret == JFileChooser.APPROVE_OPTION) {
			numFileUKTRC = fileopen.getSelectedFile().getPath();
			Se.set("numFileUKTRC", numFileUKTRC);
			println("Смена файл. Теперь это " + numFileUKTRC);
		} else {
			println("Файл остался прежним. Теперь это " + numFileUKTRC);
		}
	}

	@Override
	public void onServerChanged(EmusEvent e) {
		switch (e.get_Type()) {
			case PRINT:
				print(e.getMessage());
				break;
			case PRINTLN:
				println(e.getMessage());
				break;
			default:
				break;
		}
	}

	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		try {
			sem.acquire();
			if (LogOutString.equals("")) {
				sem.release();
				return;
			}
			if (jTextArea1.getAutoscrolls()) {
				jTextArea1.append(LogOutString);
				jTextArea1.setCaretPosition(jTextArea1.getText().length());
			} else {
				int NoScrol = jTextArea1.getCaretPosition();
				jTextArea1.append(LogOutString);
				jTextArea1.setCaretPosition(NoScrol);
			}
			System.out.print(LogOutString);
			try {
				LogOut.write(LogOutString);
				LogOut.flush();
			} catch (IOException ex) {
				Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
			}
			/* if(!jTextArea1.getAutoscrolls()){
			 * jTextArea1.setCaretPosition(NoScrol);
			 * } */
			LogOutString = "";
			sem.release();
		} catch (InterruptedException ex) {
			Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void parseUKTRCLog() {
		try {
			String num = Se.getString("numFileUKTRC");
			println("parseUKTRCLog. Парсим файл УКТР," + num);
			String[] delete = Se.getStringMs("UKTRC_delete");
			String[] mainString = { "О Ш И Б К А" };
			String oldline = "";
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(num)), "CP866"))) {
				String line = reader.readLine();
				boolean flarNon;
				Date dtOld = new Date();
				Date dtNow;
				SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
				long logLenghtI = 0;
				Date logLenghtD = ft.parse(line.substring(0, 8));
				int countErr = 0;
				int countErropen = 0;
				for (; line != null;) {
					flarNon = true;
					for (String i : delete) {
						if (line.contains(i)) {
							flarNon = false;
							break;
						}
					}
					if (line.equals("")) {
						flarNon = false;
					}
					for (String i : mainString) {
						if (line.contains(i)) {
							flarNon = true;
							break;
						}
					}
					if (flarNon) {
						dtNow = ft.parse(line.substring(0, 8));
						print(ft.format(new Date(dtNow.getTime() - dtOld.getTime() - 3 * 60 * 60 * 1000 + 1)));
						println("+=" + line);
						dtOld = dtNow;
						if (line.contains("О  Ш  И  Б  К  А") || line.contains("О  Ш  Б  И  К  А")) {
							countErropen++;
						}
					}
					if (line.contains("Диагностика - ")) {
						if (logLenghtD.compareTo(ft.parse(line.substring(0, 8))) <= 0) {
							logLenghtI += ft.parse(line.substring(0, 8)).getTime() - logLenghtD.getTime();
						} else {
							logLenghtI += ft.parse(line.substring(0, 8)).getTime() + 24 * 60 * 60 * 1000 - logLenghtD.getTime();
						}
						logLenghtD = ft.parse(line.substring(0, 8));
					}
					if (line.contains("О  Ш  И  Б  К  А") || line.contains("О  Ш  Б  И  К  А")) {
						countErr++;
					}
					oldline = line;
					line = reader.readLine();
				}
				println("Последняя строка:");
				println(oldline);
				print("Длительность лога: ");
				Date date = new Date(logLenghtI - 3 * 60 * 60 * 1000 + 1);
				println(date.getDate() - 1 + " д " + new SimpleDateFormat("HH:mm:ss").format(date));
				println("Колличество ошибок, всего: " + countErr);
				println("Колличество ошибок, показанных: " + countErropen);
			} catch (FileNotFoundException e) {
			} catch (IOException | java.lang.ArrayIndexOutOfBoundsException e) {
			} catch (ParseException ex) {
				Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
			}

			println("parseUKTRCLog. Закончили разбор. Файл " + num);
		} catch (EnumConstantNotPresentException e) {
			println("Не смогли найти элемент " + e.constantName());
		} catch (Exception e) {
			println("Ошибка в  parseUKTRCLog" + e);
		}
	}

	public void print(String msg) {
		try {
			sem.acquire();
			LogOutString += msg;
			sem.release();
		} catch (InterruptedException ex) {
			Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
		}
		repaint();
	}

	public void println(String msg) {
		try {
			sem.acquire();
			LogOutString += msg + "\n";
			sem.release();
		} catch (InterruptedException ex) {
			Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
		}
		repaint();
	}

	private void UKTRC_IsklActionPerformed(java.awt.event.ActionEvent evt) {
		jDialog1.setVisible(true);
		String[] delete = Se.getStringMs("UKTRC_delete");
		ListDelete.removeAll();
		for (String i : delete) {
			ListDelete.add(i);
		}
	}
}
