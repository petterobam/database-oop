package oop.access.constant;

import com.healthmarketscience.jackcess.Database;

/**
 * Access数据库版本格式枚举
 * Created by vetech on 2018/5/31.
 *
 * @author 欧阳洁
 */
public enum AccessFormatEnum {
    V1997(Database.FileFormat.V1997),
    GENERIC_JET4(Database.FileFormat.GENERIC_JET4),
    V2000(Database.FileFormat.V2000),
    V2003(Database.FileFormat.V2003),
    V2007(Database.FileFormat.V2007),
    V2010(Database.FileFormat.V2010),
    V2016(Database.FileFormat.V2016),
    MSISAM(Database.FileFormat.MSISAM);

    /**
     * 文件版本格式
     */
    private Database.FileFormat fileFormat;

    /**
     * 构造函数
     *
     * @param fileFormat
     */
    private AccessFormatEnum(Database.FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * 通过版本名获取版本格式
     *
     * @param versionName
     * @return
     */
    public static Database.FileFormat getFileFormatByName(String versionName) {
        for (AccessFormatEnum accessFormatEnum : AccessFormatEnum.values()) {
            if (accessFormatEnum.name().equals(versionName)) {
                return accessFormatEnum.getFileFormat();
            }
        }
        return Database.FileFormat.V2003;
    }
    /**
     * 通过版本名获取版本格式 后缀
     *
     * @param versionName
     * @return
     */
    public static String getAccessExtByName(String versionName) {
        for (AccessFormatEnum accessFormatEnum : AccessFormatEnum.values()) {
            if (accessFormatEnum.name().equals(versionName)) {
                return accessFormatEnum.getFileFormat().getFileExtension();
            }
        }
        return Database.FileFormat.V2003.getFileExtension();
    }

    public Database.FileFormat getFileFormat() {
        return fileFormat;
    }
}
