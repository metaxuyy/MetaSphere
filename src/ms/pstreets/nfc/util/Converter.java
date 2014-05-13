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
     * ���ڽ���ʮ�������ַ��������Сд�ַ�����  
     */  
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',   
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };   
  
    /**  
     * ���ڽ���ʮ�������ַ�������Ĵ�д�ַ�����  
     */  
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',   
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };   
  
    /**  
     * ���ֽ�����ת��Ϊʮ�������ַ�����  
     *   
     * @param data  
     *            byte[]  
     * @return ʮ������char[]  
     */  
    public static char[] encodeHex(byte[] data) {   
        return encodeHex(data, true);   
    }   
  
    /**  
     * ���ֽ�����ת��Ϊʮ�������ַ�����  
     *   
     * @param data  
     *            byte[]  
     * @param toLowerCase  
     *            <code>true</code> ������Сд��ʽ �� <code>false</code> �����ɴ�д��ʽ  
     * @return ʮ������char[]  
     */  
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {   
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);   
    }   
  
    /**  
     * ���ֽ�����ת��Ϊʮ�������ַ�����  
     *   
     * @param data  
     *            byte[]  
     * @param toDigits  
     *            ���ڿ��������char[]  
     * @return ʮ������char[]  
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
     * ���ֽ�����ת��Ϊʮ�������ַ���  
     *   
     * @param data  
     *            byte[]  
     * @return ʮ������String  
     */  
    public static String encodeHexStr(byte[] data) {   
        return encodeHexStr(data, true);   
    }   
  
    /**  
     * ���ֽ�����ת��Ϊʮ�������ַ���  
     *   
     * @param data  
     *            byte[]  
     * @param toLowerCase  
     *            <code>true</code> ������Сд��ʽ �� <code>false</code> �����ɴ�д��ʽ  
     * @return ʮ������String  
     */  
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {   
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);   
    }   
  
    /**  
     * ���ֽ�����ת��Ϊʮ�������ַ���  
     *   
     * @param data  
     *            byte[]  
     * @param toDigits  
     *            ���ڿ��������char[]  
     * @return ʮ������String  
     */  
    protected static String encodeHexStr(byte[] data, char[] toDigits) {   
        return new String(encodeHex(data, toDigits));   
    }   
  
    /**  
     * ��ʮ�������ַ�����ת��Ϊ�ֽ�����  
     *   
     * @param data  
     *            ʮ������char[]  
     * @return byte[]  
     * @throws RuntimeException  
     *             ���Դʮ�������ַ�������һ����ֵĳ��ȣ����׳�����ʱ�쳣  
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
     * ��ʮ�������ַ�ת����һ������  
     *   
     * @param ch  
     *            ʮ������char  
     * @param index  
     *            ʮ�������ַ����ַ������е�λ��  
     * @return һ������  
     * @throws RuntimeException  
     *             ��ch����һ���Ϸ���ʮ�������ַ�ʱ���׳�����ʱ�쳣  
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
     * java�ֽ���ת�ַ��� 
     * @param b 
     * @return 
     */
    public static String byte2hex(byte[] b) { //һ���ֽڵ�����

        // ת��16�����ַ���

        String hs = "";
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            //����ת��ʮ�����Ʊ�ʾ

            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //ת�ɴ�д

    }
    
    /**
     * ʮ������ת���ַ���
     * @param hex String ʮ������
     * @return String ת������ַ���
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
	* ���ַ��������16��������,�����������ַ����������ģ�
	*/
	public static String encode(String str)
	{
		//����Ĭ�ϱ����ȡ�ֽ�����
		byte[] bytes=str.getBytes();
		StringBuilder sb=new StringBuilder(bytes.length*8);
		//���ֽ�������ÿ���ֽڲ���2λ16��������
		for(int i=0;i<bytes.length;i++)
		{
		sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
		sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
		}
		return sb.toString();
	}
	/*
	* ��16�������ֽ�����ַ���,�����������ַ����������ģ�
	*/
	public static String decode(String bytes)
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/4);
		//��ÿ2λ16����������װ��һ���ֽ�
		for(int i=0;i<bytes.length();i+=2)
		baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		return new String(baos.toByteArray()); 
	}
	
	/**
	 * Convert byte[] to hex string.�������ǿ��Խ�byteת����int��Ȼ������Integer.toHexString(int)��ת����16�����ַ�����  
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
