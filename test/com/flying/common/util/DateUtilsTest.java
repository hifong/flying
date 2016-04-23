package com.flying.common.util;

import org.junit.Test;

import com.flying.common.util.DateUtils;

public class DateUtilsTest {
	@Test
	public void parseDate() {
		System.out.println(DateUtils.parseDate("2015-01-03"));
		System.out.println(DateUtils.parseDate("2015-01-03", "yyyy-MM-dd"));
		System.out.println(DateUtils.parseDate("2015-01-03", "yyyy-MM-dd HH:mm:ss"));
	}
}
