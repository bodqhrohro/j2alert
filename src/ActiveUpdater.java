import java.io.IOException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Ticker;
import javax.microedition.lcdui.StringItem;
import java.util.Vector;

public class ActiveUpdater extends Thread {
	UkrZenApi api;
	Ticker ticker;
	LocalStorage localStorage;
	SirenThread sirenThread;
	AlertScreen alertScreen;

	ActiveUpdater(UkrZenApi api, Ticker ticker, LocalStorage localStorage, SirenThread sirenThread, AlertScreen alertScreen) {
		this.api = api;
		this.ticker = ticker;
		this.localStorage = localStorage;
		this.sirenThread = sirenThread;
		this.alertScreen = alertScreen;
	}

	public void run() {
		Vector[] regions = api.getRegions();

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
				if (isError) {
					ticker.setString("Мережева помилка");
					sirenThread.setState(SirenThread.STATE_ERROR);
				} else {
					int[] watchedIndices = localStorage.loadRegions();
					StringBuffer alertRegions = new StringBuffer();

					for (int i = 0; i < active.size(); i++) {
						int index = ((Integer)active.elementAt(i)).intValue();
						for (int u = 0; u < watchedIndices.length; u++) {
							if (index == watchedIndices[u]) {
								isAlert = true;
								break;
							}
						}
						for (int o = 0; o < 4; o++) {
							Vector region = regions[o];
							Vector regionIndices = regions[o+4];
							for (int j = 0; j < region.size(); j++) {
								if (((Integer)regionIndices.elementAt(j)).intValue() == index) {
									alertRegions.append("\n");
									alertRegions.append((String)region.elementAt(j));
								}
							}
						}
					}

					if (isAlert) {
						ticker.setString("Повітряна тривога!");
						sirenThread.setState(SirenThread.STATE_SIREN);
					} else {
						ticker.setString("");
						sirenThread.setState(SirenThread.STATE_SILENT);
					}
					alertScreen.deleteAll();
					if (alertRegions.length() > 0) {
						alertScreen.append(new StringItem("Зараз тривога у:", alertRegions.toString()));
					} else {
						alertScreen.append(new StringItem("Тривог немає!", ""));
					}
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
