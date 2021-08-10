package Emus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

import MyMatch.EmusDeff;
import MyMatch.EmusListener;
import MyMatch.Setings;

/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. */
/**
 *
 * @author Terran
 */
public class VRT extends EmusDeff {
	private static class Device {

		private int		Id				= -1;
		private String	Type			= "";
		private int		NumObjects		= -1;
		private int		IsBlink			= -1;
		private int		Node			= -1;
		private int		RequestForBlink	= -1;
		private int		ChangeForBlink	= -1;
		Vector<Node>	Nodes			= new Vector();
		Point			point			= new Point();

		public Device(String arg) {
			String[] args = arg.split(" ");
			for (int i = 0; i < args.length; i++) {
				switch (i) {
					case 0:
						Id = Integer.parseInt(args[i]);
						break;
					case 1:
						Type = args[i];
						break;
					case 2:
						NumObjects = Integer.parseInt(args[i]);
						break;
					case 3:
						IsBlink = Integer.parseInt(args[i]);
						break;
					case 4:
						Node = Integer.parseInt(args[i]);
						break;
					case 5:
						RequestForBlink = Integer.parseInt(args[i]);
						break;
					case 6:
						ChangeForBlink = Integer.parseInt(args[i]);
						break;
				}
			}
		}

		public void addNode(int from, int to) {
			Nodes.add(new Node(from, to));
		}

		public Color getColog() {
			return new Color((int) Math.pow(Id, 2));
		}

		public void setPoent(int x, int y) {
			point = new Point(x, y);
		}
	}

	private static class Node {

		int	from;
		int	to;

		public Node(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}

	private ArrayList<EmusListener>			listeners			= new ArrayList<>();
	boolean									isDevise			= false;
	boolean									isConnections		= false;
	Vector<Device>							DataDriverVector	= new Vector();
	int										countNode			= 0;
	Set<Integer>							Nodests;
	int										NumDev				= -1;

	int										NumNode				= -1;

	boolean									autoScroll			= false;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JCheckBoxMenuItem	jCheckBoxMenuItem1;

	private javax.swing.JCheckBoxMenuItem	jCheckBoxMenuItem2;

	private javax.swing.JCheckBoxMenuItem	jCheckBoxMenuItem3;

	private javax.swing.JMenu				jMenu1;

	private javax.swing.JMenuBar			jMenuBar1;

	private javax.swing.JPanel				jPanel1;
	// End of variables declaration//GEN-END:variables

