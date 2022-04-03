import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class J2Alert extends MIDlet {
	public void startApp() {
		AlertScreen alert = new AlertScreen(this);
		alert.start();
		alert.setCommandListener(alert);
		Display.getDisplay(this).setCurrent(alert);
	}
	public void pauseApp() {
	}
	public void destroyApp(boolean unconditional) {
	}
}
