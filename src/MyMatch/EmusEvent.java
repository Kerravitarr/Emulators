package MyMatch;

import java.util.EventObject;

public class EmusEvent extends EventObject {
	private static final long serialVersionUID = -6326247162271636387L;
	private String message;
	private Type _tp;
	
	public static enum Type {
		PRINT,NON,COM_IN_S,PRINTLN,COM_IN_X
	}
	public EmusEvent(String message, Type tp) {
		this(new String(), message,tp);
	}
	public String getMessage() {
		return message;
	}

	private EmusEvent(Object source, String message, Type tp) {
		super(source);
		this.message = message;
		_tp = tp;
	}
	public Type get_Type() {
		return _tp;
	}

}
