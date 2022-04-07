import javax.microedition.rms.*;

public class LocalStorage {
	private final String SUBSCRIPTIONS = "J2Alert_subscriptions";

	LocalStorage() {
	}

	public int[] loadRegions() throws RecordStoreFullException, RecordStoreException, IllegalArgumentException {
		try {
			RecordStore aStore = RecordStore.openRecordStore(SUBSCRIPTIONS, true);

			byte[] bytes = aStore.getRecord(1);
			int[] indices = {-1, -1, -1, -1};
			if (bytes != null) {
				for (int i = 0; i < 4; i++) {
					indices[i] =
						((bytes[i*4] & 0xff) << 24) |
						((bytes[i*4+1] & 0xff) << 16) |
						((bytes[i*4+2] & 0xff) << 8) |
						((bytes[i*4+3] & 0xff));
				}
			}

			aStore.closeRecordStore();

			return indices;
		} catch (RecordStoreFullException e) {
			throw e;
		} catch (RecordStoreNotFoundException e) {
			throw (RecordStoreException)e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (RecordStoreNotOpenException e) {
			throw (RecordStoreException)e;
		} catch (InvalidRecordIDException e) {
			throw (RecordStoreException)e;
		} catch (RecordStoreException e) {
			throw e;
		}
	}

	public void saveRegions(int[] indices) throws RecordStoreFullException, RecordStoreException, IllegalArgumentException {
		try {
			RecordStore aStore = RecordStore.openRecordStore(SUBSCRIPTIONS, true);

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
		} catch (RecordStoreFullException e) {
			throw e;
		} catch (RecordStoreNotFoundException e) {
			throw (RecordStoreException)e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (RecordStoreNotOpenException e) {
			throw (RecordStoreException)e;
		} catch (InvalidRecordIDException e) {
			throw (RecordStoreException)e;
		} catch (RecordStoreException e) {
			throw e;
		}
	}
}
