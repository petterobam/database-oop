package oop.elasticsearch.utils;

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
}
