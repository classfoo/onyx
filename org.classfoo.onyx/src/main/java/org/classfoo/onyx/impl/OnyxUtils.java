package org.classfoo.onyx.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Onyx Utility Class
 * @author ClassFoo
 *
 */
public class OnyxUtils {

	public static final Logger logger = LoggerFactory.getLogger(OnyxUtils.class);

	/**
	 * return error information map to front ends
	 * @param message
	 * @param params
	 * @return
	 */
	public static Map<String, Object> returnError(String message, Object... params) {
		HashMap<String, Object> error = new HashMap<String, Object>(2);
		error.put("type", "error");
		error.put("message", message);
		logger.error(message);
		return error;
	}

	/**
	 * return error information map to front ends
	 * @param args
	 * @param message
	 * @param params
	 * @return
	 */
	public static Map<String, Object> returnError(Map<String, Object> args, String message, Object... params) {
		HashMap<String, Object> error = new HashMap<String, Object>(args.size() + 2);
		error.putAll(args);
		error.put("type", "error");
		error.put("message", message);
		logger.error(message);
		return error;
	}

	/**
	 * 获取压缩后的uuid
	 * @return
	 */
	public static String getCompressedUUID() {
		UUID uuid = UUID.randomUUID();
		return getCompressedUUID(uuid);
	}

	private static String getCompressedUUID(UUID uuid) {
		byte[] byUuid = new byte[16];
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		long2bytes(most, byUuid, 0);
		long2bytes(least, byUuid, 8);
		return Base64.encodeBase64URLSafeString(byUuid);
	}

	private static void long2bytes(long value, byte[] bytes, int offset) {
		for (int i = 7; i > -1; i--) {
			bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
		}
	}

	public static String getRandomUUID(String prefix) {
		return prefix + UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static <T> T readJson(Map<String, Object> options, String key, Class<T> type) {
		try {
			String v = MapUtils.getString(options, key);
			if (String.class.equals(type)) {
				return type.cast(v);
			}
			ObjectMapper om = new ObjectMapper();
			T list = om.readValue(v, type);
			return list;
		}
		catch (Exception e) {
			throw new RuntimeException("parse json failed!", e);
		}
	}
}
