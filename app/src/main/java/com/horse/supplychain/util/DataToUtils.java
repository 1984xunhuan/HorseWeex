/**
 * 
 */
package com.horse.supplychain.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.text.TextUtils;

/**
 * 数据转换工具类
 *
 * 
 */
public class DataToUtils {
	private static final String TAG = "DataToUtils";

	/**
	 * 计算字节校验和
	 * 
	 * @param input
	 * @return
	 */
	public static byte calcLRC(byte[] input) {
		byte ret = 0;
		for (int i = 0; i < input.length; i++) {
			ret ^= input[i];
		}
		return ret;
	}

	/**
	 * usage: str2bytes("0710BE8716FB"); it will return a byte array, just like
	 * : b[0]=0x07;b[1]=0x10;...b[5]=0xfb;
	 */
	public static byte[] str2bytes(String src) {
		if (src == null || src.length() == 0 || src.length() % 2 != 0) {
			return null;
		}
		int nSrcLen = src.length();
		byte byteArrayResult[] = new byte[nSrcLen / 2];
		StringBuffer strBufTemp = new StringBuffer(src);
		String strTemp;
		int i = 0;
		while (i < strBufTemp.length() - 1) {
			strTemp = src.substring(i, i + 2);
			byteArrayResult[i / 2] = (byte) Integer.parseInt(strTemp, 16);
			i += 2;
		}
		return byteArrayResult;
	}

	/**
	 * 将整数转为16进行数后并以指定长度返回（当实际长度大于指定长度时只返回从末位开始指定长度的值）
	 * 
	 * @param val
	 *            int 待转换整数
	 * @param len
	 *            int 指定长度
	 * @return String
	 */
	public static String int2HexStr(int val, int len) {
		String result = Integer.toHexString(val).toUpperCase();
		int r_len = result.length();
		if (r_len > len) {
			return result.substring(r_len - len, r_len);
		}
		if (r_len == len) {
			return result;
		}
		StringBuffer strBuff = new StringBuffer(result);
		for (int i = 0; i < len - r_len; i++) {
			strBuff.insert(0, '0');
		}
		return strBuff.toString();
	}

	public static String convertInt2String(int n, int len) {
		String str = String.valueOf(n);
		int strLen = str.length();

		String zeros = "";
		for (int loop = len - strLen; loop > 0; loop--) {
			zeros += "0";
		}

		if (n >= 0) {
			return zeros + str;
		} else {
			return "-" + zeros + str.substring(1);
		}
	}

	public static int convertString2Int(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 将byte数组转换成16进制组成的字符串 例如 一个byte数组 b[0]=0x07;b[1]=0x10;...b[5]=0xFB;
	 * byte2hex(b); 将返回一个字符串"0710BE8716FB"
	 * 
	 * @param src  待转换的byte数组
	 * @param len
	 * @return
	 */
	public static String hexEncode(byte[] src, int len) {
		if (src == null || src.length <= 0 || len <= 0) {
			return null;
		}
		final byte[] dest = new byte[len];
		System.arraycopy(src, 0, dest, 0, len);
		return hexEncode(dest);
	}

	/**
	 * HEX编码 将形如0x12 0x2A 0x01 转换为122A01
	 * 
	 * @param data
	 * @return
	 */
	public static String hexEncode(byte[] data) {
		if (data == null || data.length <= 0) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String tmp = Integer.toHexString(data[i] & 0xff);
			if (tmp.length() < 2) {
				buffer.append('0');
			}
			buffer.append(tmp);
		}
		String retStr = buffer.toString().toUpperCase();
		return retStr;
	}

	/**
	 * HEX解码 将形如122A01 转换为0x12 0x2A 0x01
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] hexDecode(String data) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0; i < data.length(); i += 2) {
			String onebyte = data.substring(i, i + 2);
			int b = Integer.parseInt(onebyte, 16) & 0xff;
			out.write(b);
		}
		return out.toByteArray();
	}

	/**
	 * 生成16位的动态链接库鉴权十六进制随机数字符串
	 * 
	 * @return String
	 */
	public static String yieldHexRand() {
		StringBuffer strBufHexRand = new StringBuffer();
		Random rand = new Random(System.currentTimeMillis());
		int index;
		// 随机数字符
		char charArrayHexNum[] = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F' };
		for (int i = 0; i < 16; i++) {
			index = Math.abs(rand.nextInt()) % 16;
			if (i == 0) {
				while (charArrayHexNum[index] == '0') {
					index = Math.abs(rand.nextInt()) % 16;
				}
			}
			strBufHexRand.append(charArrayHexNum[index]);
		}
		return strBufHexRand.toString();
	}

	/**
	 * 异或
	 * 
	 * @param op1
	 * @param op2
	 * @return
	 */
	public static byte[] xor(byte[] op1, byte[] op2) {
		int len = op1.length;
		byte[] out = new byte[len];
		for (int i = 0; i < len; i++) {
			out[i] = (byte) (op1[i] ^ op2[i]);
		}
		return out;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getSubmitTime() {
		Calendar c;
		try {
			c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		} catch (Exception e) {
			c = null;
		}
		Date date = new Date();
		if (c != null) {
			date = c.getTime();
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date);
	}

	public static String formatUpayTime(String time) {
		StringBuilder sb = new StringBuilder();
		sb.append(time.substring(0, 4));
		sb.append("-");
		sb.append(time.substring(4, 6));
		sb.append("-");
		sb.append(time.substring(6, 8));
		sb.append(" ");

		sb.append(time.substring(8, 10));
		sb.append(":");
		sb.append(time.substring(10, 12));
		sb.append(":");
		sb.append(time.substring(12, 14));
		return sb.toString();
	}

	/**
	 * The price into a string with a decimal point Only used in the display
	 * above !!!
	 * 
	 * @param price
	 *            (fen)
	 * @return converted price
	 */
	public static String convertMoneyForDisplay(String price) {
		String fenStr = null;
		boolean isNegative = false;
		long longPrice = Long.parseLong(price);
		if (longPrice < 0) {
			isNegative = true;
			longPrice = -longPrice;
		}
		int fen = (int) (longPrice % 100);

		fenStr = Integer.toString(fen);

		if (longPrice % 100 < 10) {
			fenStr = "0" + fenStr;
		} else {

		}
		if (isNegative) {
			return "￥-" + Long.toString(longPrice / 100) + "." + fenStr;
		} else {
			return "￥" + Long.toString(longPrice / 100) + "." + fenStr;
		}
	}

	private static String[] hexs = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };
	private static String[] bins = new String[] { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
			"1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111" };

