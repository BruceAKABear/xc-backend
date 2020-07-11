package net.zacard.xc.common.biz.util;

import com.google.common.io.Files;

/**
 * @author guoqw
 * @since 2020-07-08 15:30
 */
public class FileUtil {

    public enum FileType {
        IMAGE("image",
                new String[]{"bmp", "jpg", "jpeg", "png", "tiff", "gif", "pcx", "tga", "exif", "fpx", "svg", "psd",
                        "cdr", "pcd", "dxf", "ufo", "eps", "ai", "raw", "wmf"}),
        HTML("html", new String[]{"html", "htm"}),
        UNKNOW("unknow", new String[]{});

        private String typeName;
        private String[] suffixs;

        public String type() {
            return this.typeName;
        }

        FileType(String typeName, String[] suffixs) {
            this.typeName = typeName;
            this.suffixs = suffixs;
        }
    }

    public static String type(String fileName) {
        String fileExtension = Files.getFileExtension(fileName);
        for (FileType fileType : FileType.values()) {
            for (String suffix : fileType.suffixs) {
                if (suffix.equals(fileExtension)) {
                    return fileType.type();
                }
            }
        }
        return FileType.UNKNOW.type();
    }
}
