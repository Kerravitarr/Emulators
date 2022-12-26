package Emus;

import java.util.ArrayList;

import MyMatch.EmusDeff;
import MyMatch.MyMatch;
import MyMatch.Setings;

public class IMSI extends EmusDeff {
	private class Controller extends EmusDeff.Controller{
		int count = 0;
		byte chanellGroupe = (byte) 0x05;
		byte chanellActiv = (byte) 0x0F;
		long[] norm1 = new long[8];
		long[] norm2 = new long[8];
		public Controller() {
			super(100_000, "ИМСИ-8А");
		}

		@Override
		protected String getStrToBoard() {
			int modul = getModule(val)&0xFF;
			int exp = getExp(val)&0xFF;
			long val = (long) (modul * 10 * Math.pow(2, exp));
			return toBeautifulStr((val),9) + "Ом";
		}

		public byte[] standartRet() {
			byte msg[] = new byte[50];
			msg[0] = 0x4B; //Тип модуля
			msg[1] = 0x20; //Код команды
			msg[2] = 0x00; //Всё исправно, без отказов
			msg[3] = chanellActiv; //Таблица подключения каналов (1-8)
			msg[4] = 0x00; //Таблица подключения каналов (9-16) - всегда 0, для ИМСИ8
			msg[5] = chanellGroupe; //Таблица групп каналов (1-8)
			msg[6] = 0x00; //Таблица групп каналов (9-16) - всегда 0, для ИМСИ8
			msg[7] = (byte) 0x00; //Таблица запрета измерений (1-8)
			msg[8] = 0x00; //Таблица запрета измерений (9-16) - всегда 0, для ИМСИ8
			msg[9] = (byte) count++; //Счетчик сообщений, инкрементируется  на еди-ницу в конце каждого цикла измерения сопро-тивления
			msg[10] = 0x00; //Номер канала А (А = от 1 до 16 включ., «Земля»)
			msg[11] = 0x00; //Номер канала Б (Б = от 1 до 16 включ., «Земля»)
			msg[12] = getExp(val); //Результат измерения, множитель
			msg[13] = getModule(val); //Результат измерения, модуль
			msg[14] = 0x00; //Номер канала А (А = от 1 до 16 включ., «Земля»)
			msg[15] = 0x00; //Номер канала Б (Б = от 1 до 16 включ., «Земля»)
			msg[16] = 0x00; //Номер канала А (А = от 1 до 16 включ., «Земля»)
			msg[17] = 0x00; //Номер канала Б (Б = от 1 до 16 включ., «Земля»)
			for(int i = 0 ; i < 8 ; i++) {
				msg[18+i*2] = getExp(norm1[i]); //Норма 1, канал i+1, множитель
				msg[19+i*2] = getModule(norm1[i]);//Норма 1, канал i+1, модуль
			}
			for(int i = 0 ; i < 8 ; i++) {
				msg[18+16+i*2] = getExp(norm2[i]); //Норма 2, канал i+1, множитель
				msg[19+16+i*2] = getModule(norm2[i]);//Норма 2, канал i+1, модуль
			}
			return msg;
		}
		private long binlog( long bits ) { // returns 0 for bits=0
		    int log = 0;
		    if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
		    if( bits >= 256 ) { bits >>>= 8; log += 8; }
		    if( bits >= 16  ) { bits >>>= 4; log += 4; }
		    if( bits >= 4   ) { bits >>>= 2; log += 2; }
		    return log + ( bits >>> 1 );
		}
		
		private byte getModule(long val) {
			return (byte) (((int) ((val / 10)
					/ Math.pow(2, binlog((val / 10) / 255) + 1))) & 0xFF);
		}
		private byte getExp(long val) {
			return (byte) (binlog((val/10)/255) + 1);
		}

		public byte[] lastVal() {
			ArrayList<Byte> msgAr = new ArrayList<>();
			msgAr.add((byte) 0x4B); // Тип модуля
			msgAr.add((byte) 0x26); // Код команды
			msgAr.add((byte) chanellActiv); // Таблица подключения каналов (1-8)
			msgAr.add((byte) 0x00); // Таблица подключения каналов (9-16) - всегда 0, для ИМСИ8
			msgAr.add((byte) chanellGroupe); // Таблица групп каналов (1-8)
			msgAr.add((byte) 0x00); // Таблица групп каналов (9-16) - всегда 0, для ИМСИ8

			byte groupe = 1;
			while (groupe != 0) {
				for (int startChanel = groupe; ((startChanel & chanellGroupe) == 0 || startChanel == groupe) && startChanel < (1 << 8); startChanel <<= 1) {
					if ((startChanel & chanellActiv) != 0) {
						for (int nextChanel = startChanel << 1; (nextChanel & chanellGroupe) == 0 && nextChanel < (1 << 8); nextChanel <<= 1) {
							if ((nextChanel & chanellActiv) != 0) {
								//println("Измерение между каналами" +( binlog(startChanel) +1 )+ " и " + (binlog(nextChanel)+1));
								msgAr.add((byte) getExp(val));// Результат измерения, множитель
								msgAr.add((byte) getModule(val));// Результат измерения, модуль
							}
						}
						//println("Измерение между каналами" +( binlog(startChanel) +1 )+ " и Земля");
						msgAr.add((byte) getExp(val));// Результат измерения, множитель
						msgAr.add((byte) getModule(val));// Результат измерения, модуль
					}
				}
				do {
					groupe = (byte) (groupe << 1);
				} while ((groupe & chanellGroupe) == 0 && groupe != 0);
			}

			int[] msg_int = new int[msgAr.size()];
			msg_int = msgAr.stream().mapToInt(i -> i).toArray();
			byte[] msg = new byte[msgAr.size()];
			for (int i = 0; i < msgAr.size(); i++)
				msg[i] = (byte) msg_int[i];
			return msg;
		}
	}
	
	
	public IMSI(Setings se) {
		super(se, "Эмулятор ИМСИ-8А", 5000);
		addContr(1, 1, 1, 8);
	}

