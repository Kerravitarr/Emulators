package Emus;

import MyMatch.EmusDeff;
import MyMatch.Setings;

/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. */
/**
 *
 * @author Terran
 */
public class I2C extends EmusDeff {
	private class BME extends EmusDeff.Controller {

		public BME() {
			super(0, "Датчик погоды 0x76");
		}

		@Override
		protected String getStrToBoard() {
			return "";
		}

	}

	private class M0x2 extends EmusDeff.Controller {

		public M0x2(String adr) {
			super(0, "Раширитель порта " + adr);
		}

		@Override
		protected String getStrToBoard() {
			return "";
		}

	}

	private static final long serialVersionUID = 1L;

	// End of variables declaration//GEN-END:variables
	public I2C(Setings Se) {
		super(Se, "Эмулятор порта I2C", 1000);
		addContr(3, 0, 1, 1);
	}

	@Override
	protected void firstStep() {
	}

	@Override
	protected Controller getController(int numContr, int numPart) {
		switch (numContr) {
			case 0:
				return new M0x2("0x20");
			case 1:
				return new M0x2("0x21");
			case 2:
				return new BME();
		}
		return null;
	}

	@Override
	protected void msgIn(String data_S, String string_H) {
		String byts[] = string_H.split(" ");
		int num = Integer.parseInt(byts[0]);
		String deh;
		if ((num & 0x01) == 0) {
			deh = "Направили данные для ";
		} else {
			deh = "Попросили данные от ";
		}
		switch (num << 0x01) {
			case 0x20:
				deh += "внешнего расширителя порта ";
				break;
			case 0x21:
				deh += "внутреннего расширителя порта ";
				break;
			case 0x76:
				deh += "датчика атмосферного давления ";
				break;
			default:
				deh += "неизвестного устройства №0х" + Integer.toHexString(num) + " ";
				break;
		}
		if ((num & 0x01) == 0) {
			deh += ".Данные: ";
		} else {
			deh += ".Остальная часть сообщения: ";
		}

		for (int i = 1; i < byts.length; i++) {
			deh += "0х" + byts[i] + " ";
		}
		println(deh);
	};

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	@Override
	protected void newInitComponents() {
	}

	@Override
	protected void newStep() {

	}
}
