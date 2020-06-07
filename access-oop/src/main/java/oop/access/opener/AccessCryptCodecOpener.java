package oop.access.opener;

import com.healthmarketscience.jackcess.CryptCodecProvider;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import net.ucanaccess.jdbc.JackcessOpenerInterface;

import java.io.File;
import java.io.IOException;

/**
 * 加密文件开启
 *
 * @author 欧阳洁
 * @since 2018-05-31 10:21
 */
public class AccessCryptCodecOpener implements JackcessOpenerInterface {
    /**
     * 打开有密码的连接
     * @param fl
     * @param pwd
     * @return
     * @throws IOException
     */
    public Database open(File fl, String pwd) throws IOException {
        DatabaseBuilder dbd = new DatabaseBuilder(fl);
        dbd.setAutoSync(false);
        dbd.setCodecProvider(new CryptCodecProvider(pwd));
        dbd.setReadOnly(false);
        return dbd.open();
    }
}
