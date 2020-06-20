package net.zacard.xc.miniprogram.web.controller;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.service.MiniprogramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-06-01 16:05
 */
@RestController
public class MainController {

    @Autowired
    private MiniprogramService miniprogramService;

    @RequestMapping(path = "/channels")
    public Response<List<Channel>> channels(String appId) {
        return Response.success(miniprogramService.channels(appId));
    }

    @RequestMapping(path = "/health")
    public String health() {
        return "ok"; }

}
