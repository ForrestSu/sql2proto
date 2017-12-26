package com.sunquan.util;


public enum FieldType {

	UNKNOWN(0, "unkown"),
	INT32(1, "int32"), 
	INT64(2, "int64"),
    UINT32(3, "uint32"), 
    UINT64(4, "uint64"), 
    DOUBLE(5, "double"), 
    FLOAT(6, "float"),
	BOOL(7, "bool"),
	ENUM(8, "enum"),
	STRING(9, "string");

	private final int id;
	private final String human_type;

	FieldType(int id, String humanType) {
		this.id = id;
		this.human_type = humanType;
	}
	
	@Override
	public String toString() {
		return  human_type;
	}
	
	public static FieldType valueOf(int itype) {
		for (FieldType t : FieldType.values()) {
			if (t.id == itype) return t;
		}
		return UNKNOWN;
	}
}
