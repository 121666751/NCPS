
package com.adtec.ncps.busi.chnl.utils;


/**
 * 十六进制帮助类
 * @author jacky
 */
public class HexUtils {
    /** 转换数据 */
    private static final char[] HEXDIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    /**
     * toString(控制长度的将byte[]的转换为相应的十六进制String表示)
     */
    public static String toString(byte[] ba, int offset, int length) {
        char[] buf = new char[length * 2];
        int j = 0;
        int k;
        for (int i = offset; i < offset + length; i++) {
            k = ba[i];
            buf[j++] = HEXDIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEXDIGITS[k & 0x0F];
        }
        return new String(buf);
    }
    /**
     * 功能:将byte[]的转换为相应的十六进制字符串
     * @param ba 字节数组
     * @return 十六进制字符串
     */
    public static String toString(byte[] ba) {
        return toString(ba, 0, ba.length);
    }
    /**
     *  功能:将十六进制字符串转换为字节数组
     * @param hex 十六进制字符串
     * @return 字节数组
     */
    public static byte[] fromString(String hex) {
        int len = hex.length();
        byte[] buf = new byte[(len + 1) / 2];
        int i = 0;
        int j = 0;
        if ((len % 2) == 1) {
            buf[j++] = (byte) fromDigit(hex.charAt(i++));
        }
        while (i < len) {
            buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) | fromDigit(hex.charAt(i++)));
        }
        return buf;
    }
    /**
     * fromDigit(将十六进制的char转换为十进制的int值)
     */
    public static int fromDigit(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a' + 10;
        }
        throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
    }
}
