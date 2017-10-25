package com.sunquan.sql2proto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.sunquan.util.FieldType;
import com.sunquan.util.FileUtil;
import com.sunquan.util.ParserSqlUtil;
import com.sunquan.util.ProtoUtil;


public class GenProtosOracle {

	// 定义连接所需的字符串
	private static String USERNAMR = "user";
	private static String PASSWORD = "123456";
	private static String DRVIER = "oracle.jdbc.OracleDriver";
	private static String URL = "jdbc:oracle:thin:@10.0.0.1:1521:s"; // std是数据库名

	private static String ProtoMsgBegin = System.lineSeparator() + "{";
	private static String ProtoMsgEnd = System.lineSeparator() + "}" + System.lineSeparator();
	private static String ProtoHeader = "import public \"typedef.proto\";\r\n"
			+ "import public \"msgcarrier.proto\";\r\n\r\n" + "package ST.SQLSTRUCT;\r\n\r\n"
			+ "/** prefix 根据字段名来判断类型：\r\n" 
			+ "*    b### bool \r\n" 
			+ "*    i### int32 \r\n" 
			+ "*    u### uint32\r\n"
			+ "*    l### int64 \r\n" 
			+ "*    d### double\r\n" 
			+ "*    f### double\r\n" 
			+ "*    s### string\r\n" + "*/\r\n";
	
	private static Map<String, String> MapComments;
	private static Map<String, String> ProtoName; // <文件名,MsgName>

	// 创建一个数据库连接
	private Connection connection = null;
	private PreparedStatement pstm = null;

	public GenProtosOracle(String protoname_file) {
		ProtoName = FileUtil.LoadStructName(protoname_file);
		System.out.println(">Load ProtoName count is " + ProtoName.size());
		MapComments = FileUtil.LoadComments("dicts/comments.csv");
		System.out.println(">Load Comments count is " + MapComments.size());
	}

	/**
	 * 返回成功 生成的proto 记录条数
	 * 
	 * @param src_dir
	 * @param output_file
	 * @return
	 */
	public int LoadSql(String src_dir, String output_file) {
		// 获取连接
		GetConnection();

		File dir = new File(src_dir);
		if (!dir.exists()) {
			System.err.println("src_dir=" + src_dir + " is not exists!");
			return 0;
		}
		FileWriter fw = null;
		BufferedWriter buffwriter = null;
		try {
			// 设置成尾部追加方式
			fw = new FileWriter(output_file, false);
			buffwriter = new BufferedWriter(fw);
			buffwriter.write(ProtoHeader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int icount = 0;
		String[] list = dir.list();

		String sqlstr = "";
		for (String s : list) {
			// System.out.println(s);
			File fileitem = new File(dir.getPath() + File.separator + s);
			String file_name = fileitem.getName();
			// 如果是 sql 文件
			if (fileitem.isFile() && file_name.toLowerCase().endsWith(".sql")) {

				// 读取SQL文件
				sqlstr = FileUtil.ReadFile2Str(fileitem.getAbsolutePath());

				// 生成proto文件
				String proto = ParseSql2Proto(sqlstr);
				// typemap.cfg中与映射，文件名和消息名称。 通过文件名可以查找到proto的名称
				String msg_name = ProtoName.get(file_name);
				if (msg_name == null) {
					msg_name = file_name;
					System.err.println("Can't find proto msg name!");
				}
				
				try {
					buffwriter.write(System.lineSeparator() + "message " + msg_name);
					buffwriter.write(proto);
					buffwriter.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				++icount;
			}
		}

		// 最后关掉输出流
		try {
			if (buffwriter != null)
				buffwriter.close();
			if (fw != null)
				fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 释放数据库连接
		Release();
		return icount;
	}

	private String GetComment(Map<String, String> fileds, String field_name) {
		String table_filed_name = fileds.get(field_name);
		if (table_filed_name != null) {
			String comments = MapComments.get(table_filed_name);
			if (comments != null)
				return "  //" + comments;
		}
		return "";
	}

	/**
	 * 使用ResultSetMetaData获取列的类型
	 */
	private String ParseSql2Proto(String sql) {
		// sql = sql.replace("$证券代码$", "000001");
		// System.out.println(sql);
		Map<String, String> filed_pairs = ParserSqlUtil.AnalysisSql(sql);
		StringBuilder result = new StringBuilder();
		result.append(ProtoMsgBegin);
		ResultSet rs = null;// 创建一个结果集对象
		// sql = "select 1 as cnt, AA.* \n from AA where ROWNUM <3";
		try {
			pstm = connection.prepareStatement(sql);
			rs = pstm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols_len = rsmd.getColumnCount();
			int field_cnt = 1;
			for (int i = 1; i <= cols_len; ++i) {
				// optional int64 reqid = 1; // 字段名
				String field_name = rsmd.getColumnLabel(i).toLowerCase();
				int file_type = rsmd.getColumnType(i);
				FieldType fieldtp = ProtoUtil.Convert2ProtoType(file_type, field_name);
				if (null != fieldtp) {
					result.append(System.lineSeparator()).append("\toptional ").append(fieldtp).append(" ")
							.append(field_name).append(" = ").append(field_cnt++).append(";")
							.append(GetComment(filed_pairs, field_name));
				}
				/*
				 * System.out.println( "i==" + i + "\t typeid=" +
				 * rsmd.getColumnType(i) + "\t type=" +
				 * rsmd.getColumnTypeName(i));
				 */
			}
			result.append(ProtoMsgEnd);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result.toString();
	}

	/**
	 * 获取Connection对象
	 * 
	 * @return
	 */
	public void GetConnection() {
		if (connection != null)
			return;
		try {
			Properties props = new Properties();
			/// props.put("remarksReporting", "true");
			props.put("user", USERNAMR);
			props.put("password", PASSWORD);
			Class.forName(DRVIER);
			connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
			System.out.println("成功连接数据库");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("class not find !", e);
		} catch (SQLException e) {
			throw new RuntimeException("get connection error!", e);
		}
	}

	/**
	 * 释放资源
	 */
	public void Release() {
		if (pstm != null) {
			try {
				pstm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 查询数据
	 */
	public void SelectData() {
		// 创建一个结果集对象
		ResultSet rs = null;
		GetConnection();
		String sql = " select count(*) as cnt from AA";
		try {
			pstm = connection.prepareStatement(sql);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String id = rs.getString("cnt");

				System.out.println("cnt==" + id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			Release();
		}
	}
}
