package com.flying.common.util;

public interface Codes {
	public final static String HEADS="$.headers";
	public final static String HEAD_USERID="x-userid";
	
	public final static String CODE = "ReturnCode";
	public final static String ROWS = "rows";
	public final static String EFFECT_ROWS = "EFFECT_ROWS";
	public final static String MSG = "Msg";
	
	public final static int SUCCESS = 0;
	public final static int FAIL = 1;
	public final static int INTERNAL_ERROR = 1;
	public final static int INVALID_PARAM = 2;

	public final static int AUTH_FAIL = 1;
	public final static int USER_NOT_EXISTS = 1;
	public final static int TOKEN_EXPIRES = 1;
	public final static int TOKEN_NOT_EXISTS = 1;

	public final static int MSG_NOT_EXISTS= 1;
}
