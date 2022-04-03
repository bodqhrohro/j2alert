import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import java.util.Vector;
import java.io.*;

public class AlertScreen extends Form implements CommandListener {
	private Ticker ticker;

	private UkrZenApi api;

	private Command cmdSettings;
	private Command cmdExit;

	private J2Alert midlet;

	private SettingsScreen settingsScreen;

	public void updateData() {
		api.update();
		try {
			api.fetchRegions();

			ticker.setString("");

			this.append(new StringItem("Регіони завантажено.\n", "Налаштуйте регіони, у яких знаходитесь, для отримання сповіщень"));
		} catch (IOException e) {
			e.printStackTrace();
			ticker.setString("Помилка запиту регіонів");
		}
	}

	public AlertScreen(J2Alert midlet) {
		super("");
		this.midlet = midlet;
		api = new UkrZenApi();
	}

	public void start() {
		ticker = new Ticker("Оновлення даних...");
		this.setTicker(ticker);

		cmdSettings = new Command("Налаштування", Command.ITEM, 1);
		cmdExit = new Command("Вийти", Command.EXIT, 1);
		this.addCommand(cmdSettings);
		this.addCommand(cmdExit);

		settingsScreen = new SettingsScreen(this, this.api);
		settingsScreen.setCommandListener(settingsScreen);

		updateData();

		settingsScreen.start();
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
