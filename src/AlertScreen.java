import javax.microedition.lcdui.*;
import java.util.Vector;
import java.io.*;

public class AlertScreen extends Form implements CommandListener {
	private Ticker ticker;

	private UkrZenApi api;

	public void updateData() {
		api = new UkrZenApi();
		api.update();
		try {
			Vector[] result = api.getRegions();

			for (int i = 0; i < 4; i++) {
				String label = "";
				switch (i) {
				case 0:
					label = "Область";
				break;
				case 1:
					label = "Район";
				break;
				case 2:
					label = "Громада";
				break;
				case 3:
					label = "Місто";
				break;
				}

				ChoiceGroup cg = new ChoiceGroup(label, ChoiceGroup.POPUP);
				if (i == 3) {
					cg.append("Ніяке", null);
				} else if (i == 1) {
					cg.append("Ніякий", null);
				} else {
					cg.append("Ніяка", null);
				}

				for (int j = 0; j < result[i].size(); j+=2) {
					cg.append((String)result[i].elementAt(j), null);
				}

				this.append(cg);
			}

			ticker.setString("");
		} catch (IOException e) {
			e.printStackTrace();
			ticker.setString("Помилка запиту регіонів");
		}
	}

	public AlertScreen() {
		super("");
	}

	public void start(){
		ticker = new Ticker("Оновлення даних...");
		this.setTicker(ticker);

		this.addCommand(new Command("Налаштування", Command.ITEM, 1));
		this.addCommand(new Command("Вийти", Command.EXIT, 1));

		updateData();
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
	}
}
