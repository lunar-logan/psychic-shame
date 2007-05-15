/**
 * 
 */
package nodomain.applewhat.torrentdemonio.metafile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import nodomain.applewhat.torrentdemonio.bencoding.BDecoder;
import nodomain.applewhat.torrentdemonio.bencoding.BDictionary;
import nodomain.applewhat.torrentdemonio.bencoding.BElement;
import nodomain.applewhat.torrentdemonio.bencoding.BEncoder;
import nodomain.applewhat.torrentdemonio.bencoding.BEncodingException;
import nodomain.applewhat.torrentdemonio.bencoding.BInteger;
import nodomain.applewhat.torrentdemonio.bencoding.BList;
import nodomain.applewhat.torrentdemonio.bencoding.BString;

/**
 * @author Alberto Manzaneque
 *
 */
public class TorrentMetadata {
	
	public static class ContainedFile {
		private String name;
		private int length;
		private ContainedFile() {}
		public int getLength() {
			return length;
		}
		public String getName() {
			return name;
		}
	}
	
	
	private String announce;
	private Date creationDate;
	private String createdBy;
	private String comment;
	private boolean multifile;
	private int pieceLength;
	private List<byte[]> pieceHashes;
	private String directory;
	private List<ContainedFile> files;
	private byte[] infoHash;
	
	private TorrentMetadata() {
		pieceHashes = new ArrayList<byte[]>();
		files = new ArrayList<ContainedFile>();
	}
	
	
	public static TorrentMetadata createFromFile(File file) throws IOException, MalformedMetadataException {
		BDecoder decoder = new BDecoder(new FileInputStream(file));
		BElement bDec = null;
		try {
			bDec= decoder.decodeNext();
		} catch (BEncodingException e) {
			throw new MalformedMetadataException("File "+file.getAbsolutePath()+" is not a well-formed .torrent", e);
		}

		TorrentMetadata result = new TorrentMetadata();
		try {
			BDictionary root = (BDictionary)bDec;
			
			bDec = root.get(new BString("announce"));
			if(bDec == null)
				throw new MalformedMetadataException("announce not present in .torrent");
			result.announce = ((BString)bDec).getValue();
			
			bDec = root.get(new BString("creation date"));
			if(bDec != null)
				result.creationDate = new Date(((BInteger)bDec).getValue());
			
			bDec = root.get(new BString("comment"));
			if(bDec != null)
				result.comment = ((BString)bDec).getValue();
			
			bDec = root.get(new BString("created by"));
			if(bDec != null)
				result.createdBy = ((BString)bDec).getValue();
			
			root = (BDictionary) root.get(new BString("info"));
			if(root == null)
				throw new MalformedMetadataException("announce not present in .torrent");
			
			
			ByteOutputStream info = new ByteOutputStream(1024);
			BEncoder encoder = new BEncoder(info);
			encoder.encode(root);
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			sha1.update(info.getBytes(), 0, info.getCount());
			result.infoHash = sha1.digest();
			
			
			bDec = root.get(new BString("piece length"));
			if(bDec == null)
				throw new MalformedMetadataException("piece length not present in .torrent");
			result.pieceLength = ((BInteger)bDec).getValue();
			
			bDec = root.get(new BString("pieces"));
			if(bDec == null)
				throw new MalformedMetadataException("pieces not present in .torrent");
			byte[] hashes = ((BString)bDec).getBytes();
			if(hashes.length % 20 != 0)
				throw new MalformedMetadataException("incorrect length of pieces");
			for(int bytesRead = 0; bytesRead < hashes.length; bytesRead += 20) {
				result.pieceHashes.add(Arrays.copyOfRange(hashes, bytesRead, bytesRead+20));
			}
			
			bDec = root.get(new BString("length"));
			if(bDec != null) { // 1 file torrent
				result.directory = "";
				ContainedFile cFile = new ContainedFile();
				cFile.length = ((BInteger)bDec).getValue();
				bDec = root.get(new BString("name"));
				if(bDec == null)
					throw new MalformedMetadataException("file name not present in .torrent");
				cFile.name = ((BString)bDec).getValue();
				result.files.add(cFile);
			} else if((bDec=root.get(new BString("files"))) != null) { // multifile torrent
				bDec = root.get(new BString("name"));
				if(bDec == null)
					throw new MalformedMetadataException("file name not present in .torrent");
				result.directory = ((BString)bDec).getValue();
				
				BList list = (BList)root.get(new BString("files"));
				if(list.size() == 0)
					throw new MalformedMetadataException(".torrent contains no files");
				for (BElement next : list) {
					BDictionary dir = (BDictionary) next;
					ContainedFile cFile = new ContainedFile();
	
					bDec = dir.get(new BString("length"));
					if(bDec == null)
						throw new MalformedMetadataException("file length not present");
					cFile.length = ((BInteger)bDec).getValue();
					
					bDec = dir.get(new BString("path"));
					if(bDec == null)
						throw new MalformedMetadataException("file path not present");
					BList pathList = (BList)bDec;
					if(pathList.size() == 0)
						throw new MalformedMetadataException("file path not present");
					StringBuffer path = new StringBuffer();
					for (BElement element : pathList) {
						path.append(((BString)element).getValue());
						path.append(File.separator);
					}
					path.deleteCharAt(path.length()-1);
					cFile.name = path.toString();
	
					result.files.add(cFile);
				}
				
			} else {
				throw new MalformedMetadataException("no file found in .torrent");
			}
		} catch (ClassCastException e) {
			throw new MalformedMetadataException("not valid structure", e);
		} catch (NoSuchAlgorithmException e) {
			// TODO escribir un error entendible
			e.printStackTrace();
			System.exit(1);
		}
		
		return result;
	}


	public String getAnnounce() {
		return announce;
	}


	public String getComment() {
		return comment;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public String getDirectory() {
		return directory;
	}


	public List<ContainedFile> getFiles() {
		return files;
	}


	public boolean isMultifile() {
		return multifile;
	}


	public List<byte[]> getPieceHashes() {
		return pieceHashes;
	}


	public int getPieceLength() {
		return pieceLength;
	}
	
	public byte[] getInfoHash() {
		return infoHash;
	}
	
}
