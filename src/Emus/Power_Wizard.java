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
public class Power_Wizard extends EmusDeff {

	protected class AController extends EmusDeff.Controller {
		int Offset;
		int nBytes;
		double Koef;
		double Null;
		int num;
		public AController(String text, int Offset, int nBytes, double Koef,
				double Null, int num) {
			super(0x10, text);
			this.Offset = Offset;
			this.nBytes = nBytes;
			this.Koef = Koef;
			this.Null = Null;
			this.num = num;
		}
		@Override
		public String getStrToBoard() {
			return val + "";
		}
		public byte[] getVal() {
			byte out[] = {0x02, 0x00, 0x00};
			int val = (int) this.val;
			val = (int) ((val + Null) * Koef);
			out[1] = (byte) (val >> 8 & 0xFF);
			out[2] = (byte) (val & 0xFF);
			return out;
		}
	}

	protected class DController extends EmusDeff.Controller {
		public DController() {
			super(0x2, "Дискретные данные");
		}
		@Override
		public String getStrToBoard() {
			if (val % 10 == 0x01) {
				return "АВАРИЯ";
			} else if (val % 10 == 0x04) {
				return "Предупреждение";
			} else {
				return "Ок";
			}
		}
		public byte[] getVal() {
			byte out[] = {0x02, 0x00, 0x00};
			if (val % 10 == 0x01) {
				out[2] = 0x01;
			} else if (val % 10 == 0x04) {
				out[2] = 0x04;
			} else {
				out[2] = 0x00;
			}
			return out;
		}
	}

	enum Modes {
		None, ReadEvent, getEvent
	}

	// End of variables declaration//GEN-END:variables

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	Modes mode = Modes.None;

	public Power_Wizard(Setings Se) {
		super(Se, "PowerWizard (OOO \"ЭТС\" Москва)", 500);
		addContr(13, 0, 1, 1);
	}

	@Override
	protected void firstStep() {
		newStep();
	}
	@Override
	protected Controller getController(int numContr, int numPart) {
		// 
		switch (numContr) {
			case 0 :
				return new AController("Напряжение А (В)", 0x72, 1, 1, 0,
						numContr);
			case 1 :
				return new AController("Напряжение B (В)", 0x73, 1, 1, 0,
						numContr);
			case 2 :
				return new AController("Напряжение C (В)", 0x74, 1, 1, 0,
						numContr);
			case 3 :
				return new AController("Ток А (А)", 0x6F, 1, 1, 0, numContr);
			case 4 :
				return new AController("Ток В (А)", 0x70, 1, 1, 0, numContr);
			case 5 :
				return new AController("Ток С (А)", 0x71, 1, 1, 0, numContr);
			case 6 :
				return new AController("Частота (Гц)", 0x66, 1, 128, 0,
						numContr);
			case 7 :
				return new AController("Давление масла (кПа)", 0xC8, 1, 8, 0,
						numContr);
			case 8 :
				return new AController("Температура охлаждающей жидкости (С)",
						0xC9, 1, 32, 273, numContr);
			case 9 :
				return new AController("Напряжение батареи (В)", 0xCA, 1, 20, 0,
						numContr);
			case 10 :
				return new AController("Частота вращения коленчатого вала",
						0xCB, 1, 8, 0, numContr);
			case 11 :
				return new AController("Моточасы", 0xCC, 1, 20, 0, numContr);
			case 12 :
				return new DController();
			default :
				throw new IllegalArgumentException(
						"Unexpected value: " + numContr);
		}
	}
	@Override
	protected void msgIn(String data_S, String string_H) {
		String bytes[] = string_H.split(" ");
		char array_h[] = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			array_h[i] = (char) Integer.parseInt(bytes[i], 16);
		}
		if (bytes.length < 8) {
			return;
		}
		int byteIO = array_h[3];
		int kk = array_h[2] << 8;
		byteIO |= kk;
		int nReg = array_h[4] << 8 | array_h[5];
		print("Адрес: " + (byte) array_h[0]);
		if (array_h[1] == 0x03) {
			print(", функция: 0x03, первый байт: 0x"
					+ toStrWithZero(Integer.toHexString(byteIO), 4)
					+ ", хотим прочесть: 0x");
		} else if (array_h[1] == 0x10) {
			print(", функция: 0x10, адрес записи: 0x"
					+ toStrWithZero(Integer.toHexString(byteIO), 4)
					+ ", записываемых байт: 0x");
		}
		print(toStrWithZero(Integer.toHexString(nReg), 4) + " байт");

