package com.muling.common.cert.util.sand;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SdkUtil {
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map = null;
		try {
			if (StringUtils.isNotBlank(result)) {
				if ((result.startsWith("{")) && (result.endsWith("}"))) {
					result = result.substring(1, result.length() - 1);
				}
				map = parseQString(result);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, String> parseQString(String str) throws UnsupportedEncodingException {
		Map<String, String> map = new HashMap();
		int len = str.length();
		StringBuilder temp = new StringBuilder();

		String key = null;
		boolean isKey = true;
		boolean isOpen = false;
		char openName = '\000';
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				char curChar = str.charAt(i);
				if (isKey) {
					if (curChar == '=') {
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else {
					if (isOpen) {
						if (curChar == openName) {
							isOpen = false;
						}
					} else {
						if (curChar == '{') {
							isOpen = true;
							openName = '}';
						}
						if (curChar == '[') {
							isOpen = true;
							openName = ']';
						}
					}
					if ((curChar == '&') && (!isOpen)) {
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					} else {
						temp.append(curChar);
					}
				}
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey, String key, Map<String, String> map)
			throws UnsupportedEncodingException {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}
}
