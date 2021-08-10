package MyMatch;

import java.util.ArrayList;

public abstract class EmusEventProducer {
	private ArrayList<EmusListener> listeners = new ArrayList<>();


	final public void addListener(EmusListener listener) {
		listeners.add(listener);
	}

	final public void removeListener(EmusListener listener) {
		listeners.remove(listener);
	}
	
	/**Отправить событие в поток
	 * @param e - событие
	 */
	protected void dispatchEvent(EmusEvent e){
		for(EmusListener listener : listeners){
			listener.onServerChanged(e);
		}
	}
}
