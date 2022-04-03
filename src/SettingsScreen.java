import javax.microedition.lcdui.*;
import java.util.Vector;
import java.io.*;

public class SettingsScreen extends Form implements CommandListener {
	private UkrZenApi api;

	private Command cmdOK;
	private Command cmdBack;

	public SettingsScreen(UkrZenApi api) {
		super("");

		this.api = api;
	}

	public void start() {
		cmdOK = new Command("OK", Command.OK, 1);
		cmdBack = new Command("Назад", Command.BACK, 1);
		this.addCommand(cmdOK);
		this.addCommand(cmdBack);

		Vector[] regions = api.getRegions();

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

			Vector region = regions[i];
			for (int j = 0; j < region.size(); j++) {
				cg.append((String)region.elementAt(j), null);
			}

			this.append(cg);
		}
	}

	public void commandAction(Command command, Displayable displayable) {
	}
}
