package net.zacard.xc.common.biz.util;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

/**
 * 静态资源工具类
 *
 * @author guoqw
 * @since 2020-07-08 09:01
 */
@Slf4j
public class StaticResourceUtil {

    private static final String DEFAULT_FILE_SYSTEM_DOMAIN = Constant.DOMAIN + "/xc/website/upload";

    private static final String STATIC_PATH = "/static/upload";

    private static final String DEFAULT_PATH = "/home/webapps/xc-website" + STATIC_PATH;

    private static String path = DEFAULT_PATH;

    private static int index = 0;

    private static String lastDS = "";

    static {
        File path = new File(DEFAULT_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public static void changePath(String newPath) {
        path = newPath;
    }

    /**
     * 保存静态资源，返回访问资源的url
     */
    public synchronized static String save(String type, String fileName, byte[] fileBytes) {
        String nowDS = DateTime.now().toString("yyyyMMdd");
        if (lastDS.equals(nowDS)) {
            index++;
        } else {
            index = 0;
        }
        String id = EncryptUtil.aesEncrypt(
                type + "," + fileName + "," + nowDS + "," + index);
        String suffix = fileName.contains(".") ? fileName.substring(fileName.indexOf(".")) : "";
        // 写入到resource中的static/upload/{type}/{id}{suffix}
        String storageFileName = id + suffix;
        String filePath = path + File.separator + type;
        File fp = new File(filePath);
        if (!fp.exists()) {
            fp.mkdirs();
        }
        String fullFileName = filePath + File.separator + storageFileName;
        File file = new File(fullFileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(fileBytes, file);
        } catch (IOException e) {
            log.error("写入文件失败", e);
            throw ExceptionUtil.unchecked(e);
        }
        return DEFAULT_FILE_SYSTEM_DOMAIN + "/" + type + "/" + storageFileName;
    }
}