	@Override
	protected void firstStep() {

	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		return new Controller();
	}

	//Первый байт тела пакета
	static final int firstByteBody = 7;
	protected void msgIn(String data_S, String string_H) {
		String bytes[] = string_H.split(" ");
		byte array_h[] = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			array_h[i] = (byte) Integer.parseInt(bytes[i], 16);
		}
		if(bytes.length < firstByteBody) {
			println("Байтов не хватило даже на заголовок, сообщение битое");
			println("Пакет: " + string_H);
			return;
		}
		if(array_h[0] != 0x77) {
			println("Не совпадает синхробайт. Пришло " + toHexStrWithZero(array_h[0],2) + " вместо 0х77" );
			println("Пакет: " + string_H);
			return;
		}
		int adr = array_h[1];
		int lenght = array_h[2];
		int[] crc = MyMatch.crc16_bypass(array_h, 0, 5); //CRC загловка
		if(lenght < 1) {
			println("Длина пакета меньше 1 символа ");
			println("Пакет: " + string_H);
			return;
		}
		if((byte)crc[0] != (byte)array_h[5] || (byte)crc[1] != (byte)array_h[6]) {
			println("Не совпал CRC заголовка! Пришло 0х" + toHexStrWithZero(array_h[6], 2)
					+ " 0х" + toHexStrWithZero(array_h[5], 2)
					+ ", а ожидалось 0х" + toHexStrWithZero(crc[1], 2)
					+ " 0х" + toHexStrWithZero(crc[0], 2));
			println("Пакет: " + string_H);
			return;
		}
		crc = MyMatch.crc16_bypass(array_h, firstByteBody, array_h.length-2); //CRC пакета
		if((byte)crc[0] != (byte)array_h[array_h.length-2] || (byte)crc[1] != (byte)array_h[array_h.length-1]) {
			println("Не совпал CRC пакета! Пришло 0х" + toHexStrWithZero(array_h[array_h.length-1], 2)
					+ " 0х" + toHexStrWithZero(array_h[array_h.length-2], 2)
					+ ", а ожидалось 0х" + toHexStrWithZero(crc[1], 2)
					+ " 0х" + toHexStrWithZero(crc[0], 2));
			println("Пакет: " + string_H);
			if(isExist(adr) || adr == 0x00) {
				byte error_msg[] = new byte[6+4];
				error_msg[0] = 0x77; //Синхробайт
				error_msg[1] = 0x00; //Размер тела сообщения без учета КС
				error_msg[2] = 0x02;
				error_msg[3] = (byte) adr; //Адрес ИМСИ
				crc = MyMatch.crc16_bypass(error_msg, 0, 4); //CRC пакета
				error_msg[4] = (byte) crc[0];
				error_msg[5] = (byte) crc[1];
				
				error_msg[5+1] = 0x4B; //Тип модуля
				error_msg[5+2] = (byte) 0x80; //Код ошибки - ошибка контрольной суммы тела команды
				crc = MyMatch.crc16_bypass(error_msg, 6, 8); //CRC пакета
				error_msg[5+3] = (byte) crc[0];
				error_msg[5+4] = (byte) crc[1];
				writeBytes(error_msg);
			}
			return;
		}
		if(array_h.length < firstByteBody + lenght + 2) {
			println("Длина тела пакета меньше, чем ожидалось. Тело пакета "
					+ (array_h.length - firstByteBody - 2) + " байт, а обещали "
					+ lenght);
			println("Пакет: " + string_H);
			if(isExist(adr) || adr == 0x00) {
				byte error_msg[] = new byte[6+4];
				error_msg[0] = 0x77; //Синхробайт
				error_msg[1] = 0x00; //Размер тела сообщения без учета КС
				error_msg[2] = 0x02; //Адрес ИМСИ
				error_msg[3] = (byte) adr; //Адрес ИМСИ
				crc = MyMatch.crc16_bypass(error_msg, 0, 4); //CRC пакета
				error_msg[4] = (byte) crc[0];
				error_msg[5] = (byte) crc[1];
				
				error_msg[5+1] = 0x4B; //Тип модуля
				error_msg[5+2] = (byte) 0x82; //Код ошибки - ошибка контрольной суммы тела команды
				crc = MyMatch.crc16_bypass(error_msg, 6, 8); //CRC пакета
				error_msg[5+3] = (byte) crc[0];
				error_msg[5+4] = (byte) crc[1];
				writeBytes(error_msg);
			}
			return;
		}
		int code = array_h[firstByteBody];
		
