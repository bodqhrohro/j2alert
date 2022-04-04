import java.io.IOException;

public class ActiveUpdater extends Thread {
	UkrZenApi api;

	ActiveUpdater(UkrZenApi api) {
		this.api = api;
	}

	public void run() {
		for (;;) {
			try {
				api.fetchActive();
			} catch(IOException e) {
				e.printStackTrace();
			}

			Vector active = api.getActive();
			System.out.println(active);

			try {
				Thread.sleep(30000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
