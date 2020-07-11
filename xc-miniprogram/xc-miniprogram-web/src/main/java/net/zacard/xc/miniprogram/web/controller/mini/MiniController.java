package net.zacard.xc.miniprogram.web.controller.mini;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.entity.MiniProgramDto;
import net.zacard.xc.common.biz.service.MiniprogramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-07-11 12:38
 */
@RestController
@RequestMapping(path = "/mini")
public class MiniController {

    @Autowired
    private MiniprogramService miniprogramService;

    @RequestMapping(path = "/mini/get")
    public Response<MiniProgramDto> get(String appId) {
        return Response.success(miniprogramService.get(appId));
    }
}
