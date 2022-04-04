import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.Vector;
import java.io.*;

public class AlertScreen extends Form implements CommandListener {
	private Ticker ticker;

	private UkrZenApi api;
	private LocalStorage localStorage;
	private ActiveUpdater activeUpdater;

	private Command cmdSettings;
	private Command cmdExit;

	private J2Alert midlet;

	private SettingsScreen settingsScreen;

	public void updateData() {
		api.update();
		try {
			api.fetchRegions();

			ticker.setString("");

			this.append(new StringItem("Регіони завантажено.\n", ""));
		} catch (IOException e) {
			e.printStackTrace();
			ticker.setString("Помилка запиту регіонів");
		}
	}

	public AlertScreen(J2Alert midlet) {
		super("");
		this.midlet = midlet;
		api = new UkrZenApi();
		localStorage = new LocalStorage();
	}

	public void start() {
		ticker = new Ticker("Оновлення даних...");
		this.setTicker(ticker);

		cmdSettings = new Command("Налаштування", Command.ITEM, 1);
		cmdExit = new Command("Вийти", Command.EXIT, 1);
		this.addCommand(cmdSettings);
		this.addCommand(cmdExit);

		int[] selectedRegions = {-1,-1,-1,-1};
		try {
			selectedRegions = localStorage.loadRegions();
		} catch (RecordStoreException e) {
			this.append("Збережених налаштувань не знайдено. Налаштуйте регіони, у яких знаходитесь, для отримання сповіщень.");
		}

		settingsScreen = new SettingsScreen(this, api, localStorage, selectedRegions);
		settingsScreen.setCommandListener(settingsScreen);

		updateData();

		settingsScreen.start();

		activeUpdater = new ActiveUpdater(api);
		activeUpdater.start();
	}

	public void switchTo(Form screen) {
		Display.getDisplay(midlet).setCurrent(screen);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == cmdSettings) {
			switchTo(settingsScreen);
		} else if (command == cmdExit) {
			midlet.destroyApp(true);
			midlet.notifyDestroyed();
		}
	}
}
