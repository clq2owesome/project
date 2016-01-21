package com.clq.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * sql编辑工具,好处：防止sql注入
 * @author chenliqiang
 *
 */
public class SqlBuilder {
	protected StringBuilder sqlBuf = new StringBuilder();
    protected List<Object> values = new ArrayList<Object>();

    public SqlBuilder appendSql(String sql) {
        sqlBuf.append(sql);
        return this;
    }    

    public SqlBuilder appendValue(Object value) {
        sqlBuf.append('?');
        values.add(value);
        return this;
    }

    public SqlBuilder appendValues(Object[] values) {
        sqlBuf.append('(');
        for (int i = 0, c = values.length; i < c; ++i) {
            sqlBuf.append('?').append(','); 
            this.values.add(values[i]);
        }
        int last = sqlBuf.length() - 1;
        if (last > 0 && sqlBuf.charAt(last) == ',') {
            sqlBuf.setCharAt(last, ')');
        }
 
        return this;
    }
    
    public String buildPageSql(String sql, int pageNo, int pageSize) {
    	if(pageNo < 1) pageNo = 1;
    	if(pageSize > 100) pageSize = 100;
    	return sql + " LIMIT " + (pageNo - 1) * pageSize + "," + pageSize;
    }

    public String getSql() {
        return sqlBuf.toString();
    }

    public Object[] getValues() {
        return values.toArray();
    }

    public boolean hasValue() {
        return ! values.isEmpty();
    }
    
    public static void main(String[] args) {
    	SqlBuilder sql = new SqlBuilder();
    	String str = "95 and 1 = 1";
    	sql.appendSql("select * from clq_user ")
    	   .appendSql(" where id = ").appendValue(str);
    	System.out.println("sql="+sql.getSql());
    	System.out.println("value="+sql.getValues());
    }
    
}
