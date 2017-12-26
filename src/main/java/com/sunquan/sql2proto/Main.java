package com.sunquan.sql2proto;

import com.sunquan.util.SysUtil;

public class Main {

	private static void gen_msg()
	{ 
		 // 定义每个文件生成的消息名称
        String protoNamefile = "D:/ems/src/iSolar/SolarService/sqlindex.cfg";
        /* 输入文件夹路径 */
        String sqlpath = "D:/ems/src/iSolar/conf/sqlGroup/test";
        /* 输出文件的路径 */
        String outputfile = "D:/ems/src/iSolar/solarproto/sqlstruct.proto";

        GenProtosOracle op = new GenProtosOracle(protoNamefile);

        int iCount = op.LoadSql(sqlpath, outputfile);

        System.out.println("成功处理文件个数：" + iCount);
	}
	
    public static void main(String[] args) {
      
        gen_msg();
        
        System.out.println("开始编译proto...");
        String shell = "sh /d/sqtools/proto.sh new";
        SysUtil.DoCmd(shell);
        System.out.println("Complete!");
    }
}
