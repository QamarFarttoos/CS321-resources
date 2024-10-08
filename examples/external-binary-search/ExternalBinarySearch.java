import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Provides an external binary search method that can search a sorted file of records. 
 * This is a demonstration of searching binary files on disk that store data.
 * 
 * @author amit
 *
 */
public class ExternalBinarySearch
{
	private int DEBUG = 0;

	/**
	 * Set debug level. 
	 * @param debug 0 is no debug output, 1 is more output of intermediate steps 
	 */
	public void setDebug(int debug) 
	{
		this.DEBUG = debug;
	}

	/**
	 * Search for a record with the input key value in the specified binary file 
	 * on the disk. Uses binary search to minimize the number of disk reads.
	 * 
	 * @param dataFile file channel to an open file
	 * @param key integer key value to use for searching
	 * @return Reference to a record structure if search was successful, otherwise a null pointer
	 * @throws IOException
	 */
	public Record search(FileChannel dataFile, int key)
	throws IOException
	{
		/* the search window is low..high if we view the dataFile as an
		   array of record structures */
		long low = 0;
		long mid = 0;
		long high = 0;
		long pos = 0; /* offset into the file, in bytes */
		Record record = new Record();
		int recordSize = record.getDiskSize();
		ByteBuffer buffer = ByteBuffer.allocateDirect(recordSize);

		if (dataFile == null) return null;

		/* figure out the size of the file in bytes */
		high = dataFile.size();
		/* set high to number of records in the file */
		high = high/recordSize - 1;

		if (DEBUG >= 1) {
			System.err.println("data file has "+high+" records");
		}

		int recordKey = -1;
		while (low <= high) {
			mid = (low+high)/2;
			pos = mid * recordSize; /* calculate byte offset in the file */
			dataFile.position(pos);
			buffer.clear();
			dataFile.read(buffer);
			buffer.flip();
			if (DEBUG >= 1) {
				System.err.println("low = " + low + " mid = " + mid + " high = " + high);
			}
			recordKey = buffer.getInt();
			if (recordKey == key) {
				return new Record(recordKey, buffer.getDouble(), 
									buffer.getDouble(), buffer.getDouble());
			} else if (recordKey < key) {
				low = mid + 1;
			} else { /* recordKey > key */
				high = mid - 1;
			}
		}
		return null;
		}
}
