package net.zacard.xc.manage.web.controller;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.entity.Info;
import net.zacard.xc.common.biz.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-07-11 12:14
 */
@RestController
@RequestMapping(path = "/info")
public class InfoController {

    @Autowired
    private InfoService infoService;

    @RequestMapping(path = "/list")
    public Response<List<Info>> list() {
        return Response.success(infoService.list());
    }

    @RequestMapping(path = "/add")
    public Response add(@Validated @RequestBody Info info) {
        infoService.add(info);
        return Response.success();
    }

    @RequestMapping(path = "/update")
    public Response update(@Validated @RequestBody Info info) {
        infoService.update(info);
        return Response.success();
    }
}
