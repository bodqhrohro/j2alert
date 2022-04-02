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

	private Vector cityIds;
	private Vector hromadaIds;
	private Vector oblastIds;
	private Vector raionIds;

	UkrZenApi() {
		readConfig();
	}

	private void readConfig() {
	}

	public void fetchRegions() throws IOException {
		try {
			String res = fetchString(LOCATIONS_URI, 16348);

			cities = new Vector();
			hromadas = new Vector();
			oblasts = new Vector();
			raions = new Vector();

			cityIds = new Vector();
			hromadaIds = new Vector();
			oblastIds = new Vector();
			raionIds = new Vector();

			int beginQuoteI = 1;
			int endQuoteI = 1;
			int state = 0; // 0: title => 1: "title" => 2: type => 3: "type" => 0: ...
			String lastRegion = "";

			for (int i = 0; i < 10000; i++) { // a sane limit to prevent an endless loop
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
						cityIds.addElement(no);
					} else if (type.equals("hromada")) {
						hromadas.addElement(lastRegion);
						hromadaIds.addElement(no);
					} else if (type.equals("oblast")) {
						oblasts.addElement(lastRegion);
						oblastIds.addElement(no);
					} else if (type.equals("raion")) {
						raions.addElement(lastRegion);
						raionIds.addElement(no);
					}
				}

				state++;
				if (state >= 4) {
					state = 0;
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public Vector[] getRegions() {
		Vector[] regions = {
			oblasts, raions, hromadas, cities,
			oblastIds, raionIds, hromadaIds, cityIds
		};
		return regions;
	}

	private String fetchString(String uri, int bufSize) throws IOException {
		try {
			HttpConnection hc = (HttpConnection)Connector.open(uri);
			InputStream is = hc.openInputStream();
			Reader r = new InputStreamReader(is, "UTF-8");

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
