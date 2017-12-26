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
            BufferedReader brerr = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
            StringBuffer sbuff = new StringBuffer();
            //std output
            String line;
            while ((line = br.readLine()) != null) {
                sbuff.append(line).append(System.lineSeparator());
            }
            System.out.print(sbuff.toString());
            
            //error output
            sbuff.delete(0, sbuff.length());
            while ((line = brerr.readLine()) != null) {
                sbuff.append(line).append(System.lineSeparator());
            }
            System.err.print(sbuff.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
