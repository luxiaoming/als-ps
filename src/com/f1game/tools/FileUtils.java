package com.f1game.tools;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.util.Log;

/**
 * File utilities
 */
public class FileUtils {
	// Logging
	private static final String TAG = "FileUtils";

	/**
	 * It is not possible to instantiate this class
	 */
	private FileUtils() {
	}

	/**
	 * Delete all the files in the specified folder and the folder itself.
	 * 
	 * @param dir
	 *            The project path
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			final String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				final File f = new File(dir, children[i]);
				if (!deleteDir(f)) {
					Log.e(TAG, "File cannot be deleted: " + f.getAbsolutePath());
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static boolean deleteFile(File file) {
		boolean del = false;
		if (file.exists() && file.isFile()) {
			del = file.delete();
		}

		// The directory is now empty so delete it
		return del;
	}

	/**
	 * Get the name of the file
	 * 
	 * @param filename
	 *            The full path filename
	 * @return The name of the file
	 */
	public static String getSimpleName(String filename) {
		final int index = filename.lastIndexOf('/');
		if (index == -1) {
			return filename;
		} else {
			return filename.substring(index + 1);
		}
	}

	/**
	 * 判断特殊字符
	 * 
	 * @Title : FilterStr
	 * @Type : FilterString
	 * @date : 2014年2月28日 下午11:01:21
	 * @Description : 判断特殊字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String FilterStr(String str) throws PatternSyntaxException {
		/**
		 * 特殊字符
		 */
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_]";

		/**
		 * Pattern p = Pattern.compile("a*b"); Matcher m = p.matcher("aaaaab");
		 * boolean b = m.matches();
		 */
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(str);

		/**
		 * 返回替换结果
		 */
		return mat.replaceAll("").trim();
	}

	public static void writestr(String flpath, String str) {
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {

			fw = new FileWriter(flpath, true);//
			// 创建FileWriter对象，用来写入字符流
			bw = new BufferedWriter(fw); // 将缓冲对文件的输出
			bw.write(str); // 写入文件
			bw.newLine();
			bw.flush(); // 刷新该流的缓冲
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
		}
	}

	public static String readFileContentStr(String fullFilename) {
		String readOutStr = null;

		try {

			DataInputStream dis = new DataInputStream(new FileInputStream(
					fullFilename));
			try {
				long len = new File(fullFilename).length();
				if (len > Integer.MAX_VALUE)
					throw new IOException("File " + fullFilename
							+ " too large, was " + len + " bytes.");
				byte[] bytes = new byte[(int) len];
				dis.readFully(bytes);
				readOutStr = new String(bytes, "UTF-8");
			} finally {
				dis.close();
			}

			Log.d("readFileContentStr",
					"Successfully to read out string from file " + fullFilename);
		} catch (IOException e) {
			readOutStr = null;

			// e.printStackTrace();
			Log.d("readFileContentStr", "Fail to read out string from file "
					+ fullFilename);
		}

		return readOutStr;
	}
}
