package net.zacard.xc.manage.web.controller;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.service.MiniprogramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author guoqw
 * @since 2020-06-21 13:37
 */
@RequestMapping(path = "/mini")
@RestController
public class MiniController {

    @Autowired
    private MiniprogramService miniprogramService;

    @RequestMapping(path = "/list")
    public Response list() {
        return Response.success(miniprogramService.list());
    }

    @RequestMapping(path = "/add")
    public Response add(@RequestBody @Validated MiniProgramConfig miniProgramConfig, @RequestParam(required = false) MultipartFile file) {
        miniprogramService.add(miniProgramConfig);
        return Response.success();
    }

    @RequestMapping(path = "/update")
    public Response update(@RequestBody @Validated MiniProgramConfig miniProgramConfig, @RequestParam(required = false) MultipartFile file) {
        miniprogramService.update(miniProgramConfig);
        return Response.success();
    }

}
