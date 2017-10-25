package com.sunquan.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ParserSqlUtil {
	
	private static CCJSqlParserManager parserManager = new CCJSqlParserManager();
	/**
	 * @param sql
	 * @return Map<小写别名, 大写字段名>
	 */
	public static Map<String,String> AnalysisSql(String sql)
	{
		Map<String,String> result =  new HashMap<String, String>();
        Select select;
		try {
			select = (Select) parserManager.parse(new StringReader(sql));
		} catch (JSQLParserException e) {
			e.printStackTrace();
			return result;
		}
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<SelectItem> titles =  plainSelect.getSelectItems();
        //System.out.println("size =  " +  titles.size());
        for(int i =0; i < titles.size() ; ++i )
        {
        	SelectExpressionItem element = (SelectExpressionItem) titles.get(i);
        	
        	String oldname = element.getExpression().toString();
        	String alias = element.getAlias().getName();
        	if(!oldname.contains("("))
        	{
        		int lastIdx= oldname.lastIndexOf(".");
        		if(lastIdx>=0)
        			oldname = oldname.substring(lastIdx+1);
        		result.put(alias.toLowerCase(), oldname.toUpperCase());
        		//System.out.println("MAP: alias = "+oldname + ", expression = "+ alias  );
        	}
        	//System.out.println("alias = "+oldname + ", expression = "+ alias  );
        }
        return result;
	}

	public static void main(String[] args) {
		
		//String sqlstr = " select AA.F0382_001D as CODES , AA.* from AA where ROWNUM <3 ";
		String sqlstr = "select max(t1.ob_seq_id+t2.ob_seq_id) as A , ob_seq_id as b \n from (select max(ob_seq_id) ob_seq_id"
				+ " from tb_company_0013) t1, (select max(ob_seq_id) ob_seq_id from tb_public_0005) t2";
		ParserSqlUtil.AnalysisSql(sqlstr);
	}
}
