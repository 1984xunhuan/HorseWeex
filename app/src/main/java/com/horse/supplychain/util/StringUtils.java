package com.horse.supplychain.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName StringUtils
 * @Description 字符串处理工具类
 *
 */
public class StringUtils {

	private static final String TAG = "StringUtils";

	/**
	 * 判断obj 是否为空
	 * @param obj
	 * @return
	 */
	public static boolean objectIsEmpty(Object obj)
	{
		if (obj == null)
		{
			return true;
		}
		if ((obj instanceof List))
		{
			return ((List) obj).size() == 0;
		}
		if ((obj instanceof String))
		{
			return ((String) obj).trim().equals("");
		}
		return false;
	}
	
	/**
     * @Description: 判断String是否为空, true代表空，false代表非空 @param string @return boolean @throws
     */
    public static boolean isNullOrEmpty(String string) {
        if (string == null) {
            return true;
        }
        else if ("".equalsIgnoreCase(string.trim())) {
            return true;
        }
        return false;
    }

	
	/**
	 * 字节码数据 转 字符串工具
	 */
	public static String byteToString(byte[] data) {
		if (data == null)
			return null;
		try {
			return new String(data, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 字符串转JSONObject工具
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject stringToJSONObject(String json) {
		if (json == null)
			return null;
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 处理字符串转JSONArray工具
	 * 
	 * @param json
	 * @return
	 */
	public static JSONArray stringToJSONArray(String json) {
		JSONArray array = new JSONArray();
		try {
			array = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}

	/**
	 * 字节码数据(字符)转JSONObject JSONObject
	 * 
	 * @param data
	 * @return
	 */
	public static JSONObject bytesToJSONObject(byte[] data) {
		if (data == null)
			return null;
		return stringToJSONObject(byteToString(data));
	}

	/**
	 * 网址汉字编码
	 */
	public static String urlEncode(String str) {
		StringBuffer buf = new StringBuffer();
		byte c;
		byte[] utfBuf;
		try {
			utfBuf = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("URLEncode: Failed to get UTF-8 bytes from string.");
			utfBuf = str.getBytes();
		}
		for (int i = 0; i < utfBuf.length; i++) {
			c = utfBuf[i];
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
					|| (c == '.' || c == '-' || c == '*' || c == '_')
					|| (c == ':' || c == '/' || c == '=' || c == '?' || c == '&' || c == '%')) {
				buf.append((char) c);
			} else {
				buf.append("%").append(Integer.toHexString((0x000000FF & c)));
			}
		}
		return buf.toString();
	}
	
	/**
	 * 返回左边补0的数字字符串
	 * @param number
	 * @param width
	 * @return
	 */
	public static String getLeftZeroNumString(String number, int width) {
		try {
			int len = width - number.getBytes("utf-8").length;

			for (int i = 0; i < len; i++) {
				number = "0" + number;
			}

			return number;
		} catch (Exception e) {

		}

		return number;
	}

	/**
	 * 返回右边补空格的指定位数的字符串
	 * @param str
	 * @param width
	 * @return
	 */
	public static String getRightBlankString(String str, int width) {
		try {
			int len = width - str.getBytes("utf-8").length;

			for (int i = 0; i < len; i++) {
				str = str + " ";
			}

			return str;
		} catch (Exception e) {

		}

		return str;
	}
	
	/**
	 * 返回BCD码表示的指定长度数字字符串
	 * modified by xuxiang 
	 * int len = width - bcdString.length();
	 * 改为
	 * int len = width - bcdString.getBytes("utf-8").length;
	 * @param number
	 * @param width 总长度
	 * @return
	 */
	public static String getFormatedNumBcdString(int number, int width) {
		String bcdString = Integer.toString(number);
		try {
			int len = width - bcdString.getBytes("utf-8").length;
			for (int i = 0; i < len; i++) {
				bcdString = "0" + bcdString;
			}
		} catch (Exception e){
		}
		
		return bcdString;
	}
	
	/**
	 * 返回BCD码表示的指定长度数字字符串
	 * @param bcdString 用十六进制表示的长度
	 * @param width 总长度
	 * @return
	 */
	public static String getFormatedNumBcdString(String bcdString, int width) {
		try {
			int len = width - bcdString.getBytes("utf-8").length;
			for (int i = 0; i < len; i++) {
				bcdString = "0" + bcdString;
			}
		} catch (Exception e){
		}
		
		return bcdString;
	}
	
	 /**
     * 空白字符。
     */
    private static final char[] WhitespaceChars = new char[] { '\u0000', '\u0001',
        '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
        '\u0008', '\u0009', '\n', '\u000b', '\u000c', '\r', '\u000e',
        '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014',
        '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a',
        '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '\u0020'

    };
	
	/**
     * 移除给定字符串结尾的空白字符。
     * 
     * @param value
     *            要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static final String trimEnd(final String value) {
        return trimEnd(value, WhitespaceChars);
    }
    
    /**
     * 移除给定字符串结尾处的特定字符。
     * 
     * @param value
     *            要修剪的字符串。
     * @param chars
     *            要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trimEnd(final String value, final char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int endIndex = value.length() - 1;
        boolean flag = containsChar(chars, value.charAt(endIndex));
        while (flag) {
            endIndex--;
            flag = containsChar(chars, value.charAt(endIndex));
        }
        if (0 >= endIndex) {
            return "";
        }
        return value.substring(0, endIndex + 1);
    }
    
    /**
     * 判断给定的字符数组（已排序）中是否包含给定的字符。
     * 
     * @param chars
     *            已经排序的字符数组。
     * @param ch
     *            要检查的字符。
     * @return 如果 ch 存在于 chars 中则返回 true；否则返回 false。
     */
    public static final boolean containsChar(final char[] chars, final char ch) {
        return Arrays.binarySearch(chars, ch) >= 0;
    }
    
    /**
     * 将字符串格式化为"0.0"格式
     * @param str
     * @return
     */
    public static String format(String str) {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.CHINA);
		df.applyPattern("0.0");
		return df.format(Double.parseDouble(str));
	}
    
    /**
     * 将字符串格式化为"0.00"格式
     * @param str
     * @return
     */
    public static String format2(String str) {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.CHINA);
		df.applyPattern("0.00");
		return df.format(Double.parseDouble(str));
	}
    
    /**
	 * 判断日期str是否在结束日期之前
	 * 
	 * @param str
	 * @param endTime
	 * @return
	 */
	public static boolean beoreEndTime(String str, String endTime) {
		if (str.length() != 10 || endTime.length() != 10) {
			return false;
		}
		int str1 = Integer.valueOf(str.substring(0, 4));
		int str2 = Integer.valueOf(str.substring(5, 7));
		int str3 = Integer.valueOf(str.substring(8, 10));
		int endTime1 = Integer.valueOf(endTime.substring(0, 4));
		int endTime2 = Integer.valueOf(endTime.substring(5, 7));
		int endTime3 = Integer.valueOf(endTime.substring(8, 10));
		LogUtil.d(TAG,str1 + "_" + str2 + "_" + str3);
		LogUtil.d(TAG,endTime1 + "_" + endTime2 + "_" + endTime3);
		if (str1 < endTime1) {
			return true;
		} else if (str1 == endTime1) {
			if (str2 < endTime2) {
				return true;
			} else if (str2 == endTime2) {
				if (str3 <= endTime3) {
					return true;
				} else {
					return false;
				}
			} else if (str2 > endTime2) {
				return false;
			}
		} else if (str1 > endTime1) {
			return false;
		}
		return false;
	}
	
	public static String makeString(char ch, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; ++i) {
			sb.append(ch);
		}
		return sb.toString();
	}
	
	/**
	 * 根据通道得到balanceType的值
	 * @param channelType
	 * @return
	 */
	public static String switchChannelToBalanceType(String channelType) {
		if ("0014".equals(channelType) || "0019".equals(channelType)) {
			// 银行卡
			return "1";
		} else if ("0031".equals(channelType) || "0030".equals(channelType) 
				|| "0033".equals(channelType) || "0032".equals(channelType)
				|| "0053".equals(channelType)) {
			// 微信
			return "9";
		} else if ("0052".equals(channelType) || "0051".equals(channelType)
				|| "0061".equals(channelType) || "0062".equals(channelType)) {
			// 支付宝
			return "10";
		} else if ("0042".equals(channelType)) {
			// 批量代收
			return "31";
		} else if ("0034".equals(channelType)) {
			// 实时代收
			return "34";
		} else if ("0024".equals(channelType)) {
			//现金
			return "3";
		} else if ("0035".equals(channelType)) {
			//转账
			return "12";
		}
		return "1";
	} 

	/** 
     * splitAry方法<br> 
     * @param ary 要分割的数组 
     * @param subSize 分割的块大小 
     * @return 
     * 
     */  
    public static Object[] splitAry(String[] ary, int subSize) {  
          int count = ary.length % subSize == 0 ? ary.length / subSize: ary.length / subSize + 1;  
  
          List<List<String>> subAryList = new ArrayList<List<String>>();  
  
          for (int i = 0; i < count; i++) {  
           int index = i * subSize;  
           List<String> list = new ArrayList<String>();  
           int j = 0;  
               while (j < subSize && index < ary.length) {  
                    list.add(ary[index++]);  
                    j++;  
               }  
           subAryList.add(list);  
          }  
            
          Object[] subAry = new Object[subAryList.size()];  
            
          for(int i = 0; i < subAryList.size(); i++){  
               List<String> subList = subAryList.get(i);  
               String[] subAryItem = new String[subList.size()];  
               for(int j = 0; j < subList.size(); j++){  
                   subAryItem[j] = subList.get(j);  
               }  
               subAry[i] = subAryItem;  
          }  
            
          return subAry;  
   } 
}
