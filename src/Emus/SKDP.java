package Emus;

import java.util.ArrayList;

import MyMatch.EmusDeff;
import MyMatch.MyMatch;
import MyMatch.Setings;

public class SKDP extends EmusDeff {
	
	public SKDP(Setings se) {
		super(se, "Эмулятор СКДП - системы контроля допуска в помещения", 5000);
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
		int id_trans = Byte.toUnsignedInt(array_h[0])*0x100 +  Byte.toUnsignedInt(array_h[1]);
		int lenght =  Byte.toUnsignedInt(array_h[5]);
		int adr =  Byte.toUnsignedInt(array_h[6]);
		int fun =  Byte.toUnsignedInt(array_h[7]);
		int adr_reg =  Byte.toUnsignedInt(array_h[8])*0x100 +  Byte.toUnsignedInt(array_h[9]);
		int count_reg =  Byte.toUnsignedInt(array_h[10])*0x100 +  Byte.toUnsignedInt(array_h[11]);
		println("Пришло сообщение: транзакция " + id_trans + ", пакет длиной " 
				+ lenght + " для " + adr + " с функцией " + fun + " с регистра " + adr_reg + " длинной " + count_reg + ". Всё: " + string_H);

		byte array_out[] = new byte[9+count_reg*2];
		array_out[0] = array_h[0];
		array_out[1] = array_h[1];
		array_out[5] = (byte) (array_out.length - 5);
		array_out[6] = (byte) adr;
		array_out[7] = (byte) fun;
		array_out[8] = (byte) (array_out.length - 8);
		
		if(count_reg == 1) {
			array_out[9 + 0*2 + 0] = 0x00;
			array_out[9 + 0*2 + 1] = 0x00;
		} else {
			for(int i = 0 ; i < count_reg ; i++) {
				array_out[9 + i*2 + 0] = 0;
				array_out[9 + i*2 + 1] = 1;
			}
		}
		
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
