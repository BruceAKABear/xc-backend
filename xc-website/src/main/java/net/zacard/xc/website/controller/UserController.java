package net.zacard.xc.website.controller;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.website.entity.Response;
import net.zacard.xc.website.entity.User;
import net.zacard.xc.website.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/login")
    public Response login(String username, String password) {
        log.info("用户登录：username({}),password({})", username, password);
        return Response.ok();
    }

    @RequestMapping("/register")
    public Response register(String username, String password) {
        log.info("用户注册：username({}),password({})", username, password);
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            Response.fail("用户名：'" + username + "'已经存在");
        }
        return Response.ok();
    }
}
