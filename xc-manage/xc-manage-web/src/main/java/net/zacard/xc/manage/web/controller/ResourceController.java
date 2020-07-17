package net.zacard.xc.manage.web.controller;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author guoqw
 * @since 2020-07-08 15:08
 */
@RestController
@RequestMapping(path = "/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 返回资源访问url
     */
    @RequestMapping(path = "/upload")
    public Response<String> upload(@RequestParam(name = "upload") MultipartFile file) {
        return Response.success(resourceService.upload(file));
    }
}

