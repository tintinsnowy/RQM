package com.ibm.rqm.utils;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	public static final String UTF_8 = "UTF-8";

	public static final String TAG = FileUtils.class.getSimpleName();

	private static final int MAX_BUFFER_LENGTH = 4096;

	public static StringBuffer readText(String filePath, String decoder) {
		try {
			File file = new File(filePath);
			if (!file.exists() || !file.canRead())
				return null;

			return readText(filePath, decoder, 0, (int) file.length());

		} catch (Exception e) {
			Log.e(TAG,
                    String.format("readText Error! message:%s", e.getMessage()));
			return null;
		}
	}

	/**
	 * ???????????
	 * 
	 * @param filePath
	 *            ???��??
	 * @param decoder
	 *            ?????? ??GBK UTF-8
	 * @param offset
	 *            ?????
	 * @param length
	 *            ????
	 * @return ????????
	 */
	public static StringBuffer readText(String filePath, String decoder,
			int offset, int length) {
		FileInputStream fileInputStream = null;
		BufferedInputStream buffReader = null;

		try {
			fileInputStream = new FileInputStream(filePath);
			buffReader = new BufferedInputStream(fileInputStream);

			StringBuffer buffer = new StringBuffer();

			byte[] bytesBuf = new byte[length];
			buffReader.skip(offset);
			buffReader.read(bytesBuf, 0, length);

			return buffer.append(new String(bytesBuf, decoder));
		} catch (Exception e) {
			Log.e(TAG,
                    String.format("readText Error!\te.getMessage:%s",
                            e.getMessage()));
		} finally {
			closeCloseable(fileInputStream);
			closeCloseable(buffReader);
		}

		return null;
	}

	public static byte[] getBuffer(String path, int off, int length) {
		byte[] cover = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			cover = new byte[length];
			fis.skip(off);
			fis.read(cover, 0, length);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.closeCloseable(fis);
		}

		return cover;
	}

	public static byte[] getBuffer(String path) {
		File file = null;
		FileInputStream fis = null;
		byte[] cover = null;
		try {
			file = new File(path);
			int length = (int) file.length();
			fis = new FileInputStream(file);
			cover = new byte[length];
			fis.read(cover, 0, length);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.closeCloseable(fis);
		}
		return cover;
	}

	/**
	 * ???stream or reader
	 * 
	 * @param closeObj
	 */
	public static void closeCloseable(Closeable closeObj) {
		try {
			if (null != closeObj)
				closeObj.close();
		} catch (IOException e) {
			Log.e("Error",
                    "Method:readFile, Action:closeReader\t" + e.getMessage());
		}
	}

	public static String getContent(AssetManager assets, String fileName) {
		String ret = "";
		InputStream stream = null;
		try {
			stream = assets.open(fileName);
			ret = getContent(stream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// ??????
			closeCloseable(stream);
		}
		return ret;
	}

	// ??????, ??????
	private static String getContent(InputStream stream) {
		String ret = "";
		try {
			int len = stream.available();
			byte[] buffer = new byte[len];
			stream.read(buffer);

			ret = new String(buffer, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * ?? <b> UTF-8 </b>?????????????��?????,????????????????��
	 * 
	 * @param path
	 *            ???��??
	 * @param content
	 *            ??��??????
	 * @return ????????true????????false
	 */
	public static boolean writeString(String path, String content) {
		String encoding = "UTF-8";
		File file = new File(path);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		return writeString(path, content, encoding);
	}

	/**
	 * ??????????��?????,????????????????��
	 * 
	 * @param path
	 *            ???��??
	 * @param content
	 *            ??��??????
	 * @param encoding
	 *            String????byte[]????
	 * @return ????????true????????false
	 */
	public static boolean writeString(String path, String content,
			String encoding) {
		FileOutputStream fos = null;
		boolean result = false;
		try {
			fos = new FileOutputStream(path);
			byte[] cover = content.getBytes(encoding);
			fos.write(cover, 0, cover.length);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.closeCloseable(fos);
		}
		return result;
	}

	/**
	 * ??????????��?????????
	 * 
	 * @param fileData
	 *            ??????????
	 * @return ��??????????true???????false
	 */
	public static boolean writeBytes(String targetFilePath, byte[] fileData) {
		boolean result = false;
		File targetFile = new File(targetFilePath);
		File parentFile = targetFile.getParentFile();
		if (parentFile != null && !parentFile.exists()) {
			targetFile.getParentFile().mkdirs();
		}
		if (targetFile.exists()) {
			targetFile.delete();
		}
		ByteArrayInputStream fosfrom = null;
		FileOutputStream fosto = null;
		try {
			fosfrom = new ByteArrayInputStream(fileData);
			fosto = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024 * 4];
			int length;
			while ((length = fosfrom.read(buffer)) != -1) {
				fosto.write(buffer, 0, length);
			}
			fosto.flush();
			result = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeCloseable(fosto);
			closeCloseable(fosfrom);
		}
		return result;
	}

	/**
	 * ???????
	 * 
	 * @param oldPath
	 *            String ????��??
	 * @param newPath
	 *            String ?????��??
	 * @return
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		boolean result = false;
		File oldFile = new File(oldPath);
		File newFile = new File(newPath);
		if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
			return result;
		}

		File parentFile = newFile.getParentFile();
		if (parentFile != null && !parentFile.exists()) {
			newFile.getParentFile().mkdirs();
		}
		if (newFile.exists()) {
			newFile.delete();
		}
		FileInputStream fosfrom = null;
		FileOutputStream fosto = null;
		try {
			fosfrom = new FileInputStream(oldFile);
			fosto = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024 * 4];
			int length;
			while ((length = fosfrom.read(buffer)) != -1) {
				fosto.write(buffer, 0, length);
			}
			fosto.flush();
			result = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeCloseable(fosto);
			closeCloseable(fosfrom);
		}
		return result;
	}

	/**
	 * ????????????????
	 * 
	 * @param file
	 *            ????????
	 */
	public static void deleteAllFiles(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteAllFiles(f);
			}
		}
	}
}
