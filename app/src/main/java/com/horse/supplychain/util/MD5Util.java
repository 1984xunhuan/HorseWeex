package com.horse.supplychain.util;

import java.security.MessageDigest;

public class MD5Util {
    /**
     * 对字符串进行MD5加密。
     */
    public static String encryptMD5(String strInput) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(strInput.getBytes("UTF-8"));
            byte b[] = md.digest();
            buf = new StringBuffer(b.length * 2);
            for (int i = 0; i < b.length; i++) {
                if (((int) b[i] & 0xff) < 0x10) { /* & 0xff转换无符号整型 */
                    buf.append("0");
                }
                buf.append(Long.toHexString((int) b[i] & 0xff)); /* 转换16进制,下方法同 */
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buf.toString();
    }
}
