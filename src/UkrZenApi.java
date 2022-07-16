import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;

public class UkrZenApi {
	private static final String LOCATIONS_URI = "http://j2alert.mooo.com/api/locations.json";
	private static final String ACTIVE_URI = "http://j2alert.mooo.com/api/active.mp";
	private static final int LIMIT = 128;

	private Vector cities;
	private Vector hromadas;
	private Vector oblasts;
	private Vector raions;

	private Vector cityIds;
	private Vector hromadaIds;
	private Vector oblastIds;
	private Vector raionIds;

	private Vector activeRegions;

	UkrZenApi() {
		readConfig();
		initVectors();
		activeRegions = new Vector();
	}

	private void readConfig() {
	}

	private void initVectors() {
		cities = new Vector();
		hromadas = new Vector();
		oblasts = new Vector();
		raions = new Vector();

		cityIds = new Vector();
		hromadaIds = new Vector();
		oblastIds = new Vector();
		raionIds = new Vector();
	}

	public void fetchRegions() throws IOException {
		try {
			HttpConnection hc = (HttpConnection)Connector.open(LOCATIONS_URI);
			InputStream is = hc.openInputStream();
			Reader r = new InputStreamReader(is, "UTF-8");

			initVectors();

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

			hc.close();
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

	public void fetchActive() throws IOException {
		try {
			HttpConnection hc = (HttpConnection)Connector.open(ACTIVE_URI);
			DataInputStream dis = hc.openDataInputStream();

			reliablySkipBytes(dis, 8);

			int arrayHead = dis.readUnsignedByte();
			int arrayLength = 0;
			if (arrayHead == 0xdc) {
				arrayLength = dis.readUnsignedShort();
			} else if (arrayHead >= 0x90 && arrayHead < 0xa0) {
				arrayLength = arrayHead & 0x0f;
			} else {
				throw new IOException();
			}

			activeRegions = new Vector();

			for (int i = 0; i < arrayLength; i++) {
				int objectLength = dis.readUnsignedByte();
				if (objectLength >= 0x80 && objectLength < 0x90) {
					objectLength = objectLength & 0x0f;
				} else {
					System.out.println("ol" + Integer.toString(objectLength));
					throw new IOException();
				}

				for (int j = 0; j < objectLength; j++) {
					int stringLength = dis.readUnsignedByte();
					if (stringLength >= 0xa0 && stringLength < 0xb0) {
						stringLength = stringLength & 0x0f;
					} else {
						System.out.println("sl" + Integer.toString(stringLength));
						throw new IOException();
					}

					// completely skip "u" and "s"
					if (j < 2) {
						if (stringLength != 1) {
							System.out.println("j" + Integer.toString(stringLength));
							throw new IOException();
						}
						reliablySkipBytes(dis, 6);
						continue;
					}

					reliablySkipBytes(dis, stringLength);

					int numByte = dis.readUnsignedByte();
					int regionIndex = 0;
					if (numByte < 0x80) {
						regionIndex = numByte;
					} else if (numByte == 0xc0) {
						// null: skip
					} else if (numByte == 0xcc) {
						regionIndex = dis.readUnsignedByte();
					} else if (numByte == 0xcd) {
						regionIndex = dis.readUnsignedShort();
					} else if (numByte == 0xce) {
						// some 32-bit uint: skip
						reliablySkipBytes(dis, 4);
						continue;
					} else if (numByte >= 0xa0 && numByte < 0xc0) {
						// some fixstr: skip
						reliablySkipBytes(dis, numByte & 0x1f);
						continue;
					} else if (numByte == 0xd9 || numByte == 0xc4) {
						// some 8-bit format: skip
						int bytes8length = dis.readUnsignedByte();
						reliablySkipBytes(dis, bytes8length);
						continue;
					} else if (numByte == 0xda || numByte == 0xc5) {
						// some 16-bit format: skip
						int bytes16length = dis.readUnsignedShort();
						reliablySkipBytes(dis, bytes16length);
						continue;
					} else {
						// wut's that?
						System.out.println("wut" + Integer.toString(numByte));
						continue;
					}

					activeRegions.addElement(new Integer(regionIndex));
				}
			}

			hc.close();
		} catch (IOException e) {
			throw e;
		}
	}

	public Vector getActive() {
		return activeRegions;
	}

	private void reliablySkipBytes(DataInputStream dis, int toSkip) throws IOException {
		try {
			int skipped = 0;
			do {
				skipped += dis.skipBytes(toSkip - skipped);
			} while (skipped < toSkip);
		} catch (IOException e) {
			throw e;
		}
	}

	public void update() {
	}
}
