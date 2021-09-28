package Emus;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import MyMatch.EmusDeff;
import MyMatch.MyMatch;
import MyMatch.Setings;

public class MPC_EL extends EmusDeff {
	
	private abstract class Detector extends Controller {
		protected int StartBit;
	    protected int Del;

	    Detector(int StaBit, int StoBit, int Sd, String name) {
	    	super(Sd, name);
	    	StartBit = StaBit;
	        Del = StoBit - StartBit + 1;
	        val = 0;
	    }

	    public String toMPC() {
	        String out = "";
	        for (int i = 0; i < Del; i++) {
	            if (((val & (1 << i)) == (long) 0)) {
	                out += "0";
	            } else {
	                out += "1";
	            }
	        }
	        return out += "";
	    }
	}
	
	private class DetectorA extends Detector {

	    public DetectorA(int StaBit, int Sd) {
	        super(StaBit, StaBit + 10, Sd, "A" + (StaBit + 1));
	    }
	    
	    public String getStrToBoard() {
	        int num = StartBit + 1;
	        long val = super.val & 0x7FF;
	        if (num > 16384) {
	            num -= 16384;
	            return "а" + num + " = " + toBeautifulStr(val, 4);
	        } else {
	            return "А" + num + " = " + toBeautifulStr(val, 4);
	        }
	    }
	}
	
	private class DetectorD extends Detector {

	    public DetectorD(int StaBit, int Sd) {
	        super(StaBit, StaBit + 9, Sd, "D" + (StaBit+1));
	    }

	    public String getStrToBoard() {
	        int num = StartBit + 1;
	        String val = "";
	        /*if (seed == 0) {
	            val = Integer.toHexString((int) this.val) + "";
	        } else {
	            val = toMPC();
	        }*/
            val = toMPC();
	        if (num > 16384) {
	            num -= 16384;
	            return "ц" + num + " = " + val;
	        } else {
	            return "Ц" + num + " = " + val;
	        }
	    }
	}
	
	public MPC_EL(Setings se) {
		super(se, "Эмулятор МПЦ ЭЛ", 3000);
		addContr(4, 1, 10, 10);
	}

	@Override
	protected void firstStep() {
	}

	@Override
	protected Detector getController(int numContr, int numPart) {
		int num = numContr*10 + numPart;
		if (numContr < numKontr/2)
			return new DetectorD(num*10-1, 2);
		else
			return new DetectorA(num - numKontr/2 * 10, 200);
	}

	//Ни чего не ожидаем принять
	protected void msgIn(String data_S, String string_H) {}

	@Override
	protected void newInitComponents() {
	}

	private String getCRC(String msg) {
		int strLen = msg.length();
		int[] intArray;
		int crc = 0;
		int polynomial = 0x1021;

		if (strLen % 2 != 0) {
			msg = msg.substring(0, strLen - 1) + "0" + msg.substring(strLen - 1, strLen);
			strLen++;
		}

		intArray = new int[strLen / 2];
		int ctr = 0;
		for (int n = 0; n < strLen; n += 2) {
			intArray[ctr] = Integer.valueOf(msg.substring(n, n + 2), 16);
			ctr++;
		}

		// main code for computing the 16-bit CRC-CCITT
		for (int b : intArray) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (b >> 7 - i & 1) == 1;
				boolean c15 = (crc >> 15 & 1) == 1;
				crc <<= 1;
				if (c15 ^ bit) {
					crc ^= polynomial;
				}
			}
		}

		crc &= 0xFFFF;
		return Hex2Ascii(crc);
	}

	private String Hex2Ascii(int hex) {
		while (hex > 0xFFFF) {
			hex /= 2;
		}
		String out = "";
		int a = hex;
		for (int i = 0; i < 2; i++) {
			// out = Integer.toHexString((int) (a & 0x0F)) + out;
			out += Integer.toHexString(a >> 4 & 0x0F);
			out += Integer.toHexString(a & 0x0F);
			a >>= 8;
		}
		return out += "";
	}
	
	@Override
	protected void newStep() {
		regenerateControllerValue();
		// Отправляем запрос устройству
		String msg = "";
		// Записываем данные
		int sizeTC = 0;
		String msgbits = "";
		String data;
		String blok = "";
		// Забиваем данные
		for (Controller[] contrs : deviseVector) {
			for(Controller contr : contrs) {
				Detector i = (Detector) contr;
				if(contr instanceof DetectorD) sizeTC += i.Del;
				data = i.toMPC();
				do {
					blok = data.substring(0, 1) + blok;
					data = data.substring(1, data.length());
					if (blok.length() == 8) {
						msgbits += blok;
						blok = "";
					}
				} while (data.length() > 0);
				// msgbits += i.toMPC();
			}
		}
		for (int n = 0; n < msgbits.length() - 8; n += 8) {
			int val = Integer.valueOf(msgbits.substring(n, n + 8), 2);
			if (val > 15) {
				msg += Integer.toHexString(val);
			} else {
				msg += "0" + Integer.toHexString(val);
			}
		}
		int msgsize = msg.length();
		// Записываем СРС
		String crc = getCRC(msg);
		msg += crc;

		System.out.print("?B sizeTC(" + sizeTC + ") size(" + msgsize / 2 + ") crc(" + crc + ") -> ");
		msg = Hex2Ascii(sizeTC) + msg; // Размер данных ТС
		msg = Hex2Ascii(msgsize / 2) + msg; // Размер пакета
		msg = "?B" + msg; // Заголовок
		// Отправляем по кусочкам
		do {
			if (msg.length() > 100) {
				writeString(msg.substring(0, 100));
				// System.out.print(msg.substring(0, 100));
				msg = msg.substring(100, msg.length());
			} else {
				writeString(msg.substring(0, msg.length()));
				// System.out.print(msg.substring(0, msg.length()));
				msg = "";
			}
			try {Thread.sleep((long) (Math.random() * 2 + 1));} catch (InterruptedException e) {}
		} while (msg.length() > 0);
	}

}
