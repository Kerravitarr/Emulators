package Emus;

import java.util.ArrayList;

import MyMatch.EmusDeff;
import MyMatch.MyMatch;
import MyMatch.Setings;

public class ATIS extends EmusDeff {
	
	public ATIS(Setings se) {
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
		byte array_h[] = AsciiToStr(data_S);
		if(!validMsgMB(array_h)) return;

		int adr = array_h[0];
		int firstadr = array_h[2]*0x100 + array_h[3];
		int count = array_h[4]*0x100 + array_h[5];

		byte array_out[] = new byte[5+count*2];
		array_out[0] = (byte) adr;
		array_out[1] = array_h[1];
		array_out[2] = (byte) count;
		
		for(int i = 0 ; i < count ; i++) {
			array_out[3 + i*2 + 0] = 0;
			array_out[3 + i*2 + 1] = 1;
		}
		int []crc = MyMatch.calculateCRC(array_out, 0, array_out.length - 2);
		array_out[array_out.length - 2] = (byte)crc[0];
		array_out[array_out.length - 1] = (byte)crc[1];
		
		String stringOut = HexToAscii(array_out);
		
		writeString(stringOut);
	}

	@Override
	protected void newInitComponents() {


	}

	@Override
	protected void newStep() {
		regenerateControllerValue();
	}

}
