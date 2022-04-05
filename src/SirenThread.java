import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class SirenThread extends Thread {
	public static final int STATE_SIREN = 0;
	public static final int STATE_SILENT = 1;
	public static final int STATE_ERROR = 2;

	private int state = STATE_SILENT;
	private int prevState = STATE_SILENT;

	SirenThread() {
	}

	public void setState(int state) {
		this.prevState = this.state;
		this.state = state;
	}

	public void run() {
		for (;;) {
			try {
				try {
					if (state == STATE_SIREN) {
						Manager.playTone(100, 1000, 100);
					} else if (state == STATE_SILENT && prevState == STATE_SIREN) {
						Manager.playTone(100, 200, 100);
						Thread.sleep(200);
						Manager.playTone(100, 200, 100);
						Thread.sleep(200);
						Manager.playTone(100, 200, 100);
						Thread.sleep(200);
						Manager.playTone(100, 500, 100);
					} else if (state == STATE_ERROR) {
						Manager.playTone(100, 100, 100);
					}
				} catch (MediaException e) {
					e.printStackTrace();
				}

				setState(state);

				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
