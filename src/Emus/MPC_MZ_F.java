package Emus;

import MyMatch.EmusDeff;
import MyMatch.MyMatch;
import MyMatch.Setings;

/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. */
/**
 *
 * @author Terran
 */
public class MPC_MZ_F extends EmusDeff {

	protected class Controller extends EmusDeff.Controller {

		public Controller() {
			super(0x02, "МПЦ-МЗ-Ф");
		}

		@Override
		public String getStrToBoard() {
			if (mode == Mode.UNDEFENDED) {
				return "XXXXXXXX";
			} else {
				return toStrWithZero(Integer.toBinaryString((int) val), 8);
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// End of variables declaration//GEN-END:variables

	public MPC_MZ_F(Setings Se) {
		super(Se, "МПЦ МЗ Ф", 500);
		addContr(2, 0, 1, 1);
	}

	@Override
	protected void firstStep() {
		newStep();
	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		// 
		return new Controller();
	}

	@Override
	protected void msgIn(String data_S, String string_H) {
		println("In " + string_H);
		String bytes[] = string_H.split(" ");
		byte array_h[] = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			array_h[i] = (byte) Integer.parseInt(bytes[i], 16);
		}
		if (array_h[0] == '#' && array_h[1] == 'N' && array_h[2] == 0 && array_h[3] == 0 && array_h[4] == (byte) 0xFF && array_h[5] == (byte) 0xFF) {
			println("Пришёл верный пустой пакет");
			return;
		}
		if (array_h[0] == '#' && array_h[1] == 'N') {
			println("Пришёл пакет от устройства с правильными символами начала '#' и 'N'");
		} else {
			println("Первые символы пакеты не '#' и 'N'");
		}
		if (array_h[2] == 0 && array_h[3] == 0) {
			println("Верная длина пакета 00_00");
		} else {
			println("Пакет не пустой, длинна " + bytes[2] + bytes[3]);
		}
		if (array_h[4] == (byte) 0xFF && array_h[5] == (byte) 0xFF) {
			println("Верный crc FF_FF");
		} else {
			println("Пакет с ошибкой в crc " + bytes[4] + bytes[5]);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	@Override
	protected void newInitComponents() {
	}// </editor-fold>//GEN-END:initComponents

	@Override
	protected void newStep() {
		regenerateControllerValue();

		// Пустой пакет - 6 байт, '?','B',00,00,FF,FF
		byte arrayFree[] = { '?', 'B', 0, 0, (byte) 0xFF, (byte) 0xFF };
		byte array1[] = new byte[6 + numKontr * 8];
		boolean isEmpty = false;
		array1[0] = '?'; // Символ начала пакета
		array1[1] = 'B'; // Символ источника данных
		array1[2] = (byte) (numKontr * 8);
		array1[3] = 0x00;

		for (int i = 0; i < numKontr; i++) {
			Controller dataDriv = (Controller) deviseVector.get(i)[0];
			array1[i + 4] = (byte) dataDriv.getValue();
		}

		int[] crc = MyMatch.crc_16(array1, 4, array1.length - 2);
		array1[array1.length - 2] = (byte) crc[1]; // CRC
		array1[array1.length - 1] = (byte) crc[0];

		if (isEmpty) {
			array1 = arrayFree;
		}
		writeBytes(array1);
		String data = "";
		for (byte element : array1) {
			data += " " + /* (char) element; */ String.format("%02X ", element);
		}
		println("Out" + data);
	}
}
