package net.zacard.xc.website.controller;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.website.entity.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-07-03 11:28
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @RequestMapping("/login")
    public Response login(String username, String password) {
        log.info("用户登录：username({}),password({})", username, password);
        return Response.ok();
    }

    @RequestMapping("/register")
    public Response register(String username, String password) {
        log.info("用户注册：username({}),password({})", username, password);
        return Response.ok();
    }
}
