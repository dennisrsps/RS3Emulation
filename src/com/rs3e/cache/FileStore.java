package com.rs3e.cache;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.rs3e.cache.util.FileChannelUtils;

/**
 * A file store holds multiple files inside a "virtual" file system made up of
 * several index files and a single data file.
 * 
 * @author Graham
 * @author `Discardedx2
 */
public final class FileStore implements Closeable {

	/**
	 * Opens the file store stored in the specified directory.
	 * 
	 * @param root
	 *            The directory containing the index and data files.
	 * @return The file store.
	 * @throws FileNotFoundException
	 *             if any of the {@code main_file_cache.*} files could not be
	 *             found.
	 */
	public static FileStore open(String root) throws FileNotFoundException {
		return open(new File(root));
	}

	/**
	 * Opens the file store stored in the specified directory.
	 * 
	 * @param root
	 *            The directory containing the index and data files.
	 * @return The file store.
	 * @throws FileNotFoundException
	 *             if any of the {@code main_file_cache.*} files could not be
	 *             found.
	 */
	public static FileStore open(File root) throws FileNotFoundException {
		File data = new File(root, "main_file_cache.dat2");
		if (!data.exists())
			throw new FileNotFoundException();

		RandomAccessFile raf = new RandomAccessFile(data, "rw");
		FileChannel dataChannel = raf.getChannel();

		List<FileChannel> indexChannels = new ArrayList<FileChannel>();
		for (int i = 0; i < 254; i++) {
			File index = new File(root, "main_file_cache.idx" + i);
			if (!index.exists())
				break;

			raf = new RandomAccessFile(index, "rw");
			FileChannel indexChannel = raf.getChannel();
			indexChannels.add(indexChannel);
		}

		if (indexChannels.isEmpty())
			throw new FileNotFoundException();

		File meta = new File(root, "main_file_cache.idx255");
		if (!meta.exists())
			throw new FileNotFoundException();

		raf = new RandomAccessFile(meta, "rw");
		FileChannel metaChannel = raf.getChannel();

		return new FileStore(dataChannel,
				indexChannels.toArray(new FileChannel[0]), metaChannel);
	}

	/**
	 * The data file.
	 */
	private final FileChannel dataChannel;

	/**
	 * The index files.
	 */
	private final FileChannel[] indexChannels;

	/**
	 * The 'meta' index files.
	 */
	private final FileChannel metaChannel;

	/**
	 * Creates a new file store.
	 * 
	 * @param data
	 *            The data file.
	 * @param indexes
	 *            The index files.
	 * @param meta
	 *            The 'meta' index file.
	 */
	public FileStore(FileChannel data, FileChannel[] indexes, FileChannel meta) {
		this.dataChannel = data;
		this.indexChannels = indexes;
		this.metaChannel = meta;
	}

