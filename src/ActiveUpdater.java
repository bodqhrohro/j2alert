import java.io.IOException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Ticker;
import java.util.Vector;

public class ActiveUpdater extends Thread {
	UkrZenApi api;
	Ticker ticker;
	LocalStorage localStorage;
	SirenThread sirenThread;

	ActiveUpdater(UkrZenApi api, Ticker ticker, LocalStorage localStorage, SirenThread sirenThread) {
		this.api = api;
		this.ticker = ticker;
		this.localStorage = localStorage;
		this.sirenThread = sirenThread;
	}

	public void run() {
		for (;;) {
			boolean isAlert = false;
			boolean isError = false;

			try {
				api.fetchActive();
			} catch(IOException e) {
				isError = true;
				e.printStackTrace();
			}

			Vector active = api.getActive();
			System.out.println(active);

			try {
				int[] watchedIndices = localStorage.loadRegions();

				for (int i = 0; i < active.size(); i++) {
					int index = ((Integer)active.elementAt(i)).intValue();
					for (int u = 0; u < watchedIndices.length; u++) {
						if (index == watchedIndices[u]) {
							isAlert = true;
							i = active.size(); // break from outer loop too
						}
					}
				}

				if (isError) {
					ticker.setString("Мережева помилка");
					sirenThread.setState(SirenThread.STATE_ERROR);
				} else if (isAlert) {
					ticker.setString("Повітряна тривога!");
					sirenThread.setState(SirenThread.STATE_SIREN);
				} else {
					ticker.setString("");
					sirenThread.setState(SirenThread.STATE_SILENT);
				}
			} catch (RecordStoreException e) {
				ticker.setString("Помилка RMS");
				sirenThread.setState(SirenThread.STATE_ERROR);
			}

			try {
				Thread.sleep(30000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
