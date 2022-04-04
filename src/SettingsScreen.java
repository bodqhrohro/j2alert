import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.Vector;
import java.io.*;

public class SettingsScreen extends Form implements CommandListener {
	private Form parentForm;
	private UkrZenApi api;
	private LocalStorage localStorage;
	private int[] selectedRegions;

	private Command cmdOK;
	private Command cmdBack;

	public SettingsScreen(Form parentForm, UkrZenApi api, LocalStorage localStorage, int[] selectedRegions) {
		super("");

		this.parentForm = parentForm;
		this.api = api;
		this.localStorage = localStorage;
		this.selectedRegions = selectedRegions;
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
			Vector regionIndices = regions[i+4];
			int selected = selectedRegions[i];
			for (int j = 0; j < region.size(); j++) {
				cg.append((String)region.elementAt(j), null);
				if (((Integer)regionIndices.elementAt(j)).intValue() == selected) {
					cg.setSelectedIndex(j+1, true);
				}
			}

			this.append(cg);
		}
	}

	private int[] getSelectedIndices() {
		int[] indices = {-1, -1, -1, -1};

		Vector[] regions = api.getRegions();

		for (int i = 0; i < 4; i++) {
			try {
				ChoiceGroup cg = (ChoiceGroup)this.get(i);
				int selected = cg.getSelectedIndex();
				if (selected > 0) { // 0 and -1 mean -1
					Vector regionIndices = regions[4 + i];
					indices[i] = ((Integer)regionIndices.elementAt(selected - 1)).intValue();
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		return indices;
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == cmdOK) {
			try {
				localStorage.saveRegions(this.getSelectedIndices());

				((AlertScreen)parentForm).switchTo(parentForm);
			} catch (RecordStoreFullException e) {
				this.setTicker(new Ticker("Сховище переповнене!"));
			} catch (RecordStoreException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		} else if (command == cmdBack) {
			((AlertScreen)parentForm).switchTo(parentForm);
		}
	}
}
