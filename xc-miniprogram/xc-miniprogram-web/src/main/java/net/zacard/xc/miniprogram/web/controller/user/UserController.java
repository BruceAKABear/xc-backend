package net.zacard.xc.miniprogram.web.controller.user;

import net.zacard.xc.common.api.entity.Response;
import net.zacard.xc.common.api.entity.UserDto;
import net.zacard.xc.common.biz.entity.OpenIdRes;
import net.zacard.xc.common.biz.entity.User;
import net.zacard.xc.miniprogram.biz.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-06-05 21:01
 */
@RequestMapping(path = "/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册
     */
    @RequestMapping(path = "/register")
    public Response register(User user) {
        userService.register(user);
        return Response.success();
    }

    @RequestMapping(path = "/openid")
    public Response<OpenIdRes> openId(String code, String appId) {
        return Response.success(userService.openid(code, appId));
    }

    /**
     * 登录
     */
    @RequestMapping(path = "/sign/in")
    public Response<String> signIn(@RequestBody @Validated UserDto userDto) {
        return Response.success(userService.signIn(userDto));
    }

    /**
     * 登出
     */
    public Response signOut(String userToken) {
        return Response.success();
    }
}
