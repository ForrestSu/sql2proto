package com.sunquan.util;

public class ProtoUtil{
    /**
     * 将Oracle类型转为 protobuf的类型
     * 
     * @param itype
     * @param prefix
     *            prefix 根据字段名的开头开判断：<br/>
     *            b### bool <br/>
     *            i### int32 <br/>
     *            u### uint32 <br/>
     *            l### int64 <br/>
     *            d### double <br/>
     *            f### double <br/>
     *            s### string <br/>
     * @return
     */
    public static FieldType Convert2ProtoType(int itype, String fieldname) {
        if (itype == java.sql.Types.DATE || itype == java.sql.Types.VARCHAR) {
            // Date/VARCHAR类型 如果是 i###开头，则说明用 int 类型存储
            char prefix = fieldname.charAt(0);
            if (prefix == 'i')
                return FieldType.INT32;
            else if (prefix == 'l')
                return FieldType.INT64;
            // 没有指定则采用 string
            return FieldType.STRING;
        } else if (itype == java.sql.Types.NUMERIC) {

            // if (fieldname.charAt(1) == '_')
            char prefix = Character.toLowerCase(fieldname.charAt(0));
            if (prefix == 'b')
                return FieldType.BOOL;
            else if (prefix == 'i')
                return FieldType.INT32;
            else if (prefix == 'u')
                return FieldType.UINT32;
            else if (prefix == 'l')
                return FieldType.INT64;
            else if (prefix == 'd')
                return FieldType.DOUBLE;
            else if (prefix == 'f')
                return FieldType.DOUBLE;
            else if (prefix == 's')
                return FieldType.STRING;
            else {
                System.err.println("No support prefix: " + prefix + "!");
                return null;
            }
        }
        System.err.println("No support FieldName: " + fieldname + " ,type:" + itype + "!");
        return null;
    }
}