		println("Пришёл пакет. Адрес 0х" + toHexStrWithZero(adr, 2)
				+ ", код команды 0x" + toHexStrWithZero(code, 2));
		if (isExist(adr) || adr == 0x00) {
			switch (code) {
				case 0x20: standartRet(adr); break;
				case 0x22: setAdrOnNumber(array_h); break;
				case 0x26: lastVal(adr); break;
				case 0x27: setNorm(adr,array_h); break;
				default: println("Мы с таким кодом не работаем 0х" + toHexStrWithZero(code, 2)); break;
			}
		}
	}

	private void setNorm(int adr,byte[] array_h) {
		if(adr != 0x00) {
			Controller dev = (Controller) getDevise(adr)[0];
			dev.chanellActiv = array_h[firstByteBody+1];
			dev.chanellGroupe = array_h[firstByteBody+3];
			for(int i = 0; i < 8; i++) {
				dev.norm1[i] = (long)((array_h[firstByteBody+6+i*2]&0xFF)*10*Math.pow(2, array_h[firstByteBody+5+i*2]));
				dev.norm2[i] = (long)((array_h[firstByteBody+6+i*2]&0xFF)*10*Math.pow(2, array_h[firstByteBody+21+i*2]));
			}
			
		} else {
			println("Широковещательный запрос установки норм. Это может быть плохо, у всех нормы одно (не работает у меня)");
		}
	}

	private void lastVal(int adr) {
		if(adr != 0x00) {
			Controller dev = (Controller) getDevise(adr)[0];
			byte [] answer = dev.lastVal();
			byte msg[] = new byte[6+answer.length + 2];
			msg[0] = 0x77; //Синхробайт
			msg[1] = (byte) (answer.length&0xFF); //Размер тела сообщения без учета КС
			msg[2] = (byte) ((answer.length >> 8)&0xFF);
			msg[3] = (byte) adr; //Адрес ИМСИ
			int[] crc = MyMatch.crc16_bypass(msg, 0, 4); //CRC пакета
			msg[4] = (byte) crc[0];
			msg[5] = (byte) crc[1];
			for(int i = 0; i < answer.length; i++) {
				msg[5+1+i] = answer[i];
			}
			crc = MyMatch.crc16_bypass(msg, 6, 6+answer.length); //CRC пакета
			msg[msg.length-2] = (byte) crc[0];
			msg[msg.length-1] = (byte) crc[1];
			writeBytes(msg);
		} else {
			println("Широковещательный запрос последних данных, на линии неразбериха");
		}
	}

	private void setAdrOnNumber(byte[] array_h) {
		print("Команда установки адреса по заводскому номеру. Новый адрес: 0х" + toHexStrWithZero(array_h[firstByteBody+1],2) + " ");
		print("Заводской номер: " + ((array_h[firstByteBody+5]>>4)&0x0F) + ((array_h[firstByteBody+5])&0x0F));
		print(""+((array_h[firstByteBody+4]>>4)&0x0F) + ((array_h[firstByteBody+4])&0x0F));
		print(""+((array_h[firstByteBody+3]>>4)&0x0F) + ((array_h[firstByteBody+3])&0x0F));
		print(""+((array_h[firstByteBody+2]>>4)&0x0F) + ((array_h[firstByteBody+2])&0x0F));
		println(". Адрес установлен ((АХАХХАХАХАХА))");
	}

	private void standartRet(int adr) {
		if(adr != 0x00) {
			Controller dev = (Controller) getDevise(adr)[0];
			byte [] answer = dev.standartRet();
			byte msg[] = new byte[6+answer.length + 2];
			msg[0] = 0x77; //Синхробайт
			msg[1] = (byte) (answer.length&0xFF); //Размер тела сообщения без учета КС
			msg[2] = (byte) ((answer.length >> 8)&0xFF);
			msg[3] = (byte) adr; //Адрес ИМСИ
			int[] crc = MyMatch.crc16_bypass(msg, 0, 4); //CRC пакета
			msg[4] = (byte) crc[0];
			msg[5] = (byte) crc[1];
			for(int i = 0; i < answer.length; i++) {
				msg[5+1+i] = answer[i];
			}
			crc = MyMatch.crc16_bypass(msg, 6, 6+answer.length); //CRC пакета
			msg[msg.length-2] = (byte) crc[0];
			msg[msg.length-1] = (byte) crc[1];
			writeBytes(msg);
		} else {
			println("Широковещательный стандартный запрос, на линии неразбериха");
		}
	}

	@Override
	protected void newInitComponents() {


	}

	@Override
	protected void newStep() {
		regenerateControllerValue();
	}

}
