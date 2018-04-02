package oop.elasticsearch.config;

import oop.elasticsearch.utils.EsLogUtils;
import oop.elasticsearch.utils.EsUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * sqlite的一些静态配置
 */
public class ElasticsearchConfig {

    /**
     * sqlite配置文件的配置信息
     */
    private static Properties properties = new Properties();

    /**
     * 启动程序的时候读取properties配置文件信息，并永久缓存
     */
    static {
        try {
            //使用 properties 配置文件，默认在 config/elasticsearch.properties 目录下面，若该项目被引用，启动项目只需要在相同目录下相同配置文件覆盖即可生效
            properties.load(ElasticsearchConfig.class.getClassLoader().getResourceAsStream("config/elasticsearch.properties"));
            //properties.loadFromXML(ElasticsearchConfig.class.getClassLoader().getResourceAsStream("config/elasticsearch.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据key得到value的值
     */
    public static String getValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * 默认要配置的参数：elasticsearch集群名
     * @return
     */
    public static String getClusterName() {
        return properties.getProperty("elasticsearch.clusterName");
    }
    /**
     * 默认要配置的参数：集群节点集
     * @return
     */
    public static String getClusterNodes() {
        return properties.getProperty("elasticsearch.clusterNodes");
    }
    /**
     * 默认要配置的参数：集群节点集
     * @return
     */
    public static String getPingTimeout() {
        return properties.getProperty("elasticsearch.client.pingTimeout");
    }

    /**
     * 获取client
     * @return
     */
    public static TransportClient getClient() {
        return getClient(null,null,null);
    }
    /**
     * 获取client
     * @param esClusterName
     * @param esClusterNodes
     * @param pingTimeout
     * @return
     */
    public static TransportClient getClient(String esClusterName, String esClusterNodes,String pingTimeout) {
        if(EsUtils.isBlank(esClusterName)){
            esClusterName = getClusterName();
        }
        if(EsUtils.isBlank(esClusterNodes)){
            esClusterNodes = getClusterNodes();
        }
        if(EsUtils.isBlank(pingTimeout)){
            pingTimeout = getPingTimeout();
        }
        if(EsUtils.isBlank(pingTimeout)){
            pingTimeout = "60s";
        }

        EsLogUtils.info("Building ElasticSearch Client  IP地址：" + esClusterNodes + " clusterName: " + esClusterName);
        Settings settings = Settings.builder()
                .put("cluster.name", esClusterName)
                .put("client.transport.sniff", true)
                .put("client.transport.ping_timeout", pingTimeout)
                .build();
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings);
        } catch (Exception e) {
            EsLogUtils.error("连接ElasticSearch异常", e);
            return client;
        }
        for (String nodes : esClusterNodes.split(",")) {
            String[] split = nodes.split(":");
            InetAddress ip = null;
            Integer port = null;
            try {
                ip = InetAddress.getByName(split[0]);
                port = Integer.valueOf(split[1]);
            } catch (UnknownHostException e) {
                EsLogUtils.error("连接ElasticSearch时解析IP异常", e);
                continue;
            }
            client.addTransportAddress(new InetSocketTransportAddress(ip, port));
        }
        return client;
    }
}
