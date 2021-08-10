package Emus;

import javax.swing.JOptionPane;

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
public class MPAB extends EmusDeff {

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenuItem				InstalZero;

	private javax.swing.JMenuItem				Reset;

	private javax.swing.JMenu					jMenu2;

	private javax.swing.JMenu					jMenu4;

	private javax.swing.JMenuBar				jMenuBar1;

	private javax.swing.JMenuItem				jMenuItem1;

	private javax.swing.JMenuItem				jMenuItem2;

	private javax.swing.JMenuItem				jMenuItem3;

	private javax.swing.JRadioButtonMenuItem	jMenuItem4;

	private javax.swing.JRadioButtonMenuItem	jMenuItem6;
	private boolean								isRandom	= false;
	private boolean								isErr		= false;
	// End of variables declaration//GEN-END:variables

	/**
	 * Creates new form UKTRC
	 */
	public MPAB(Setings aThis) {
		super(aThis, "MPAB", 10000);
	}

	private byte[] detectedDisc(byte[] rBuf) {
		byte[] data = new byte[20 * 2];
		int tmp = 0, tmp1 = 0, tmp2 = 0, dataByte = 0x00, dataByte_add = 0;

		// 1 - 0
		tmp = rBuf[3 + 29 * 2 + 1];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 0;
		dataByte |= tmp;

		// 2 - 1
		tmp1 = tmp2 = rBuf[3 + 30 * 2];
		tmp1 = tmp1 >> 0 & 0x01;
		tmp2 = tmp2 >> 4 & 0x01;
		tmp = (tmp1 | tmp2) << 1;
		dataByte |= tmp;

		// 3 - 2
		tmp1 = tmp2 = rBuf[3 + 29 * 2 + 1];
		tmp1 = tmp1 >> 1 & 0x01;
		tmp2 = tmp2 >> 5 & 0x01;
		tmp = (tmp1 | tmp2) << 2;
		dataByte |= tmp;

		// 4 - 3
		tmp = rBuf[3 + 29 * 2 + 1];
		tmp = tmp >> 2 & 0x01;
		tmp = tmp << 3;
		dataByte |= tmp;

		// 5 - 4
		tmp1 = tmp2 = rBuf[3 + 30 * 2];
		tmp1 = tmp1 >> 1 & 0x01;
		tmp2 = tmp2 >> 5 & 0x01;
		tmp = (tmp1 | tmp2) << 4;
		dataByte |= tmp;

		// 6 - 5
		tmp1 = tmp2 = rBuf[3 + 29 * 2 + 1];
		tmp1 = tmp1 >> 3 & 0x01;
		tmp2 = tmp2 >> 7 & 0x01;
		tmp = (tmp1 | tmp2) << 5;
		dataByte |= tmp;

		// 7, 8 в резерве

		// ДАННЫЕ с 1 по 8
		data[0] = (byte) dataByte;
		data[0 + data.length / 2] = (byte) 0xC0;

		dataByte = 0x00;

		// 9 - 0
		tmp = rBuf[3 + 31 * 2];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 0;
		dataByte |= tmp;

		// 10 - 1
		tmp = rBuf[3 + 29 * 2 + 1];
		tmp = tmp >> 4 & 0x01;
		tmp = tmp << 1;
		dataByte |= tmp;

		// 11 - 2
		tmp = rBuf[3 + 29 * 2];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 2;
		dataByte |= tmp;

		// 12 - 3
		tmp1 = tmp2 = rBuf[3 + 29 * 2];
		tmp1 = tmp1 >> 1 & 0x01;
		tmp2 = tmp2 >> 5 & 0x01;
		tmp = (tmp1 | tmp2) << 3;
		dataByte |= tmp;

		// 13 - 4
		tmp = rBuf[3 + 29 * 2];
		tmp = tmp >> 2 & 0x01;
		tmp = tmp << 4;
		dataByte |= tmp;

		// 14 - 5
		tmp1 = tmp2 = rBuf[3 + 29 * 2];
		tmp1 = tmp1 >> 3 & 0x01;
		tmp2 = tmp2 >> 7 & 0x01;
		tmp = (tmp1 | tmp2) << 5;
		dataByte |= tmp;

		// 15 - 6
		tmp = rBuf[3 + 71 * 2];
		tmp = tmp >> 4 & 0x01;
		tmp = tmp << 6;
		dataByte |= tmp;

		// 16 в резерве

		// ДАННЫЕ с 9 по 16
		data[0 + 1] = (byte) dataByte;
		data[0 + 1 + data.length / 2] = (byte) 0x80;

		dataByte = 0x00;
		dataByte_add = 0x00;

		// 17 - 0
		tmp = rBuf[3 + 72 * 2];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 0;
		dataByte |= tmp;

		// 18 - 1
		tmp = rBuf[3 + 72 * 2];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 2;
		dataByte |= tmp;

		// 19 - 2
		tmp = rBuf[3 + 72 * 2 + 1];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 2;
		dataByte |= tmp;

		// 20 - 3
		tmp1 = tmp2 = rBuf[3 + 72 * 2];
		tmp1 = tmp1 >> 6 & 0x01;
		tmp2 = tmp2 >> 7 & 0x01;
		tmp = (tmp1 & tmp2) << 3;
		dataByte |= tmp;

		// 21 - 4, 22 - 5, 23 - 6, 24 - 7, 25 - 0, 26 - 1, 27 - 2, 28 - 3
		tmp = rBuf[3 + 43 * 2 + 1];
		tmp = tmp & 0x1F;
		switch (tmp) {
			case 0x00:
				dataByte |= 0x01 << 4;
				break;

			case 0x01:
				dataByte |= 0x01 << 5;
				break;

			case 0x02:
				dataByte |= 0x01 << 6;
				break;

			case 0x04:
				dataByte |= 0x01 << 7;
				break;

			case 0x08:
				dataByte_add |= 0x01 << 0;
				break;

			case 0x09:
				dataByte_add |= 0x01 << 1;
				break;

			case 0x10:
				dataByte_add |= 0x01 << 2;
				break;

			default:
				dataByte_add |= 0x01 << 3;
				break;
		}

		// ДАННЫЕ с 17 по 24
		data[0 + 2] = (byte) dataByte;
		data[0 + 2 + data.length / 2] = 0x00;

		dataByte = dataByte_add;
		dataByte_add = 0x00;

		// 29 - 4
		tmp = rBuf[3 + 43 * 2 + 1];
		tmp = tmp >> 5 & 0x01;
		tmp = tmp << 4;
		dataByte |= tmp;

		// 30 - 5
		tmp = rBuf[3 + 43 * 2 + 1];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 5;
		dataByte |= tmp;

		// 31 - 6, 32 - 7, 33 - 0, 34 - 1, 35 - 2, 36 - 3, 37 - 4
		tmp = rBuf[3 + 37 * 2 + 1];
		tmp = tmp & 0x1F;
		switch (tmp) {
			case 0x00:
				dataByte |= 0x01 << 6;
				break;

			case 0x01:
				dataByte |= 0x01 << 7;
				break;

			case 0x02:
				dataByte_add |= 0x01 << 0;
				break;

			case 0x04:
				dataByte_add |= 0x01 << 1;
				break;

			case 0x08:
				dataByte_add |= 0x01 << 2;
				break;

			case 0x10:
				dataByte_add |= 0x01 << 3;
				break;

			default:
				dataByte_add |= 0x01 << 4;
				break;
		}

		// ДАННЫЕ с 25 по 32
		data[0 + 3] = (byte) dataByte;
		data[0 + 3 + data.length / 2] = 0x00;

		dataByte = dataByte_add;
		dataByte_add = 0x00;

		// 38 - 5
		tmp = rBuf[3 + 37 * 2];
		tmp = tmp >> 5 & 0x01;
		tmp = tmp << 4;
		dataByte |= tmp;

		// 39 - 6
		tmp = rBuf[3 + 37 * 2];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 6;
		dataByte |= tmp;

		// 40 в резерве

		// ДАННЫЕ с 33 по 40
		data[0 + 4] = (byte) dataByte;
		data[0 + 4 + data.length / 2] = (byte) 0x80;

		dataByte = 0x00;

		// 41, 42, 43, 44, 45, 46, 47
		dataByte = rBuf[3 + 42 * 2];
		dataByte &= 0x7F;

		// 48 - 7
		tmp = rBuf[3 + 42 * 2 + 1];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 7;
		dataByte |= tmp;

		// ДАННЫЕ с 41 по 48
		data[0 + 5] = (byte) dataByte;
		data[0 + 5 + data.length / 2] = 0x00;

		dataByte = 0x00;

		// 49, 50, 51, 52, 53, 54, 55
		dataByte = rBuf[3 + 42 * 2 + 1];
		dataByte = dataByte >> 1;

		// 56 - 7
		tmp = rBuf[3 + 36 * 2];
		tmp = tmp >> 2 & 0x01;
		tmp = tmp << 7;
		dataByte |= tmp;

		// ДАННЫЕ с 49 по 56
		data[0 + 6] = (byte) dataByte;
		data[0 + 6 + data.length / 2] = 0x00;

		dataByte = 0x00;

		// 57, 58, 59, 60
		dataByte = rBuf[3 + 36 * 2];
		dataByte = dataByte >> 4;

		// 61 - 4
		tmp1 = tmp2 = rBuf[3 + 72 * 2];
		tmp1 = tmp1 >> 3 & 0x01;
		tmp2 = tmp2 >> 4 & 0x01;
		tmp = (tmp1 | tmp2) << 4;
		dataByte |= tmp;

		// 62 - 5
		tmp = rBuf[3 + 72 * 2];
		tmp = tmp >> 5 & 0x01;
		tmp = tmp << 5;
		dataByte |= tmp;

		// 63, 64 в резерве

		// ДАННЫЕ с 57 по 64
		data[0 + 7] = (byte) dataByte;
		data[0 + 7 + data.length / 2] = (byte) 0xC0;

		dataByte = 0x00;
		dataByte_add = 0x00;

		// 65 - 0
		tmp = rBuf[3 + 71 * 2 + 1];
		tmp = tmp >> 1 & 0x01;
		tmp = tmp << 0;
		dataByte |= tmp;

		// 66 - 1
		tmp = rBuf[3 + 71 * 2 + 1];
		tmp = tmp >> 3 & 0x01;
		tmp = tmp << 1;
		dataByte |= tmp;

		// 67 - 2
		tmp = rBuf[3 + 71 * 2];
		tmp = tmp >> 5 & 0x01;
		tmp = tmp << 2;
		dataByte |= tmp;

		// 68 - 3
		tmp1 = tmp2 = rBuf[3 + 71 * 2 + 1];
		tmp1 = tmp1 >> 5 & 0x01;
		tmp2 = tmp2 >> 6 & 0x01;
		tmp = (tmp1 & tmp2) << 3;
		dataByte |= tmp;

		// 69 - 4, 70 - 5, 71 - 6, 72 - 7, 73 - 0, 74 - 1, 75 - 2
		tmp = rBuf[3 + 34 * 2 + 1];
		tmp = tmp & 0x1F;
		switch (tmp) {
			case 0x00:
				dataByte |= 0x01 << 4;
				break;

			case 0x01:
				dataByte |= 0x01 << 5;
				break;

			case 0x02:
				dataByte |= 0x01 << 6;
				break;

			case 0x04:
				dataByte |= 0x01 << 7;
				break;

			case 0x08:
				dataByte_add |= 0x01 << 0;
				break;

			case 0x10:
				dataByte_add |= 0x01 << 1;
				break;

			default:
				dataByte_add |= 0x01 << 2;
				break;
		}

		// ДАННЫЕ с 65 по 72
		data[0 + 8] = (byte) dataByte;
		data[0 + 8 + data.length / 2] = 0x00;

		dataByte = dataByte_add;
		dataByte_add = 0x00;

		// 76, 77, 78, 79, 80 в резерве

		// ДАННЫЕ с 73 по 80
		data[0 + 9] = (byte) dataByte;
		data[0 + 9 + data.length / 2] = (byte) 0xF8;

		dataByte = 0x00;

		// 81, 82, 83, 84, 85, 86, 87
		dataByte = rBuf[3 + 71 * 2];
		dataByte &= 0x7F;

		// 88 - 7
		tmp = rBuf[3 + 71 * 2 + 1];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 7;
		dataByte |= tmp;

		// ДАННЫЕ с 81 по 88
		data[0 + 10] = (byte) dataByte;
		data[0 + 10 + data.length / 2] = 0x00;

		dataByte = 0x00;

		// 89, 90, 91, 92
		dataByte = rBuf[3 + 71 * 2 + 1];
		dataByte = dataByte >> 1;
		dataByte = dataByte & 0x0F;

		// 93 - 4
		tmp1 = tmp2 = rBuf[3 + 71 * 2 + 1];
		tmp1 = tmp1 >> 5 & 0x01;
		tmp2 = tmp2 >> 6 & 0x01;
		tmp = (tmp1 & tmp2) << 3;
		dataByte |= tmp;

		// 94 - 5
		tmp = rBuf[3 + 71 * 2 + 1];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 5;
		dataByte |= tmp;

		// 95, 96 в резерве

		// ДАННЫЕ с 89 по 96
		data[0 + 11] = (byte) dataByte;
		data[0 + 11 + data.length / 2] = 0x00;

		dataByte = 0x00;
		dataByte_add = 0x00;

		// 97 - 0
		tmp = rBuf[3 + 64 * 2];
		tmp = tmp >> 2 & 0x01;
		tmp = tmp << 0;
		dataByte |= tmp;

		// 98 - 1, 99 - 2, 100 - 3, 101 - 4
		dataByte = rBuf[3 + 64 * 2];
		dataByte = dataByte >> 4;

		// 102 - 5, 103 - 6, 104 - 7
		dataByte_add = rBuf[3 + 65 * 2];
		dataByte_add = dataByte_add << 5;
		dataByte = dataByte | dataByte_add;

		// ДАННЫЕ с 97 по 104
		data[0 + 12] = (byte) dataByte;
		data[0 + 12 + data.length / 2] = 0x00;

		dataByte = 0x00;
		dataByte_add = 0x00;

		// 105 - 0, 106 - 1, 107 - 2, 108 - 3
		dataByte = rBuf[3 + 65 * 2];
		dataByte = dataByte >> 3;
		dataByte = dataByte & 0x0F;

		// 109 - 4
		tmp = rBuf[3 + 65 * 2 + 1];
		tmp = tmp >> 0 & 0x01;
		tmp = tmp << 4;
		dataByte |= tmp;

		// 110 - 5, 111 - 6, 112 - 7
		dataByte_add = rBuf[3 + 65 * 2 + 1];
		dataByte_add = dataByte_add << 4;
		dataByte_add = dataByte_add & 0xE0;
		dataByte = dataByte | dataByte_add;

		// ДАННЫЕ с 105 по 112
		data[0 + 13] = (byte) dataByte;
		data[0 + 13 + data.length / 2] = 0x00;

		dataByte = 0x00;

		// 113 - 0, 114 - 1, 115 - 2, 116 - 3
		dataByte_add = rBuf[3 + 65 * 2 + 1];
		dataByte_add = dataByte_add >> 4;
		dataByte_add = dataByte_add & 0x0F;
		dataByte = dataByte | dataByte_add;

		// 117 - 4, 118 - 5, 119 - 6, 120 - 7, 121 - 0, 122 - 1, 123 - 2
		tmp = rBuf[3 + 64 * 2 + 1];
		tmp = tmp & 0x1F;
		switch (tmp) {
			case 0x00:
				dataByte |= 0x01 << 4;
				break;

			case 0x01:
				dataByte |= 0x01 << 5;
				break;

			case 0x02:
				dataByte |= 0x01 << 6;
				break;

			case 0x04:
				dataByte |= 0x01 << 7;
				break;

			case 0x08:
				dataByte_add |= 0x01 << 0;
				break;

			case 0x10:
				dataByte_add |= 0x01 << 1;
				break;

			default:
				dataByte_add |= 0x01 << 2;
				break;
		}

		// ДАННЫЕ с 113 по 120
		data[0 + 14] = (byte) dataByte;
		data[0 + 14 + data.length / 2] = 0x00;

		dataByte = dataByte_add;
		dataByte_add = 0x00;

		// 124, 125, 126, 127, 128 в резерве

		// ДАННЫЕ с 120 по 128
		data[0 + 15] = (byte) dataByte;
		data[0 + 15 + data.length / 2] = (byte) 0xF8;

		dataByte = 0x00;
		dataByte_add = 0x00;

		// 129, 130, 131, 132 в резерве

		// 133 - 4, 134 - 5, 135 - 6, 136 - 7, 137 - 0, 138 - 1, 139 - 2
		tmp = rBuf[3 + 66 * 2 + 1];
		tmp = tmp & 0x1F;
		switch (tmp) {
			case 0x00:
				dataByte |= 0x01 << 4;
				break;

			case 0x01:
				dataByte |= 0x01 << 5;
				break;

			case 0x02:
				dataByte |= 0x01 << 6;
				break;

			case 0x04:
				dataByte |= 0x01 << 7;
				break;

			case 0x08:
				dataByte_add |= 0x01 << 0;
				break;

			case 0x10:
				dataByte_add |= 0x01 << 1;
				break;

			default:
				dataByte_add |= 0x01 << 2;
				break;
		}

		// ДАННЫЕ с 129 по 136
		data[0 + 16] = (byte) dataByte;
		data[0 + 16 + data.length / 2] = (byte) 0xF0;

		dataByte = dataByte_add;
		dataByte_add = 0x00;

		// 140 - 3
		tmp = rBuf[3 + 66 * 2 + 1];
		tmp = tmp >> 5 & 0x01;
		tmp = tmp << 3;
		dataByte |= tmp;

		// 141 - 4
		tmp = rBuf[3 + 66 * 2 + 1];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 4;
		dataByte |= tmp;

		// ДАННЫЕ с 137 по 144
		data[0 + 17] = (byte) dataByte;
		data[0 + 17 + data.length / 2] = (byte) 0xE0;

		dataByte = 0x00;
		dataByte_add = 0x00;

		// 145, 146, 147, 148 в резерве

		// 149 - 4, 150 - 5, 151 - 6, 152 - 7, 153 - 0, 154 - 1, 155 - 2, 156 - 3
		tmp = rBuf[3 + 67 * 2 + 1];
		tmp = tmp & 0x1F;
		switch (tmp) {
			case 0x00:
				dataByte |= 0x01 << 4;
				break;

			case 0x01:
				dataByte |= 0x01 << 5;
				break;

			case 0x02:
				dataByte |= 0x01 << 6;
				break;

			case 0x04:
				dataByte |= 0x01 << 7;
				break;

			case 0x08:
				dataByte_add |= 0x01 << 0;
				break;

			case 0x09:
				dataByte_add |= 0x01 << 1;
				break;

			case 0x10:
				dataByte_add |= 0x01 << 2;
				break;

			default:
				dataByte_add |= 0x01 << 3;
				break;
		}

		// ДАННЫЕ с 145 по 152
		data[0 + 18] = (byte) dataByte;
		data[0 + 18 + data.length / 2] = (byte) 0xF0;

		dataByte = dataByte_add;
		dataByte_add = 0x00;

		// 157 - 4
		tmp = rBuf[3 + 67 * 2 + 1];
		tmp = tmp >> 5 & 0x01;
		tmp = tmp << 4;
		dataByte |= tmp;

		// 158 - 5
		tmp = rBuf[3 + 66 * 2 + 1];
		tmp = tmp >> 7 & 0x01;
		tmp = tmp << 5;
		dataByte |= tmp;

		// ДАННЫЕ с 153 по 160
		data[0 + 19] = (byte) dataByte;
		data[0 + 19 + data.length / 2] = (byte) 0xC0;

		return data;

	}

