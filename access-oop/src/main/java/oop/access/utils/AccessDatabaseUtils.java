package oop.access.utils;

import com.healthmarketscience.jackcess.CryptCodecProvider;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import oop.access.config.AccessConfig;
import oop.access.constant.AccessFormatEnum;

import java.io.File;
import java.io.IOException;

/**
 * Access数据库工具类
 *
 * @author 欧阳洁
 * @since 2018-05-31 9:52
 */
public class AccessDatabaseUtils {
    /**
     * 记录以及创建了的数据库链接
     */
    private static String EXIST_DB_PATHS = "";

    /**
     * 创建Access库文件
     *
     * @param dbPath
     * @return
     */
    public static boolean createDatabaseFile(String dbPath) {
        return createDatabaseFile(dbPath, null);
    }

    /**
     * 创建带密码的Access库文件
     *
     * @param dbPath
     * @param password
     * @return
     */
    public static boolean createDatabaseFile(String dbPath, String password) {
        if (AccessDatabaseUtils.isExistDbPath(dbPath)) {
            return true;
        }
        File file = new File(dbPath);
        if (!file.exists()) {
            Database db = null;
            try {
                Database.FileFormat fileFormat = AccessDatabaseUtils.getAccessFormat(dbPath);
                DatabaseBuilder dbd = new DatabaseBuilder(file);
                dbd.setAutoSync(false);
                dbd.setFileFormat(fileFormat);
                dbd.setReadOnly(false);
                if (!AccessUtils.isBlank(password)) {
                    dbd.setCodecProvider(new CryptCodecProvider(password));
                }
                db = dbd.create();
                AccessDatabaseUtils.addExistDbPath(dbPath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (null != db) {
                    try {
                        db.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            AccessDatabaseUtils.addExistDbPath(dbPath);
        }
        return true;
    }

    /**
     * 是否已经存在了
     *
     * @param dbPath
     * @return
     */
    public static boolean isExistDbPath(String dbPath) {
        return EXIST_DB_PATHS.indexOf(dbPath) >= 0;
    }

    /**
     * 判断库中表是否存在
     * @param dbPath
     * @param tableName
     * @return
     */
    public static boolean isTableExist(String dbPath, String tableName) {
        return AccessDatabaseUtils.isTableExist(dbPath,tableName,null);
    }
    /**
     * 判断库中表是否存在
     * @param dbPath
     * @param tableName
     * @param password
     * @return
     */
    public static boolean isTableExist(String dbPath, String tableName, String password) {
        if(AccessUtils.isBlank(tableName) || AccessUtils.isBlank(dbPath)){
            return false;
        }
        File file = new File(dbPath);
        if (file.exists()) {
            Database db = null;
            try {
                Database.FileFormat fileFormat = AccessDatabaseUtils.getAccessFormat(dbPath);
                DatabaseBuilder dbd = new DatabaseBuilder(file);
                dbd.setAutoSync(false);
                dbd.setFileFormat(fileFormat);
                dbd.setReadOnly(false);
                if (!AccessUtils.isBlank(password)) {
                    dbd.setCodecProvider(new CryptCodecProvider(password));
                }
                db = dbd.open();
                return db.getTable(tableName) != null;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (null != db) {
                    try {
                        db.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    /**
     * 添加已经存在的数据库
     *
     * @param dbPath
     */
    public static void addExistDbPath(String dbPath) {
        EXIST_DB_PATHS = EXIST_DB_PATHS + "|" + dbPath;
    }

    /**
     * 获取 库版本格式
     *
     * @return
     */
    public static Database.FileFormat getAccessFormat() {
        return AccessDatabaseUtils.getAccessFormat(null);
    }

    /**
     * 获取 库版本格式
     *
     * @param dbPath 库文件地址
     * @return
     */
    public static Database.FileFormat getAccessFormat(String dbPath) {
        String accessFormat = AccessConfig.getAccessFormat();
        if (!AccessUtils.isBlank(accessFormat)) {
            return AccessFormatEnum.getFileFormatByName(accessFormat);
        } else if (!AccessUtils.isBlank(dbPath)) {
            int extIndex = dbPath.indexOf(".");
            if (extIndex <= 0) {
                return Database.FileFormat.V2003;
            }
            String ext = dbPath.substring(extIndex);
            if (".mdb".equals(ext)) {
                return Database.FileFormat.V2003;
            } else if (".accdb".equals(ext)) {
                return Database.FileFormat.V2010;
            } else if (".mny".equals(ext)) {
                return Database.FileFormat.MSISAM;
            } else {
                return Database.FileFormat.V2003;
            }
        } else {
            return Database.FileFormat.V2003;
        }
    }
}
