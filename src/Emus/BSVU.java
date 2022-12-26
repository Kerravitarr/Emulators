package Emus;

import java.util.ArrayList;

import MyMatch.EmusDeff;
import MyMatch.MyMatch;
import MyMatch.Setings;

public class BSVU extends EmusDeff {
	
	public BSVU(Setings se) {
		super(se, "Эмулятор шкафов ОВЕН", 5000);
		//addContr(1, 1, 1, 8);
	}

	@Override
	protected void firstStep() {

	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		return null;
	}

	//Первый байт тела пакета
	static final int firstByteBody = 7;
	protected void msgIn(String data_S, String string_H) {
		byte array_h[] = hexToStr(string_H);
		if(array_h.length != 8 && array_h.length != 16) {
			println("Не правильная длинна пакета, 8 (16) байт. Пакет: " + string_H);
			return;
		}
		int adr = array_h[0];
		int[] crc = MyMatch.calculateCRC(array_h, 0, array_h.length - 2);
		if((byte)crc[1] != (byte)array_h[array_h.length - 1] || (byte)crc[0] != (byte)array_h[array_h.length - 2]) {
			println("Не совпал CRC! Пришло 0х" + toHexStrWithZero(array_h[array_h.length - 2], 2)
					+ " 0х" + toHexStrWithZero(array_h[array_h.length - 1], 2)
					+ ", а ожидалось 0х" + toHexStrWithZero(crc[1], 2)
					+ " 0х" + toHexStrWithZero(crc[0], 2));
			println("Пакет: " + string_H);
			return;
		}
		int firstadr = array_h[2]*0x100 + array_h[3];
		int count = array_h[4]*0x100 + array_h[5];
		
		if (array_h[1] == 16) {
			// Передача временной метки
			if (adr == 0)
				print("Широковещательная передача ");
			else
				print("Передача для " + adr + " ");
			println(" функция " + array_h[1] + " первый регистр - " + firstadr + " число регистров - " + count);
			print("Передача временной метки: ");
			int a = (array_h[6] << 8)&0xFF00;
			int b = array_h[7] & 0xFF;
			print("Год " + (a+b) + " ");
			print("Месяц " + (array_h[8]) + " ");
			print("День " + (array_h[9]) + " ");
			print("Часы " + (array_h[10]) + " ");
			print("Минуты " + (array_h[11]) + " ");
			println("Секунды " + (array_h[12]) + " ");
			return;
		}
		
		if(array_h[1] != 4) {
			println("Не верная функция, должна быть 4. Пакет: " + string_H);
			return;
		}
		if(count > 255) {
			println("Запрошенно слишком много регистров. Пакет: " + string_H);
			return;
		}
		
		if((adr - 1) % 10 == 0) {
			print("Базовый адрес (адрес каркаса) " + adr);
		} else if((adr - 1) % 10 == 1) {
			print("Базовый адрес " + (((adr-1)/10)*10+1) + " данные БСВУ");
		} else {
			print("Базовый адрес " + (((adr-1)/10)*10+1) + " данные устройства " + ((adr - 1) % 10 - 1));
		}
		
		print(" функция " + array_h[1] + " первый регистр - " + firstadr + " число регистров - " + count);
		println();
		
		byte array_out[] = new byte[5+count*2];
		array_out[0] = (byte) adr;
		array_out[1] = array_h[1];
		array_out[2] = (byte) count;
		
		for(int i = 0 ; i < count ; i++) {
			array_out[3 + i*2 + 0] = 0;
			array_out[3 + i*2 + 1] = 1;
		}
		crc = MyMatch.calculateCRC(array_out, 0, array_out.length - 2);
		array_out[array_out.length - 2] = (byte)crc[0];
		array_out[array_out.length - 1] = (byte)crc[1];
		
		writeBytes(array_out);
	}

	@Override
	protected void newInitComponents() {


	}

	@Override
	protected void newStep() {
		regenerateControllerValue();
	}

}