		int[] crc = MyMatch.calculateCRC(array_h, 0, array_h.length - 2);
		if (crc[1] == array_h[array_h.length - 2]
				&& crc[0] == array_h[array_h.length - 1]) {
			println(". crc совпал");
		} else {
			println(".Ожидали " + toStrWithZero(Integer.toHexString(crc[1]), 2)
					+ " " + toStrWithZero(Integer.toHexString(crc[0]), 2)
					+ ", а получили "
					+ toStrWithZero(
							Integer.toHexString(array_h[array_h.length - 2]), 2)
					+ " "
					+ toStrWithZero(
							Integer.toHexString(array_h[array_h.length - 1]),
							2));
			println("In Hex " + string_H);
			return;
		}
		if (array_h[1] == 0x03) {
			if (byteIO == 0x0409 && mode == Modes.getEvent) {
				println("Запросили событие из журнала ");
				byte ans[] = new byte[29];
				ans[28] = 0; // Номер события
				writeModbus((byte) array_h[0], (byte) 0x03, ans);
				mode = Modes.ReadEvent;
			} else {
				for (Controller[] i : deviseVector) {
					if (i[0] instanceof AController) {
						if (((AController) i[0]).Offset == array_h[3] + 1) {
							println("Запросили "
									+ ((AController) i[0]).getName());
							writeModbus((byte) array_h[0], (byte) 0x03,
									((AController) i[0]).getVal());
						}
					} else if (i[0] instanceof DController) {
						if (array_h[2] == 0x01 && array_h[3] == 0x4F) {
							println("Запросили дискретные данные");
							writeModbus((byte) array_h[0], (byte) 0x03,
									((DController) i[0]).getVal());
						}
					}
				}
				mode = Modes.None;
			}
		} else if (array_h[1] == 0x10) {
			if (byteIO == 0x0130) {
				if (nReg == 0x0001 && array_h[6] == 0x01 && array_h[7] == 0x00
						&& array_h[8] == 0x01) {
					println("Заполнение буфера запроса к панели PowerWizard"
							+ " - запись '1' в регистр '0130'h для опроса всех событий");
				} else {
					println("Фигня полнейшая, но перешли в режим выдачи событий");
				}
				byte ans[] = {0x01};
				writeModbus((byte) array_h[0], (byte) 0x03, ans);
				println("Перешли в режим выдачи событий");
				mode = Modes.ReadEvent;
			} else if (byteIO == 0x0409 && mode == Modes.ReadEvent) {
				println("Запросили событие из журнала № " + (byte) array_h[8]);
				byte ans[] = {0x01};
				writeModbus((byte) array_h[0], (byte) 0x03, ans);
				println("Перешли в режим выдачи события № "
						+ (byte) array_h[8]);
				mode = Modes.getEvent;
			} else {
				mode = Modes.None;
				println("Не понятно что");
				println("In Hex " + string_H);
			}
		} else {
			mode = Modes.None;
			println("In Hex " + string_H);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	@Override
	protected void newInitComponents() {
	}// </editor-fold>//GEN-END:initComponents

	@Override
	protected void newStep() {
		regenerateControllerValue();
		/*
		 * for (Controller[] i : DataDriverVector) { if (i[0] instanceof
		 * AController) { println("Запросили " + ((AController)
		 * i[0]).getName()); writeModbus((byte) 0x03, (byte) 0x03,
		 * ((AController) i[0]).getVal()); } else if (i[0] instanceof
		 * DController) { println("Запросили дискретные данные");
		 * writeModbus((byte) 0x03, (byte) 0x03, ((DController) i[0]).getVal());
		 * } }
		 */
	}

	private void writeModbus(byte adr, byte fun, byte[] ans) {
		byte array_ans[] = new byte[ans.length + 1 + 1 + 2];
		array_ans[0] = adr;
		array_ans[1] = fun;
		for (int i = 0; i < ans.length; i++) {
			array_ans[i + 2] = ans[i];
		}
		int[] crc = MyMatch.calculateCRC(array_ans, 0, array_ans.length - 2);
		array_ans[array_ans.length - 1] = (byte) crc[0];
		array_ans[array_ans.length - 2] = (byte) crc[1];
		writeBytes(array_ans);
	}
}
