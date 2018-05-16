package com.sunquan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileUtil {

	/**
	 * 读取txt文件的内容
	 * 
	 * @param file
	 *            想要读取的文件对象
	 * @return 返回文件内容
	 */
	public static String ReadFile2Str(String sfile) {
		System.out.println(sfile);
		File file = new File(sfile);
		StringBuilder result = new StringBuilder();
		try {
			InputStreamReader istream = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader read = new BufferedReader(istream);
			String sline = "";
			// 使用readLine方法，一次读一行
			while ((sline = read.readLine()) != null) {
				result.append(sline + "\n");
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * 获取结构体的名字
	 * @param cfg-file
	 *        想要读取的文件对象
	 * @return 
	 *        Map<ProtoMsg, sqlfile> 返回文件内容
	 */
	public static List<String[]> LoadStructName(String sfile) {
		List<String[]> params = new LinkedList<String[]>();
		try {
			InputStreamReader istream = new InputStreamReader(new FileInputStream(sfile), "utf-8");
			BufferedReader read = new BufferedReader(istream);// 构造一个BufferedReader类来读取文件
			String sline = "";
			int lineno = 0;
		
			// 使用readLine方法，一次读一行
			while ((sline = read.readLine()) != null) {
				++lineno;
				sline = sline.trim();
				if ((!sline.startsWith("#")) && sline.length() > 5) {
					String[] arr = sline.split("[|]");
					if (arr.length > 3) {
						// 放入map
						String file_name= arr[3] + ".sql";
						String msg_name = arr[2];
						int ipos = msg_name.lastIndexOf('.');
					    if(ipos>=0) 
					    	msg_name = msg_name.substring(ipos+1,msg_name.length());
						if (msg_name.length() > 0) {
							params.add(new String[]{msg_name, file_name});
						}
					} else
						System.err.println("typemap.cfg error at line: " + lineno);
				}
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	/**
	 * @param 加载每个字段的注释
	 * @return map<大写字段名，注释>
	 * @usage: Map<String, String> result =
	 *         LoadComments("resources/comments.csv");
	 */
	public static Map<String, String> LoadComments(String sfile) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			InputStreamReader istream = new InputStreamReader(new FileInputStream(sfile), "utf-8");
			BufferedReader read = new BufferedReader(istream);// 构造一个BufferedReader类来读取文件
			String sline = "";
			int lineno = 0;
			// 使用readLine方法，一次读一行
			while ((sline = read.readLine()) != null) {
				++lineno;
				sline = sline.trim();
				if (sline.length() > 2 && sline.startsWith("\"") && sline.endsWith("\"")) {
					// 去除首尾字符
					sline = sline.substring(1, sline.length() - 1);
					String[] arr = sline.split("\",\"");
					if (arr.length == 2) {
						// 放入map
						if (arr[0].equals("OB_SEQ_ID")) {
							params.put(arr[0], "序列号");
							continue;
						}
						if (params.containsKey(arr[0])) {
							String old = params.get(arr[0]);
							if (!old.equals(arr[1])) {
								//System.out.println("key=" + arr[0] + old=" + old + ", new=" + arr[1]);
							}
						} else
							params.put(arr[0], arr[1]);
					} else
						System.err.println("["+ sfile +"] error at line: " + lineno);
				} else
					System.err.println("format error at line: " + lineno);
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}
}
