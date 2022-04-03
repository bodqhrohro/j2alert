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

			this.append(new StringItem("Регіони завантажено", "Налаштуйте регіони, у яких знаходитесь, для отримання сповіщень"));
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

		settingsScreen = new SettingsScreen(this.api);
		settingsScreen.setCommandListener(settingsScreen);

		updateData();

		settingsScreen.start();
	}

	public void paint(Graphics g) {
	}

	protected void keyPressed(int keyCode) {
	}
	protected void keyReleased(int keyCode) {
	}
	protected void keyRepeated(int keyCode) {
	}
	protected void pointerDragged(int x, int y) {
	}
	protected void pointerPressed(int x, int y) {
	}
	protected void pointerReleased(int x, int y) {
	}
	public void commandAction(Command command, Displayable displayable) {
		if (command == cmdSettings) {
			Display.getDisplay(midlet).setCurrent(settingsScreen);
		} else if (command == cmdExit) {
			midlet.destroyApp(true);
			midlet.notifyDestroyed();
		}
	}
}
