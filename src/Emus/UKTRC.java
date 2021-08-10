package Emus;

import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JOptionPane;

import MyMatch.EmusDeff;
import MyMatch.Setings;

/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. */
/**
 *
 * @author Terran
 */
public class UKTRC extends EmusDeff {

	private class Controller extends EmusDeff.Controller{

		private int		val;
		private Mode	mode;
		private int		seed;
		private int		DelSeed	= 10;

		public Controller() {
			super(10,"УКТРЦ");
			val = 0;
			mode = Mode.STEP;
			seed = (int) (Math.random() * DelSeed) - DelSeed / 2;
		}
		@Override
		public String toString() {
			if (mode != Mode.UNDEFENDED) {
				return df2.format(val / 1_000.0);
			} else {
				return "9999999";
			}
		}

		@Override
		protected String getStrToBoard() {
			if (mode == Mode.UNDEFENDED) {
				return "XX_XXX";
			} else {
				return df2.format(val / 1_000.0);
			}
		}
	}

	static enum Mode {
		RND, STEP, UNDEFENDED, NULL
	}

	private static DecimalFormat	df2					= new DecimalFormat("+00.000");

	// Variables declaration - do not modify//GEN-BEGIN:variables

	// End of variables declaration//GEN-END:variables

	/**
	 * Creates new form UKTRC
	 */
	public UKTRC(Setings Se) {
		super(Se, "UKTRC", 1000);
		addContr(1, 0, 8, 8);
	}

	private String addInt(int num) {
		String out = "";
		out += Integer.toHexString(num >> 4 & 0x0F).toUpperCase();
		out += Integer.toHexString(num & 0x0F).toUpperCase();
		out += (char) 0x0D;
		return out;
	}

	private byte crc(String msg) {
		byte Sum = 0;
		for (char i : msg.toCharArray()) {
			Sum += i;
		}
		return Sum;
	}

	private String end(String msg) {
		String out = "";
		byte crc = crc(msg);
		out += addInt(crc);
		out += (char) 0x0D;
		return out;
	}

	@Override
	protected void firstStep() {
	}

	@Override
	protected void msgIn(String data_S, String string_H) {
		println("<" + data_S);
		if (data_S.indexOf("$") != -1) {
			// Настройка
			int num1 = Integer.parseInt(data_S.substring(1, 3));
			if (num1 > numKontr) { return; }
			if (data_S.indexOf("RST") != -1) {
				println("Команда перезагрузиться контроллеру " + num1);
				return;
			}
			String msg1 = "!";
			msg1 += addInt(num1);
			msg1 += end(msg1);
			writeString(msg1);
			print(msg1);
		}
		if (data_S.indexOf("#") != -1) {
			// Данные
			int num2 = Integer.parseInt(data_S.substring(1, 3));
			if (num2 > numKontr) { return; }
			Controller[] dataDriv = (Controller[]) deviseVector.get(num2 - 1);
			String msg2 = ">";
			for (int i = 0; i < 8; i++) {
				msg2 += dataDriv[i];
			}
			if (msg2.length() != 57) {
				println("Косяк в сообщении, длина его не верна. " + msg2.length() + " вместо 57!!!");
				return;
			}
			msg2 += end(msg2);
			writeString(msg2);
			println(msg2);
		}

	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@Override
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	protected void newInitComponents() {
	}// </editor-fold>//GEN-END:initComponents

	@Override
	protected void newStep() {
		regenerateControllerValue();
	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		return new Controller();
	}
}
