import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;

public class UkrZenApi {
	private static final String LOCATIONS_URI = "http://nedoschechko.undo.it:11113/locations.json";
	private static final String ACTIVE_URI = "http://nedoschechko.undo.it:11113/active.mp";
	private static final int LIMIT = 128;

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
			HttpConnection hc = (HttpConnection)Connector.open(LOCATIONS_URI);
			InputStream is = hc.openInputStream();
			Reader r = new InputStreamReader(is, "UTF-8");

			cities = new Vector();
			hromadas = new Vector();
			oblasts = new Vector();
			raions = new Vector();

			cityIds = new Vector();
			hromadaIds = new Vector();
			oblastIds = new Vector();
			raionIds = new Vector();

			boolean inString = false;
			int state = 0; // 0: title => 1: "title" => 2: type => 3: "type" => 0: ...
			StringBuffer lastRegion = new StringBuffer(LIMIT);
			StringBuffer type = new StringBuffer();
			int idx = 0;

			int read;
			while ((read = r.read()) != -1) {
				if (read == 0x22) {
					if (inString) {
						if (state == 3) {
							Integer no = new Integer(idx);
							String strLastRegion = lastRegion.toString();
							String strType = type.toString();
							if (strType.equals("city")) {
								cities.addElement(strLastRegion);
								cityIds.addElement(no);
							} else if (strType.equals("hromada")) {
								hromadas.addElement(strLastRegion);
								hromadaIds.addElement(no);
							} else if (strType.equals("oblast")) {
								oblasts.addElement(strLastRegion);
								oblastIds.addElement(no);
							} else if (strType.equals("raion")) {
								raions.addElement(strLastRegion);
								raionIds.addElement(no);
							}

							lastRegion = new StringBuffer(LIMIT);
							type = new StringBuffer();
							idx++;
							state = 0;
						} else {
							state++;
						}

						inString = false;
					} else {
						inString = true;
					}
				} else {
					if (inString) {
						if (state == 1) {
							lastRegion.append((char)read);
						} else if (state == 3) {
							type.append((char)read);
						}
					}
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

	public void update() {
	}
}