	/**
	 * Creates new form VRT
	 */
	public VRT(Setings Se) {
		super(Se, "INC", 1000);
		initComponents();
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// update();
				if (autoScroll) {
					for (int i = 0; i < DataDriverVector.size(); i++) {
						if (DataDriverVector.get(i).Id == NumDev) {
							if (i + 1 < DataDriverVector.size()) {
								NumDev = DataDriverVector.get(i + 1).Id;
							} else {
								NumDev = DataDriverVector.get(0).Id;
							}
							break;
						}
					}
					repaint();
				}
			}
		}, 1000, 1000);
		println("VRT. Парсим файл VRT");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("VRT.txt")), "UTF-8"))) {
			String line = reader.readLine();
			for (; line != null;) {
				// Вот тут обработка
				parse(line);
				line = reader.readLine();
			}
			// Сортеруем
			Collections.sort(DataDriverVector, (o1, o2) -> (o1.Id - o2.Id));
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < countNode; i++) {
				list.add(i + 1);
			}
			Nodests = new LinkedHashSet<>(list);
			countNode = Nodests.size();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	private void drawArrow(int x1, int y1, int x2, int y2, Graphics g) {
		double angl = Math.atan2(y2 - y1, x2 - x1);
		angl = -angl;
		angl -= Math.PI / 2;

		int len = (int) Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x1 - x2, 2));
		len = (int) Math.log10(len) * 10;
		int delAngl = 10;

		g.drawLine(x1, y1, x2, y2);

		angl += Math.PI * delAngl / 180;
		x1 = x2 + (int) (Math.sin(angl) * len);
		y1 = y2 + (int) (Math.cos(angl) * len);
		g.drawLine(x2, y2, x1, y1);
		angl -= Math.PI * delAngl * 2 / 180;
		x1 = x2 + (int) (Math.sin(angl) * len);
		y1 = y2 + (int) (Math.cos(angl) * len);
		g.drawLine(x2, y2, x1, y1);
	}

	private String drawNode(int nd, Point point, int D, Graphics g) {
		String name = "";
		switch (nd) {
			case 1:
				name = "CP";
				point.x = point.x / 10 * 1;
				point.y = point.y / 5 * 5;
				break;
			case 2:
				name = "Молочная";
				point.x = point.x / 10 * 2;
				point.y = point.y / 5 * 2;
				break;
			case 3:
				name = "Дикая";
				point.x = point.x / 10 * 4;
				point.y = point.y / 5 * 2;
				break;
			case 4:
				name = "Кипелово";
				point.x = point.x / 10 * 7;
				point.y = point.y / 5 * 2;
				break;
			case 5:
				name = "Кущуба";
				point.x = point.x / 10 * 10;
				point.y = point.y / 5 * 2;
				break;
			case 6:
				name = "Чебсара";
				point.x = point.x / 10 * 10;
				point.y = point.y / 5 * 1;
				break;
			case 7:
				name = "Шексна";
				point.x = point.x / 10 * 7;
				point.y = point.y / 5 * 1;
				break;
			case 8:
				name = "Шеломово";
				point.x = point.x / 10 * 4;
				point.y = point.y / 5 * 1;
				break;
			case 9:
				name = "Хемалда";
				point.x = point.x / 10 * 4;
				point.y = point.y / 5 * 1;
				break;
			case 10:
				name = "Череповец1";
				point.x = point.x / 10 * 4;
				point.y = point.y / 5 * 4;
				break;
			case 11:
				name = "Череповец2";
				point.x = point.x / 10 * 8;
				point.y = point.y / 5 * 4;
				break;
			case 12:
				name = "Кошта";
				point.x = point.x / 10 * 10;
				point.y = point.y / 5 * 4;
				break;
			case 13:
				name = "Ч1_MOD";
				point.x = point.x / 10 * 3;
				point.y = point.y / 5 * 3;
				break;
			case 14:
				name = "ЦП_2";
				point.x = point.x / 10 * 10;
				point.y = point.y / 5 * 5;
				break;
			case 15:
				name = "Ч1_APM";
				point.x = point.x / 10 * 5;
				point.y = point.y / 5 * 3;
				break;
			case 16:
				name = "БП 522км";
				point.x = point.x / 10 * 7;
				point.y = point.y / 5 * 3;
				break;
			case 17:
				name = "Кошта_APM1";
				point.x = point.x / 10 * 10;
				point.y = point.y / 5 * 3;
				break;
			case 18:
				name = "Кошта_APM2";
				point.x = point.x / 10 * 9;
				point.y = point.y / 5 * 3;
				break;
			case 19:
				name = "БП 547км";
				point.x = point.x / 10 * 8;
				point.y = point.y / 5 * 3;
				break;
			default:
				name = "" + nd;
				point.x = D;
				point.y = D;
		}
		if (g != null) {
			g.setColor(Color.BLACK);
			g.drawOval(point.x - D / 2, point.y - D / 2, D, D);
		}
		return name;
	}

	@Override
	protected void firstStep() {

	}

	private void formKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_formKeyPressed
		repaint();
		if (NumDev != -1) {
			if (evt.getExtendedKeyCode() == 37 || evt.getExtendedKeyCode() == 65 || evt.getExtendedKeyCode() == 16778308) {
				for (int i = 0; i < DataDriverVector.size(); i++) {
					if (DataDriverVector.get(i).Id == NumDev) {
						if (i + 1 < DataDriverVector.size()) {
							NumDev = DataDriverVector.get(i + 1).Id;
						} else {
							NumDev = DataDriverVector.get(0).Id;
						}
						break;
					}
				}
			} else
				if (evt.getExtendedKeyCode() == 39 || evt.getExtendedKeyCode() == 68 || evt.getExtendedKeyCode() == 16778290) {
					for (int i = 0; i < DataDriverVector.size(); i++) {
						if (DataDriverVector.get(i).Id == NumDev) {
							if (i - 1 < 0) {
								NumDev = DataDriverVector.get(DataDriverVector.size() - 1).Id;
							} else {
								NumDev = DataDriverVector.get(i - 1).Id;
							}
							break;
						}
					}
				}
			println("Devise = " + NumDev);
		}
		if (NumNode != -1) {
			if (evt.getExtendedKeyCode() == 37 || evt.getExtendedKeyCode() == 65 || evt.getExtendedKeyCode() == 16778308) {
				NumNode--;
				if (NumNode < 1) {
					NumNode = countNode;
				}
			} else
				if (evt.getExtendedKeyCode() == 39 || evt.getExtendedKeyCode() == 68 || evt.getExtendedKeyCode() == 16778290) {
					NumNode++;
					if (NumNode > countNode) {
						NumNode = 1;
					}
				}
			println("Node = " + drawNode(NumNode, new Point(1, 1), 1, null));
		}
	}// GEN-LAST:event_formKeyPressed

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
		jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
		jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed(java.awt.event.KeyEvent evt) {
				formKeyPressed(evt);
			}
		});

		jPanel1.setFont(new java.awt.Font("Times New Roman", 0, 10)); // NOI18N

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 763, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 437, Short.MAX_VALUE));

		jMenu1.setText("Меню");

		jCheckBoxMenuItem3.setText("По одному узлу");
		jCheckBoxMenuItem3.addActionListener(evt -> jCheckBoxMenuItem3ActionPerformed(evt));
		jMenu1.add(jCheckBoxMenuItem3);

		jCheckBoxMenuItem1.setText("По одному устройству");
		jCheckBoxMenuItem1.addActionListener(evt -> jCheckBoxMenuItem1ActionPerformed(evt));
		jMenu1.add(jCheckBoxMenuItem1);

		jCheckBoxMenuItem2.setText("Автолистание");
		jCheckBoxMenuItem2.addActionListener(evt -> jCheckBoxMenuItem2ActionPerformed(evt));
		jMenu1.add(jCheckBoxMenuItem2);

		jMenuBar1.add(jMenu1);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
		repaint();
		if (jCheckBoxMenuItem1.getState()) {
			NumDev = DataDriverVector.get(0).Id;
		} else {
			NumDev = -1;
			jCheckBoxMenuItem2.setSelected(false);
			jCheckBoxMenuItem3.setSelected(false);
		}
		autoScroll = false;
	}// GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

	private void jCheckBoxMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItem2ActionPerformed
		jCheckBoxMenuItem1.setSelected(true);
		jCheckBoxMenuItem1ActionPerformed(null);
		autoScroll = jCheckBoxMenuItem2.getState();
	}// GEN-LAST:event_jCheckBoxMenuItem2ActionPerformed

	private void jCheckBoxMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItem3ActionPerformed
		repaint();
		if (jCheckBoxMenuItem3.getState()) {
			NumNode = 1;
			jCheckBoxMenuItem1.setSelected(false);
			jCheckBoxMenuItem1ActionPerformed(null);
		} else {
			NumNode = -1;
		}
	}// GEN-LAST:event_jCheckBoxMenuItem3ActionPerformed

	@Override
	protected void msgIn(String data_S, String string_H) {

	}

	@Override
	protected void newInitComponents() {

	}

	@Override
	protected void newStep() {

	}

	@Override
	@SuppressWarnings("override")
	public void paint(Graphics gr) {
		super.paint(gr);

		Graphics g = jPanel1.getGraphics();
		int x = (int) (this.getWidth() * 0.8) / 2;
		int y = (int) (this.getHeight() * 0.7) / 2;

		List<Integer> listNode = new ArrayList<>(Nodests);
		@SuppressWarnings("UseOfObsoleteCollectionType")
		Vector<Point> PointNode = new Vector<>();
		for (double i = 0, j = 0; i < Math.PI * 2; i += Math.PI * 2 / countNode, j++) {
			// Рисуем узел
			// int xpos = (int) ((Math.sin(i - Math.PI / 2) + 1.2) * x);
			// int ypos = (int) ((Math.cos(i - Math.PI / 2) + 1.3) * y);
			int D = (int) (x * 0.15);
			// g.setColor(Color.BLACK);
			// g.drawOval(xpos - D / 2, ypos - D / 2, D, D);
			int nd = listNode.get((int) j);
			Point pointNode = new Point(x * 2, y * 2);
			String nameNode = drawNode(nd, pointNode, D, g);

			// Сохраняем центр узла
			if (PointNode.size() < nd) {
				PointNode.setSize(nd);
			}
			PointNode.add(nd, pointNode);
			List<Device> listDev = new ArrayList<>();
			DataDriverVector.stream().filter((k) -> (k.Node == nd)).forEachOrdered((k) -> {
				listDev.add(k);
			});
			// Рисуем дрова на узле
			for (double smalC = 0, smalj = 0; smalj < listDev.size(); smalC += Math.PI * 2 / listDev.size(), smalj++) {
				int smalxpos = pointNode.x + (int) (Math.sin(smalC) * D / 2);
				int smalypos = pointNode.y + (int) (Math.cos(smalC) * D / 2);
				int smalD = (int) (D * 0.2);
				smalxpos -= smalD / 2;
				smalypos -= smalD / 2;
				Device dev = listDev.get((int) smalj);
				g.setColor(dev.getColog());
				g.drawString("" + dev.Id, smalxpos, smalypos);
				g.drawOval(smalxpos, smalypos, smalD, smalD);
				dev.setPoent(smalxpos + smalD / 2, smalypos + smalD / 2);
			}
			Font a = g.getFont();
			a.deriveFont(10);
			g.drawString("" + nameNode, pointNode.x, pointNode.y);
			a.deriveFont(5);
		}
		// Рисуем связи каждой дровины
		for (Device i : DataDriverVector) {
			if (NumDev == -1 || NumDev == i.Id) {
				g.setColor(i.getColog());
				for (Node j : i.Nodes) {
					if (NumNode == -1 || j.from == NumNode || j.to == NumNode) {
						if (j.from == i.Node) {
							drawArrow(i.point.x, i.point.y, PointNode.get(j.to).x, PointNode.get(j.to).y, g);
						} else
							if (j.to == i.Node) {
								drawArrow(PointNode.get(j.from).x, PointNode.get(j.from).y, i.point.x, i.point.y, g);
							} else {
								drawArrow(PointNode.get(j.from).x, PointNode.get(j.from).y, PointNode.get(j.to).x, PointNode.get(j.to).y, g);
							}
					}
				}
				if (NumDev == i.Id) {
					drawArrow(0, 0, i.point.x, i.point.y, g);
				}
			}
		}
	}

	private void parse(String line) {
		if (line.indexOf("[Devices]") != -1) {
			isDevise = true;
			isConnections = false;
			return;
		} else
			if (line.indexOf("[Connections]") != -1) {
				isConnections = true;
				isDevise = false;
				return;
			} else
				if (line.indexOf("#") != -1) {
					return;
				} else
					if (line.length() == 0) { return; }
		if (isDevise) {
			// Секция устройст
			while (line.indexOf("  ") > 0) {
				line = line.replace("  ", " ");
			}
			DataDriverVector.add(new Device(line));
			return;
		}
		if (isConnections) {
			// Секция устройст
			while (line.indexOf("  ") > 0) {
				line = line.replace("  ", " ");
			}
			String[] args = line.split(" ");
			int id = Integer.parseInt(args[0]);
			int from = Integer.parseInt(args[1]);
			int to = Integer.parseInt(args[2]);
			if (from > countNode) {
				countNode = from;
			} else
				if (to > countNode) {
					countNode = to;
				}
			for (Device j : DataDriverVector) {
				if (j.Node > countNode) {
					countNode = j.Node;
				}
				if (j.Id == id) {
					j.addNode(from, to);
					return;
				}
			}
		}
		// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		return null;
	}
}
