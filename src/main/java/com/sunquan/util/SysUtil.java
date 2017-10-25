package com.sunquan.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SysUtil {
	/**
	 * 执行一个shell 脚本
	 * @param shcmd
	 *            "sh /home/trade/build.sh"
	 */
	public static void DoCmd(String shcmd) {
		try {
			Process ps = Runtime.getRuntime().exec(shcmd);
			ps.waitFor();
			// 获取shell脚本的输出
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			StringBuffer sbuff = new StringBuffer();
			// print
			String line;
			while ((line = br.readLine()) != null) {
				sbuff.append(line).append("\n");
			}
			String result = sbuff.toString();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
