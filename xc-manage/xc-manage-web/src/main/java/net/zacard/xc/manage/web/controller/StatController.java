package net.zacard.xc.manage.web.controller;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.api.entity.StatDto;
import net.zacard.xc.common.biz.entity.stat.MainStat;
import net.zacard.xc.common.biz.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author guoqw
 * @since 2020-07-18 17:47
 */
@RestController
@RequestMapping(path = "/stat")
public class StatController {

    @Autowired
    private StateService stateService;

    @RequestMapping(path = "/list")
    public Response<List<MainStat>> list(@RequestBody @Validated StatDto statDto) {
        return Response.success(stateService.stat(statDto));
    }
}
