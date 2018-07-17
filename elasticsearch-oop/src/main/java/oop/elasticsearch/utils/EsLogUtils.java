package oop.elasticsearch.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EsLogUtils {
    /**
     * info日志打印
     * @param messagePattern
     * @param argArray
     */
    public static final void info(String messagePattern, Object... argArray) {
        StringBuffer info = new StringBuffer("[elasticsearch-oop]-[info]");
        Date msgDatetime = new Date();
        info.append("-[").append(msgDatetime).append("]");
        // 待处理： 代理获取调用类的类名信息、方法名信息等等
        String msgBodyStr = EsParseMsgUtils.parseMsg(messagePattern,argArray);
        info.append("-[").append(msgBodyStr).append("]");
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        System.out.println(info.toString());
    }
    /**
     * error日志打印
     * @param messagePattern
     * @param argArray
     */
    public static final void error(String messagePattern, Object... argArray) {
        StringBuffer info = new StringBuffer("[elasticsearch-oop]-[error]");
        Date msgDatetime = new Date();
        info.append("-[").append(msgDatetime).append("]");
        // 待处理： 代理获取调用类的类名信息、方法名信息等等
        String msgBodyStr = EsParseMsgUtils.parseMsg(messagePattern,argArray);
        info.append("-[").append(msgBodyStr).append("]");
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        System.out.println(info.toString());
    }
    /**
     * debug日志打印
     * @param messagePattern
     * @param argArray
     */
    public static final void debug(String messagePattern, Object... argArray) {
        StringBuffer info = new StringBuffer("[elasticsearch-oop]-[debug]");
        Date msgDatetime = new Date();
        info.append("-[").append(msgDatetime).append("]");
        // 待处理： 代理获取调用类的类名信息、方法名信息等等
        String msgBodyStr = EsParseMsgUtils.parseMsg(messagePattern,argArray);
        info.append("-[").append(msgBodyStr).append("]");
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        // 待处理： 这些info信息可以转储到其他地方，形成服务日志
        System.out.println(info.toString());
    }

}