	// 将十进制数hex转换为二进制数并返回
	public static String convertHexToBin(String hex) {
		StringBuffer buff = new StringBuffer();
		int i;
		for (i = 0; i < hex.length(); i++) {
			buff.append(getBin(hex.substring(i, i + 1)));
		}
		return buff.toString();
	}

	// 将二进制数bin转换为十六进制数并返回
	public static String convertBinToHex(String bin) {
		StringBuffer buff = new StringBuffer(bin);
		int i;
		if (bin.length() % 4 != 0) {// 左补零
			for (i = 0; i < (4 - bin.length() % 4); i++) {
				buff.insert(0, "0");
			}
		}
		bin = buff.toString();
		buff = new StringBuffer();

		for (i = 0; i < bin.length(); i += 4) {
			buff.append(getHex(bin.substring(i, i + 4)));
		}
		return buff.toString();
	}

	// 返回十六进制数的二进制形式
	private static String getBin(String hex) {
		int i;
		for (i = 0; i < hexs.length && !hex.toLowerCase().equals(hexs[i]); i++)
			;
		return bins[i];
	}

	// 返回二进制数的十六进制形式 //
	private static String getHex(String bin) {
		int i;
		for (i = 0; i < bins.length && !bin.equals(bins[i]); i++)
			;
		return hexs[i];
	}

	/**
	 * 将byte数组转换成16进制组成的字符串 例如 一个byte数组 b[0]=0x07;b[1]=0x10;...b[5]=0xFB;
	 * byte2hex(b); 将返回一个字符串"0710BE8716FB"
	 * 
	 * @param bytes
	 *            待转换的byte数组
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		StringBuffer buff = new StringBuffer();
		int len = bytes.length;
		for (int j = 0; j < len; j++) {
			if ((bytes[j] & 0xff) < 16) {
				buff.append('0');
			}
			buff.append(Integer.toHexString(bytes[j] & 0xff));
		}
		return buff.toString();
	}

	/**
	 * 将byte数组转换成16进制组成的字符串 例如 一个byte数组 b[0]=0x07;b[1]=0x10;...b[5]=0xFB;
	 * byte2hex(b); 将返回一个字符串"0710BE8716FB"
	 * 
	 * @param src 待转换的byte数组
	 * @param len
	 * @return
	 */
	public static String bytesToHexString(byte[] src, int len) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0 || len <= 0) {
			return null;
		}
		for (int i = 0; i < len; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static JSONObject stringToJson(String orderInfo) {
		if (orderInfo == null)
			return null;
		try {
			return new JSONObject(orderInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject bytesToJSONObject(byte[] response) {
		String string = null;
		try {
			string = new String(response, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return stringToJson(string);
	}
	
	/**
	 * 处理字符串转JSONArray工具
	 * 
	 * @param json
	 * @return
	 */
	public static JSONArray stringToJSONArray(String json) {
		JSONArray array = new JSONArray();
		if(TextUtils.isEmpty(json)){
			return array;
		}
		try {
			array = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}
	
	/**
	 * JsonObject转Map
	 * @param jsonObject
	 * @return
	 */
	public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
		final Map<String, Object> jsonMap = new HashMap<String, Object>();
		for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
			String key = (String) iter.next();
			try {
				jsonMap.put(key, jsonObject.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonMap;
	}

	/**
	 * 将byte数组转换成16进制组成的字符串 例如 一个byte数组 b[0]=0x07;b[1]=0x10;...b[5]=0xFB;
	 * byte2hex(b); 将返回一个字符串"0710BE8716FB"
	 *
	 * @param buffer
	 *            待转换的byte数组
	 * @return
	 */
	public static String byteToHex(byte[] buffer) {
		StringBuffer hexString = new StringBuffer();
		String hex;
		int iValue;

		for (int i = 0; i < buffer.length; i++) {
			iValue = buffer[i];
			if (iValue < 0)
				iValue += 256;

			hex = Integer.toString(iValue, 16);
			if (hex.length() == 1)
				hexString.append("0" + hex);
			else
				hexString.append(hex);
		}

		return hexString.toString().toUpperCase();
	}

	/**
	 * 组建请求数据格式
	 * @param infoMap
	 * @return
	 */
	public static String mapToJSONStr(final Map<String, Object> infoMap) {
		try {
			LogUtil.d(TAG,"infoMap: "+infoMap.toString());
			final JSONObject info = mapToJSONObj(infoMap);
			LogUtil.d(TAG,"请求报文："+info.toString());
			return info.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	public static JSONObject mapToJSONObj(final Map<String, Object> infoMap) throws JSONException {
		JSONObject info = new JSONObject();
		if (infoMap != null && !infoMap.isEmpty()) {
			Iterator<Map.Entry<String, Object>> it = infoMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
				info.put(entry.getKey().trim(), entry.getValue());
			}
		}
		return info;
	}

}
