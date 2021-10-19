package Emus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import MyMatch.EmusDeff;
import MyMatch.EmusEvent;
import MyMatch.MyMatch;
import MyMatch.Setings;

public class MPC_EL extends EmusDeff {
	
	/**
	 * Эта фигня нужна чтобы парсить HTML
	 * @author rjhjk
	 *
	 */
	private class ObjectInHTML extends Controller{
		int id;
		String Name;
		String Type;
		int bitStart;
		int bitStop;
		
		/**Число битов на датчик */
		int Del;
		
		ObjectInText typeNode = null;
		
		ObjectInHTML(Node id, Node Name,Node Type,Node bitStart,Node bitStop){
	    	super(0, id+"");
			String id_ = id.childNodes().get(0).toString().replaceAll("\n", "").replaceAll(" ", "");
			this.id = Integer.parseInt(id_);
			this.Name = Name.childNodes().get(0).toString().replaceAll("\n", "").replaceAll(" ", "");
			this.Type = Type.childNodes().get(0).toString().replaceAll("\n", "").replaceAll(" ", "");
			String bitStart_ = bitStart.childNodes().get(0).toString().replaceAll("\n", "").replaceAll(" ", "");
			this.bitStart = Integer.parseInt(bitStart_);
			String bitStop_ = bitStop.childNodes().get(0).toString().replaceAll("\n", "").replaceAll(" ", "");
			this.bitStop = Integer.parseInt(bitStop_);
			Del = this.bitStop - this.bitStart + 1;
			seed = (int) (Math.random() * Del/2);// - DelSeed / 2;
			super.Name = this.Name;
		}
		
		public String toMPC() {
			String out = "";
			if (typeNode == null) {
				for (int i = 0; i < Del; i++) {
					if (((val & (1 << i)) == (long) 0)) {
						out += "0";
					} else {
						out += "1";
					}
				}
			} else {
				int del = typeNode.countD;
				for (int i = 0; i < del; i++) {
					if (((val & (1 << i)) == (long) 0)) {
						out += "0";
					} else {
						out += "1";
					}
				}
				for(Integer countA : typeNode.countA) {
					for (int i = 0; i < countA; i++) {
						if (((val & (1 << i)) == (long) 0)) {
							out += "0";
						} else {
							out += "1";
						}
					}
				}
			}
			return out;
		}
	    
	    public String getStrToBoard() {
	    	if(typeNode == null) {
		        String val = toMPC();
	            return "Ц" + id + " = " + val;
	    	} else {
	    		String ret = "A" + id + " = ";
	    		ret += "ц:";
				int del = typeNode.countD;
				for (int i = 0; i < del; i++) {
					if (((val & (1 << i)) == (long) 0)) {
						ret += "0";
					} else {
						ret += "1";
					}
				}
				del = 0;
				for(Integer countA : typeNode.countA) {
					ret += " a"+(del++)+":";
					int mask = (1 << countA) - 1;
					ret += toBeautifulStr((val&mask),(int) Math.ceil(Math.log10(mask)));
				}
				return ret;
	    	}
	    }
		
		public String toString() {
			return "N" + id + " Имя " + Name + " Тип " + Type + " бит начала " +bitStart + " бит конца " + bitStop;
		}
	}
	
	/**
	 * А вот тут уже объекты из текстового файла
	 * @author rjhjk
	 *
	 */
	private class ObjectInText{
		String name;
		int countD;
		ArrayList<Integer> countA = new ArrayList<>();
		
		public String toString() {
			String ret = "Объект " + name  + " дискретов " + countD;
			for(int i = 0 ; i < countA.size() ; i++)
				ret+= " А" + i + "=" + countA.get(i);
			return ret;
		}
	}
	/**
	 * Вектор всех объектов
	 */
	ArrayList<ObjectInHTML> objsH = new ArrayList<>();
	public MPC_EL(Setings se) {
		super(se, "Эмулятор МПЦ ЭЛ", 3000);
		
		ArrayList<ObjectInText> objsT = new ArrayList<>();
		try {
			File htmlFile = new File("MPC_EL_config.html");
			Document doc = Jsoup.parse(htmlFile, "UTF-8");
			printCild(doc.childNodes(),-4,objsH); //Cпускаемся по документу до 4 уровня
		} catch (IOException e) {e.printStackTrace();}
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("MPC_EL_config.txt"), "UTF-8"))) {
			String line = reader.readLine();
			String[] keyVal;
			while (line != null) {
				keyVal = line.split("\t");
				ObjectInText obj = new ObjectInText();
				obj.name = keyVal[0];
				obj.countD = Integer.parseInt(keyVal[1]);
				for(int i = 2 ; i < keyVal.length ; i++) {
					obj.countA.add(Integer.parseInt(keyVal[i]));
				}
				objsT.add(obj);
				System.out.println(obj);
				dispatchEvent(new EmusEvent(obj.toString(), EmusEvent.Type.PRINTLN));
				line = reader.readLine();
			}
		} catch (Exception ex) {
			dispatchEvent(new EmusEvent(ex.toString(), EmusEvent.Type.PRINTLN));
		}
		
		for(ObjectInText i : objsT) {
			for(ObjectInHTML h : objsH)
				if(h.Type.equals(i.name))
					h.typeNode = i;
		}
		
		addContr(objsH.size(), 1, 1, 1);
	}

	private void printCild(List<Node> childNodes, int tab, ArrayList<ObjectInHTML> objs) {
		if(childNodes == null) return;
		if (tab < 0) {
			for (Node i : childNodes) {
				if (i instanceof Element)
					printCild(i.childNodes(), tab + 1,objs);
			}
		} else {
			boolean isFirst = true; //Пропускаем заголовок таблицы
			for (Node i : childNodes) {
				if (i instanceof TextNode) continue;
				if(isFirst) {
					isFirst = false;
					continue;
				}
				List<Node> e = i.childNodes();
				ObjectInHTML obj = new ObjectInHTML(e.get(1),e.get(3),e.get(5),e.get(7),e.get(9));
				System.out.println(obj);
				dispatchEvent(new EmusEvent(obj.toString(), EmusEvent.Type.PRINTLN));
				objs.add(obj);
			}
		}
	}

	@Override
	protected void firstStep() {
	}

	@Override
	protected ObjectInHTML getController(int numDev, int numContrInDev) {
		ObjectInHTML obj = objsH.get(numDev);
		return objsH.get(numDev);
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
		hex &= 0xFFFF;
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
		int k = 0;
		// Забиваем данные
		for (Controller[] contrs : deviseVector) {
			ObjectInHTML i = (ObjectInHTML) contrs[0];
			if(i.typeNode == null) sizeTC += i.Del;
			k += i.Del;
			data = i.toMPC();
			do {
				blok += data.substring(0, 1);
				data = data.substring(1, data.length());
				if (blok.length() == 8) {
					msgbits += blok;
					blok = "";
				}
			} while (data.length() > 0);
			// msgbits += i.toMPC();
		}
		int dd = msgbits.length();
		while(!blok.isEmpty()) {
			k ++;
			blok += "0";
			if (blok.length() == 8) {
				msgbits += blok;
				blok = "";
			}
		}
		int dddd = msgbits.length();
		for (int n = 0; n < msgbits.length(); n += 8) {
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

		System.out.print("?B size(" + msgsize / 2 + ") sizeTC(" + sizeTC + ") crc(" + crc + ") -> ");
		dispatchEvent(new EmusEvent("?B size(" + msgsize / 2 + ") sizeTC(" + sizeTC + ") crc(" + crc + ") -> ", EmusEvent.Type.PRINT));
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
