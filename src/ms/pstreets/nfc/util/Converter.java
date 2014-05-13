//------------------------------------------------------------------------------
//                         COPYRIGHT 2011 GUIDEBEE
//                           ALL RIGHTS RESERVED.
//                     GUIDEBEE CONFIDENTIAL PROPRIETARY
///////////////////////////////////// REVISIONS ////////////////////////////////
// Date       Name                 Tracking #         Description
// ---------  -------------------  ----------         --------------------------
// 13SEP2011  James Shen                 	          Initial Creation
////////////////////////////////////////////////////////////////////////////////
//--------------------------------- PACKAGE ------------------------------------
package ms.pstreets.nfc.util;

import java.io.ByteArrayOutputStream;

//[------------------------------ MAIN CLASS ----------------------------------]
//--------------------------------- REVISIONS ----------------------------------
//Date       Name                 Tracking #         Description
//--------   -------------------  -------------      --------------------------
//13SEP2011  James Shen                 	         Initial Creation
////////////////////////////////////////////////////////////////////////////////
/**
* Convert help class.
* <hr>
* <b>&copy; Copyright 2011 Guidebee, Inc. All Rights Reserved.</b>
* 
* @version 1.00, 13/09/11
* @author Guidebee Pty Ltd.
*/
public class Converter {

	// Hex help
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };

	////////////////////////////////////////////////////////////////////////////
	//--------------------------------- REVISIONS ------------------------------
	// Date       Name                 Tracking #         Description
	// ---------  -------------------  -------------      ----------------------
	// 13SEP2011  James Shen                 	          Initial Creation
	////////////////////////////////////////////////////////////////////////////
	
	 /**  
     * 用于建立十六进制字符的输出的小写字符数组  
     */  
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',   
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };   
  
    /**  
     * 用于建立十六进制字符的输出的大写字符数组  
     */  
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',   
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };   
  
    /**  
     * 将字节数组转换为十六进制字符数组  
     *   
     * @param data  
     *            byte[]  
     * @return 十六进制char[]  
     */  
    public static char[] encodeHex(byte[] data) {   
        return encodeHex(data, true);   
    }   
  
    /**  
     * 将字节数组转换为十六进制字符数组  
     *   
     * @param data  
     *            byte[]  
     * @param toLowerCase  
     *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式  
     * @return 十六进制char[]  
     */  
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {   
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);   
    }   
  
    /**  
     * 将字节数组转换为十六进制字符数组  
     *   
     * @param data  
     *            byte[]  
     * @param toDigits  
     *            用于控制输出的char[]  
     * @return 十六进制char[]  
     */  
    protected static char[] encodeHex(byte[] data, char[] toDigits) {   
        int l = data.length;   
        char[] out = new char[l << 1];   
        // two characters form the hex value.   
        for (int i = 0, j = 0; i < l; i++) {   
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];   
            out[j++] = toDigits[0x0F & data[i]];   
        }   
        return out;   
    }   
  
    /**  
     * 将字节数组转换为十六进制字符串  
     *   
     * @param data  
     *            byte[]  
     * @return 十六进制String  
     */  
    public static String encodeHexStr(byte[] data) {   
        return encodeHexStr(data, true);   
    }   
  
    /**  
     * 将字节数组转换为十六进制字符串  
     *   
     * @param data  
     *            byte[]  
     * @param toLowerCase  
     *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式  
     * @return 十六进制String  
     */  
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {   
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);   
    }   
  
    /**  
     * 将字节数组转换为十六进制字符串  
     *   
     * @param data  
     *            byte[]  
     * @param toDigits  
     *            用于控制输出的char[]  
     * @return 十六进制String  
     */  
    protected static String encodeHexStr(byte[] data, char[] toDigits) {   
        return new String(encodeHex(data, toDigits));   
    }   
  
    /**  
     * 将十六进制字符数组转换为字节数组  
     *   
     * @param data  
     *            十六进制char[]  
     * @return byte[]  
     * @throws RuntimeException  
     *             如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常  
     */  
    public static byte[] decodeHex(char[] data) {   
  
        int len = data.length;   
  
        if ((len & 0x01) != 0) {   
            throw new RuntimeException("Odd number of characters.");   
        }   
  
        byte[] out = new byte[len >> 1];   
  
        // two characters form the hex value.   
        for (int i = 0, j = 0; j < len; i++) {   
            int f = toDigit(data[j], j) << 4;   
            j++;   
            f = f | toDigit(data[j], j);   
            j++;   
            out[i] = (byte) (f & 0xFF);   
        }   
  
        return out;   
    }   
  
    /**  
     * 将十六进制字符转换成一个整数  
     *   
     * @param ch  
     *            十六进制char  
     * @param index  
     *            十六进制字符在字符数组中的位置  
     * @return 一个整数  
     * @throws RuntimeException  
     *             当ch不是一个合法的十六进制字符时，抛出运行时异常  
     */  
    protected static int toDigit(char ch, int index) {   
        int digit = Character.digit(ch, 16);   
        if (digit == -1) {   
            throw new RuntimeException("Illegal hexadecimal character " + ch   
                    + " at index " + index);   
        }   
        return digit;   
    }   
  
	/**
	 * convert a byte arrary to hex string
	 * @param raw byte arrary
	 * @param len lenght of the arrary.
	 * @return hex string.
	 */
	public static String getHexString(byte[] raw, int len) {
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= len)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		String str = "";
		try{
			str = new String(decodeHex(byte2hex(raw).toCharArray()),"GB2312");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return str;
//		return hex2bin(byte2hex(raw));
	}
	
	 /** 
     * java字节码转字符串 
     * @param b 
     * @return 
     */
    public static String byte2hex(byte[] b) { //一个字节的数，

        // 转成16进制字符串

        String hs = "";
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            //整数转成十六进制表示

            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //转成大写

    }
    
    /**
     * 十六进制转换字符串
     * @param hex String 十六进制
     * @return String 转换后的字符串
     */
    public static String hex2bin(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return new String(bytes);
    }
    
    private static String hexString="0123456789ABCDEF"; 
	/*
	* 将字符串编码成16进制数字,适用于所有字符（包括中文）
	*/
	public static String encode(String str)
	{
		//根据默认编码获取字节数组
		byte[] bytes=str.getBytes();
		StringBuilder sb=new StringBuilder(bytes.length*8);
		//将字节数组中每个字节拆解成2位16进制整数
		for(int i=0;i<bytes.length;i++)
		{
		sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
		sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
		}
		return sb.toString();
	}
	/*
	* 将16进制数字解码成字符串,适用于所有字符（包括中文）
	*/
	public static String decode(String bytes)
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/4);
		//将每2位16进制整数组装成一个字节
		for(int i=0;i<bytes.length();i+=2)
		baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		return new String(baos.toByteArray()); 
	}
	
	/**
	 * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。  
	 * @param src byte[] data  
	 * @return hex string  
	 */     
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString().toUpperCase();
	}  
	/** 
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}
	
	/** 
	 * Convert char to byte 
	 * @param c char 
	 * @return byte 
	 */  
	private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
	 
}
