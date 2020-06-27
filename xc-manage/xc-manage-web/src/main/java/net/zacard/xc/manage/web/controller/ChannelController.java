package net.zacard.xc.manage.web.controller;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.biz.entity.Channel;
import net.zacard.xc.common.biz.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-06-21 15:16
 */
@RequestMapping(path = "/channel")
@RestController
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @RequestMapping(path = "/list")
    public Response list() {
        return Response.success(channelService.list());
    }

    @RequestMapping(path = "/add")
    public Response add(@RequestBody @Validated Channel channel) {
        channelService.add(channel);
        return Response.success();
    }

    @RequestMapping(path = "/update")
    public Response update(@RequestBody @Validated Channel channel) {
        channelService.update(channel);
        return Response.success();
    }
}
