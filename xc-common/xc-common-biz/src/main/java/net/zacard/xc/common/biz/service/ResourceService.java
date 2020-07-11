package net.zacard.xc.common.biz.service;

import net.zacard.xc.common.biz.util.ExceptionUtil;
import net.zacard.xc.common.biz.util.FileUtil;
import net.zacard.xc.common.biz.util.StaticResourceUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author guoqw
 * @since 2020-07-08 15:11
 */
@Service
public class ResourceService {

    public String upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            return StaticResourceUtil.save(FileUtil.type(fileName), fileName, file.getBytes());
        } catch (IOException e) {
            throw ExceptionUtil.unchecked(e);
        }
    }
}
