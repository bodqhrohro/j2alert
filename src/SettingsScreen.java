import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.Vector;
import java.io.*;

public class SettingsScreen extends Form implements CommandListener {
	private Form parentForm;
	private UkrZenApi api;

	private Command cmdOK;
	private Command cmdBack;

	public SettingsScreen(Form parentForm, UkrZenApi api) {
		super("");

		this.parentForm = parentForm;
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
				RecordStore aStore = RecordStore.openRecordStore("J2Alert_subscriptions", true);

				int[] indices = this.getSelectedIndices();
				byte[] bytes = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
				for (int i = 0; i < 4; i++) {
					int index = indices[i];
					bytes[i*4] = (byte)((index >> 24) & 0xff);
					bytes[i*4+1] = (byte)((index >> 16) & 0xff);
					bytes[i*4+2] = (byte)((index >> 8) & 0xff);
					bytes[i*4+3] = (byte)(index & 0xff);
				}

				if (aStore.getNumRecords() == 0) {
					aStore.addRecord(bytes, 0, bytes.length);
				} else {
					aStore.setRecord(1, bytes, 0, bytes.length);
				}

				aStore.closeRecordStore();

				((AlertScreen)parentForm).switchTo(parentForm);
			} catch (RecordStoreFullException e) {
				this.setTicker(new Ticker("Сховище переповнене!"));
			} catch (RecordStoreNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (RecordStoreNotOpenException e) {
				e.printStackTrace();
			} catch (InvalidRecordIDException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		} else if (command == cmdBack) {
			((AlertScreen)parentForm).switchTo(parentForm);
		}
	}
}
