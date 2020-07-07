package net.zacard.xc.manage.web.controller;

import net.zacard.xc.common.api.entity.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-07-04 14:46
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping(path = "/update")
    public Response update() {

        return Response.success();
    }
}