	/**
	 * Gets the number of index files, not including the meta index file.
	 * 
	 * @return The number of index files.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public int getTypeCount() throws IOException {
		return indexChannels.length;
	}

	/**
	 * Gets the number of files of the specified type.
	 * 
	 * @param type
	 *            The type.
	 * @return The number of files.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public int getFileCount(int type) throws IOException {
		if ((type < 0 || type >= indexChannels.length) && type != 255)
			throw new FileNotFoundException();

		if (type == 255)
			return (int) (metaChannel.size() / Index.SIZE);
		return (int) (indexChannels[type].size() / Index.SIZE);
	}

	/**
	 * Writes a file.
	 * 
	 * @param type
	 *            The type of the file.
	 * @param id
	 *            The id of the file.
	 * @param data
	 *            A {@link ByteBuffer} containing the contents of the file.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void write(int type, int id, ByteBuffer data) throws IOException {
		data.mark();
		if (!write(type, id, data, true)) {
			data.reset();
			write(type, id, data, false);
		}
	}

	/**
	 * Writes a file.
	 * 
	 * @param type
	 *            The type of the file.
	 * @param id
	 *            The id of the file.
	 * @param data
	 *            A {@link ByteBuffer} containing the contents of the file.
	 * @param overwrite
	 *            A flag indicating if the existing file should be overwritten.
	 * @return A flag indicating if the file was written successfully.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private boolean write(int type, int id, ByteBuffer data, boolean overwrite)
			throws IOException {
		if ((type < 0 || type >= indexChannels.length) && type != 255)
			throw new FileNotFoundException();

		FileChannel indexChannel = type == 255 ? metaChannel
				: indexChannels[type];

		int nextSector = 0;
		long ptr = id * Index.SIZE;
		if (overwrite) {
			if (ptr < 0) {
				throw new IOException();
			} else if (ptr >= indexChannel.size()) {
				return false;
			}

			ByteBuffer buf = ByteBuffer.allocate(Index.SIZE);
			FileChannelUtils.readFully(indexChannel, buf, ptr);

			Index index = Index.decode((ByteBuffer) buf.flip());
			nextSector = index.getSector();
			if (nextSector <= 0
					|| nextSector > dataChannel.size() * Sector.SIZE)
				return false;
		} else {
			nextSector = (int) ((dataChannel.size() + Sector.SIZE - 1) / Sector.SIZE);
			if (nextSector == 0)
				nextSector = 1;
		}

		Index index = new Index(data.remaining(), nextSector);
		indexChannel.write(index.encode(), ptr);
		int sectorDataSize = (id > 65535) ? Sector.EXPANDED_DATA_SIZE
				: Sector.DATA_SIZE;

		ByteBuffer buf = ByteBuffer.allocate(Sector.SIZE);

		int chunk = 0, remaining = index.getSize();
		do {
			int curSector = nextSector;
			ptr = curSector * Sector.SIZE;
			nextSector = 0;

			if (overwrite) {
				buf.clear();
				FileChannelUtils.readFully(dataChannel, buf, ptr);

				Sector sector = Sector.decode((ByteBuffer) buf.flip(),
						(id > 65535));

				if (sector.getType() != type) {
					return false;
				}

				if (sector.getId() != id) {
					return false;
				}

				if (sector.getChunk() != chunk) {
					return false;
				}

				nextSector = sector.getNextSector();
				if (nextSector < 0
						|| nextSector > dataChannel.size() / Sector.SIZE) {
					return false;
				}
			}

			if (nextSector == 0) {
				overwrite = false;
				nextSector = (int) ((dataChannel.size() + Sector.SIZE - 1) / Sector.SIZE);
				if (nextSector == 0)
					nextSector++;
				if (nextSector == curSector)
					nextSector++;
			}

			byte[] bytes = new byte[sectorDataSize];
			if (remaining < sectorDataSize) {
				data.get(bytes, 0, remaining);
				nextSector = 0; // mark as EOF
				remaining = 0;
			} else {
				remaining -= sectorDataSize;
				data.get(bytes, 0, sectorDataSize);
			}

			Sector sector = new Sector(type, id, chunk++, nextSector, bytes,
					(id > 65535));
			dataChannel.write(sector.encode(), ptr);
		} while (remaining > 0);

		return true;
	}

	/**
	 * Reads a file.
	 * 
	 * @param type
	 *            The type of the file.
	 * @param id
	 *            The id of the file.
	 * @return A {@link ByteBuffer} containing the contents of the file.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public ByteBuffer read(int type, int id) throws IOException {
		if ((type < 0 || type >= indexChannels.length) && type != 255) {
			throw new FileNotFoundException();
		}

		FileChannel indexChannel = type == 255 ? metaChannel
				: indexChannels[type];

		long ptr = id * Index.SIZE;
		if (ptr < 0 || ptr >= indexChannel.size()) {
			throw new FileNotFoundException();
		}

		ByteBuffer buf = ByteBuffer.allocate(Index.SIZE);
		FileChannelUtils.readFully(indexChannel, buf, ptr);

		Index index = Index.decode((ByteBuffer) buf.flip());
		if (index.getSector() <= 0
				|| index.getSector() > dataChannel.size() / Sector.SIZE) {
			return null;
		}

		ByteBuffer data = ByteBuffer.allocate(index.getSize());
		buf = ByteBuffer.allocate(Sector.SIZE);
		int sectorDataSize = (id > 65535) ? Sector.EXPANDED_DATA_SIZE
				: Sector.DATA_SIZE;
		// System.out.println("Expected size: "+index.getSize());

		int chunk = 0, remaining = index.getSize();
		ptr = index.getSector();
		do {
			buf.clear();
			FileChannelUtils.readFully(dataChannel, buf, ptr * Sector.SIZE);
			Sector sector = Sector
					.decode((ByteBuffer) buf.flip(), (id > 65535));

			if (remaining > sectorDataSize) {
				data.put(sector.getData(), 0, sectorDataSize);
				remaining -= sectorDataSize;
				if (sector.getType() != type) {
					throw new IOException("File type mismatch for type=" + type
							+ ", id=" + id + " at sector "
							+ (ptr / Sector.SIZE) + ". Expected type: "
							+ sector.getType());
				}

				if (sector.getId() != id) {
					throw new IOException("File id mismatch.");
				}

				if (sector.getChunk() != chunk++) {
					throw new IOException("Chunk mismatch.");
				}

				ptr = sector.getNextSector();
			} else {
				// System.out.println("Read last block: "+Arrays.toString(sector.getData())+" (size="+remaining+", sector="+(ptr/Sector.SIZE)+", nextSector="+sector.getNextSector()+")");
				data.put(sector.getData(), 0, remaining);
				remaining = 0;
			}
		} while (remaining > 0);
		// System.out.println("Actual size size: "+readCount);
		return (ByteBuffer) data.flip();
	}

	@Override
	public void close() throws IOException {
		dataChannel.close();
		for (FileChannel channel : indexChannels) {
			channel.close();
		}
		metaChannel.close();
	}

}
