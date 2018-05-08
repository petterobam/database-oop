package oop.elasticsearch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EsUtils {
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToStr(InputStream is) {
        return convertStreamToStr(is,null);
    }
    /**
     * 输入流转字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToStr(InputStream is, String charset) {
        StringBuilder sb = new StringBuilder("");
        try {
            if (isBlank(charset)) {
                charset = "UTF-8";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));

            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
