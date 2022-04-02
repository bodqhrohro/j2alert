import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;

public class UkrZenApi {
	private static final String LOCATIONS_URI = "http://nedoschechko.undo.it:11113/locations.json";
	private static final String ACTIVE_URI = "http://nedoschechko.undo.it:11113/active.mp";

	private Vector cities;
	private Vector hromadas;
	private Vector oblasts;
	private Vector raions;

	UkrZenApi() {
		readConfig();
	}

	private void readConfig() {
	}

	public Vector[] getRegions() throws IOException {
		try {
			String res = fetchString(LOCATIONS_URI, 16348);

			cities = new Vector();
			hromadas = new Vector();
			oblasts = new Vector();
			raions = new Vector();

			int beginQuoteI = 1;
			int endQuoteI = 1;
			int state = 0; // 0: title => 1: "title" => 2: type => 3: "type" => 0: ...
			String lastRegion = "";

			for (int i = 0; i < 800; i++) {
				beginQuoteI = res.indexOf('"', endQuoteI+1);
				endQuoteI = res.indexOf('"', beginQuoteI+1);
				if (beginQuoteI == -1 || endQuoteI == -1) {
					break;
				}

				if (state == 1) {
					lastRegion = res.substring(beginQuoteI+1, endQuoteI);
				} else if (state == 3) {
					Integer no = new Integer(i/4);
					String type = res.substring(beginQuoteI+1, endQuoteI);
					if (type.equals("city")) {
						cities.addElement(lastRegion);
						cities.addElement(no);
					} else if (type.equals("hromada")) {
						hromadas.addElement(lastRegion);
						hromadas.addElement(no);
					} else if (type.equals("oblast")) {
						oblasts.addElement(lastRegion);
						oblasts.addElement(no);
					} else if (type.equals("raion")) {
						raions.addElement(lastRegion);
						raions.addElement(no);
					}
				}

				state++;
				if (state >= 4) {
					state = 0;
				}
			}

			Vector[] regions = {oblasts, raions, hromadas, cities};
			return regions;
		} catch (IOException e) {
			throw e;
		}
	}

	private String fetchString(String uri, int bufSize) throws IOException {
		try {
			HttpConnection hc = (HttpConnection)Connector.open(uri);
			InputStream is = hc.openInputStream();
			Reader r = new InputStreamReader(is);

			StringBuffer sbuf = new StringBuffer(bufSize);
			char[] buf = new char[128];
			int read;
			while ((read = r.read(buf, 0, buf.length)) != -1) {
				sbuf.append(buf, 0, read);
			}
			return sbuf.toString();
		} catch (IOException e) {
			throw e;
		}
	}

	public void update() {
	}
}