	@Override
	protected void firstStep() {
	}

	private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem1ActionPerformed
		String res = JOptionPane.showInputDialog(this, "Введите код, который надо пустить");
		if (res == null) { return; }

		numKontr = Integer.parseInt(res);
		newStep();
	}// GEN-LAST:event_jMenuItem1ActionPerformed

	private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem4ActionPerformed
		isRandom = !isRandom;
		jMenuItem4.setSelected(isRandom);
		newStep();
	}// GEN-LAST:event_jMenuItem4ActionPerformed

	private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem6ActionPerformed
		isErr = !isErr;
		jMenuItem6.setSelected(isErr);
		newStep();
	}// GEN-LAST:event_jMenuItem6ActionPerformed

	@Override
	protected void msgIn(String data_S, String string_H) {
		println("<" + string_H);

		String req[] = string_H.split(" ");
		int num = Integer.parseInt(req[0]);
		int fun = Integer.parseInt(req[1]);
		println("Адрес = " + num + " Функция = " + fun);

		byte[] array1;
		if (isErr) {
			array1 = new byte[] { 0, 1, 2, 3, 4 };
			array1[0] = (byte) num;
			array1[1] = (byte) (fun | 0x80);
			array1[2] = (byte) numKontr; // код ошибки

			int[] crc = MyMatch.calculateCRC(array1, 0, array1.length - 2);
			array1[array1.length - 2] = (byte) crc[0]; // CRC
			array1[array1.length - 1] = (byte) crc[1];
		} else {
			array1 = new byte[155];
			array1[0] = (byte) num;
			array1[1] = (byte) fun;
			array1[2] = (byte) 150; // счётчик байт полезного сообщения

			for (int i = 3; i < array1.length - 2; i++) {
				switch (i) {
					case 3 + 11 * 2 + 0:
						array1[i] = (byte) (numKontr >> 8);
						break;
					case 3 + 11 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 12 * 2 + 0:
						array1[i] = (byte) (numKontr >> 8);
						break;
					case 3 + 12 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 63 * 2 + 0:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 63 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 70 * 2 + 0:
						array1[i] = (byte) (numKontr >> 8);
						break;
					case 3 + 70 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					default:
						array1[i] = (byte) numKontr;
						break;
				}
			}

			int[] crc = MyMatch.calculateCRC(array1, 0, array1.length - 2);
			array1[array1.length - 2] = (byte) crc[0]; // CRC
			array1[array1.length - 1] = (byte) crc[1];
		}
		writeBytes(array1);
		String data = "";
		for (byte element : array1) {
			data += " " + String.format("%02X ", element);
		}
		println(">" + data);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	@Override
	protected void newInitComponents() {

		jMenuBar1 = new javax.swing.JMenuBar();
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenu2 = new javax.swing.JMenu();
		jMenuItem4 = new javax.swing.JRadioButtonMenuItem();
		jMenuItem6 = new javax.swing.JRadioButtonMenuItem();
		Reset = new javax.swing.JMenuItem();
		InstalZero = new javax.swing.JMenuItem();
		jMenuItem2 = new javax.swing.JMenuItem();
		jMenu4 = new javax.swing.JMenu();
		jMenuItem3 = new javax.swing.JMenuItem();

		jMenuItem1.setText("Устновить значение");
		jMenuItem1.addActionListener(evt -> jMenuItem1ActionPerformed(evt));
		Menu.add(jMenuItem1);

		jMenuItem4.setText("Случайные");
		jMenuItem4.setSelected(false);
		jMenuItem4.addActionListener(evt -> jMenuItem4ActionPerformed(evt));
		Menu.add(jMenuItem4);

		jMenuItem6.setText("Отказ");
		jMenuItem6.setSelected(false);
		jMenuItem6.addActionListener(evt -> jMenuItem6ActionPerformed(evt));
		Menu.add(jMenuItem6);

		setTitle("Эмулятор автоблокировки МПАБ");

	}// </editor-fold>//GEN-END:initComponents;

	@Override
	protected void newStep() {
		if (isRandom) {
			numKontr = (int) (Math.random() * 127);
		}
		TextList.removeAll();
		if (isErr) {
			TextList.add("Сигнализируем об ошибке " + numKontr);
		} else {
			byte[] array1 = new byte[155];
			array1[0] = (byte) 0;
			array1[1] = (byte) 0;
			array1[2] = (byte) 150; // счётчик байт полезного сообщения

			for (int i = 3; i < array1.length - 2; i++) {
				switch (i) {
					case 3 + 11 * 2 + 0:
						array1[i] = (byte) (numKontr >> 8);
						break;
					case 3 + 11 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 12 * 2 + 0:
						array1[i] = (byte) (numKontr >> 8);
						break;
					case 3 + 12 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 63 * 2 + 0:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 63 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					case 3 + 70 * 2 + 0:
						array1[i] = (byte) (numKontr >> 8);
						break;
					case 3 + 70 * 2 + 1:
						array1[i] = (byte) numKontr;
						break;
					default:
						array1[i] = (byte) numKontr;
						break;
				}
			}

			int[] crc = MyMatch.calculateCRC(array1, 0, array1.length - 2);
			array1[array1.length - 2] = (byte) crc[0]; // CRC
			array1[array1.length - 1] = (byte) crc[1];
			array1 = detectedDisc(array1);
			String msgD = "";
			for (int i = 0; i < array1.length / 2; i++) {
				for (int j = 0; j < 8; j++) {
					byte state1 = (byte) (array1[i] & 1 << j);
					byte state2 = (byte) (array1[i + array1.length / 2] & 1 << j);
					byte ex = (byte) (state1 != 0 ? 1 << 0 : 0);
					ex |= (byte) (state2 != 0 ? 1 << 1 : 0);
					int num = i * 8 + j + 1;
					msgD += " D" + (num < 10 ? "00" + num : num < 100 ? "0" + num : num) + "=" + (ex == 2 ? "X" : ex);
				}
				TextList.add(msgD);
				msgD = "";
			}
			String msgA = "";
			msgA += " A1 = " + numKontr * 1000 + "";
			msgA += " A2 = " + numKontr * 1000 + "";
			msgA += " A3 = " + (byte) numKontr * 1000 + "";
			msgA += " A4 = " + (byte) numKontr * 1000 + "";
			msgA += " A5 = " + numKontr * 1000 + "";

			TextList.add(msgA);
		}
	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		// 
		return null;
	}
}
