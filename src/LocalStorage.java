import javax.microedition.rms.*;

public class LocalStorage {
	LocalStorage() {
	}

	public void saveRegions(int[] indices) throws RecordStoreFullException {
		try {
			RecordStore aStore = RecordStore.openRecordStore("J2Alert_subscriptions", true);

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
	}
}
