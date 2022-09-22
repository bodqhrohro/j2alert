import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.lcdui.Display;

public class SirenThread extends Thread {
	public static final int STATE_SIREN = 0;
	public static final int STATE_SILENT = 1;
	public static final int STATE_ERROR = 2;

	private static final byte C5 = (byte)(ToneControl.C4 + 12);
	private static final byte[] SEQUENCE_SIREN = {
		ToneControl.VERSION, 1,
		ToneControl.TEMPO, 30,
		ToneControl.SET_VOLUME, 100,
		C5, 64
	};
	private static final byte[] SEQUENCE_SIREN_END = {
		ToneControl.VERSION, 1,
		ToneControl.TEMPO, 30,
		ToneControl.SET_VOLUME, 100,
		ToneControl.BLOCK_START, 0,
		C5, 4,
		ToneControl.SILENCE, 4,
		ToneControl.BLOCK_END, 0,
		ToneControl.PLAY_BLOCK, 0,
		ToneControl.PLAY_BLOCK, 0,
		ToneControl.PLAY_BLOCK, 0,
		C5, 16,
	};
	private static final byte[] SEQUENCE_ERROR = {
		ToneControl.VERSION, 1,
		ToneControl.TEMPO, 30,
		ToneControl.SET_VOLUME, 100,
		C5, 4
	};

	private int state = STATE_SILENT;
	private int prevState = STATE_SILENT;

	private Display display;

	SirenThread(Display display) {
		this.display = display;
	}

	private Player getPlayer(int state) {
		Player player = null;

		try {
			String name = "";
			if (state == STATE_SIREN) {
				name = "/res/siren.mid";
			} else if (state == STATE_SILENT) {
				name = "/res/siren_end.mid";
			} else if (state == STATE_ERROR) {
				name = "/res/error.mid";
			}

			InputStream is = getClass().getResourceAsStream(name);
			player = Manager.createPlayer(is, "audio/midi");
			player.realize();
			if (player != null) {
				VolumeControl vc = (VolumeControl)player.getControl("VolumeControl");
				vc.setLevel(100);

				player.start();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (player != null) {
			return player;
		}

		try {
			player = Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
			player.realize();
			if (player != null) {
				VolumeControl vc = (VolumeControl)player.getControl("VolumeControl");
				vc.setLevel(100);
				ToneControl synth = (ToneControl)player.getControl("ToneControl");
				if (state == STATE_SIREN) {
					synth.setSequence(SEQUENCE_SIREN);
				} else if (state == STATE_SILENT) {
					synth.setSequence(SEQUENCE_SIREN_END);
				} else if (state == STATE_ERROR) {
					synth.setSequence(SEQUENCE_ERROR);
				}

				player.start();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return player;
	}

	public void setState(int state) {
		this.prevState = this.state;
		this.state = state;
	}

	public void run() {
		for (;;) {
			try {
				Player player = null;
				if (state == STATE_SIREN) {
					player = getPlayer(STATE_SIREN);
					display.vibrate(500);
				} else if (state == STATE_SILENT && prevState == STATE_SIREN) {
					player = getPlayer(STATE_SILENT);
					display.vibrate(1000);
					Thread.sleep(2000);
				} else if (state == STATE_ERROR) {
					player = getPlayer(STATE_ERROR);
					display.vibrate(100);
					Thread.sleep(1000);
				}

				setState(state);

				Thread.sleep(1000);
				if (player != null) {
					player.close();
				}
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
