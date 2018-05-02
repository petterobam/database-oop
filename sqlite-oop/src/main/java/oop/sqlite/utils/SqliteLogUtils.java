package oop.sqlite.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SqliteLogUtils {
    /**
     * info日志打印
     * @param messagePattern
     * @param argArray
     */
    public static final void info(String messagePattern, Object... argArray) {
        StringBuffer info = new StringBuffer("[sqlite-oop]-[info]");
        info.append("-[").append(new Date()).append("]");
        // 待处理： 代理获取调用类的类名信息、方法名信息等等
        info.append("-[").append(print(messagePattern,argArray)).append("]");
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        System.out.println(info.toString());
    }
    /**
     * error日志打印
     * @param messagePattern
     * @param argArray
     */
    public static final void error(String messagePattern, Object... argArray) {
        StringBuffer info = new StringBuffer("[sqlite-oop]-[error]");
        info.append("-[").append(new Date()).append("]");
        // 待处理： 代理获取调用类的类名信息、方法名信息等等
        info.append("-[").append(print(messagePattern,argArray)).append("]");
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        System.out.println(info.toString());
    }
    /**
     * debug日志打印
     * @param messagePattern
     * @param argArray
     */
    public static final void debug(String messagePattern, Object... argArray) {
        StringBuffer info = new StringBuffer("[sqlite-oop]-[debug]");
        info.append("-[").append(new Date()).append("]");
        // 待处理： 代理获取调用类的类名信息、方法名信息等等
        info.append("-[").append(print(messagePattern,argArray)).append("]");
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        System.out.println(info.toString());
    }
    /**
     * 打印日志信息
     * @param messagePattern
     * @param argArray
     * @return
     */
    public static final String print(String messagePattern, Object[] argArray) {
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        Throwable throwableCandidate = getThrowableCandidate(argArray);
        Object[] args = argArray;
        if (throwableCandidate != null) {
            args = trimmedCopy(argArray);
        }

        if (argArray == null && messagePattern != null) {
            sbuf.append(messagePattern);
        } else {
            int i = 0;
            for(int L = 0; L < argArray.length; ++L) {
                int j = messagePattern.indexOf("{}", i);
                if (j == -1) {
                    break;
                }

                if (isEscapedDelimeter(messagePattern, j)) {
                    if (!isDoubleEscaped(messagePattern, j)) {
                        --L;
                        sbuf.append(messagePattern, i, j - 1);
                        sbuf.append('{');
                        i = j + 1;
                    } else {
                        sbuf.append(messagePattern, i, j - 1);
                        deeplyAppendParameter(sbuf, argArray[L], new HashMap());
                        i = j + 2;
                    }
                } else {
                    sbuf.append(messagePattern, i, j);
                    deeplyAppendParameter(sbuf, argArray[L], new HashMap());
                    i = j + 2;
                }
            }
            sbuf.append(messagePattern, i, messagePattern.length());
            //该日志信息可以转储
            System.out.println(sbuf.toString());
        }
        if(null != throwableCandidate){
            //throwableCandidate.printStackTrace();
            sbuf.append(throwableCandidate.getLocalizedMessage());
        }
        return sbuf.toString();
    }

    static final Throwable getThrowableCandidate(Object[] argArray) {
        if (argArray != null && argArray.length != 0) {
            Object lastEntry = argArray[argArray.length - 1];
            return lastEntry instanceof Throwable ? (Throwable)lastEntry : null;
        } else {
            return null;
        }
    }
    private static Object[] trimmedCopy(Object[] argArray) {
        if (argArray != null && argArray.length != 0) {
            int trimemdLen = argArray.length - 1;
            Object[] trimmed = new Object[trimemdLen];
            System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
            return trimmed;
        } else {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
    }
    static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        } else {
            char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
            return potentialEscape == '\\';
        }
    }
    static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\';
    }
    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            sbuf.append("null");
        } else {
            if (!o.getClass().isArray()) {
                safeObjectAppend(sbuf, o);
            } else if (o instanceof boolean[]) {
                booleanArrayAppend(sbuf, (boolean[])((boolean[])o));
            } else if (o instanceof byte[]) {
                byteArrayAppend(sbuf, (byte[])((byte[])o));
            } else if (o instanceof char[]) {
                charArrayAppend(sbuf, (char[])((char[])o));
            } else if (o instanceof short[]) {
                shortArrayAppend(sbuf, (short[])((short[])o));
            } else if (o instanceof int[]) {
                intArrayAppend(sbuf, (int[])((int[])o));
            } else if (o instanceof long[]) {
                longArrayAppend(sbuf, (long[])((long[])o));
            } else if (o instanceof float[]) {
                floatArrayAppend(sbuf, (float[])((float[])o));
            } else if (o instanceof double[]) {
                doubleArrayAppend(sbuf, (double[])((double[])o));
            } else {
                objectArrayAppend(sbuf, (Object[])((Object[])o), seenMap);
            }

        }
    }
    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable var3) {
            System.out.println("EsLogUtils: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]");
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, (Object)null);
            int len = a.length;

            for(int i = 0; i < len; ++i) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1) {
                    sbuf.append(", ");
                }
            }

            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }

        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }
}
