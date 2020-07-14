package net.zacard.xc.website.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.website.entity.Blog;
import net.zacard.xc.website.entity.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-07-13 19:49
 */
@RestController
@RequestMapping(path = "/blog")
@Slf4j
public class BlogController {

    @RequestMapping(path = "/add")
    public Response add(@RequestBody Blog blog) {
        log.info("收到信息发布：" + JSON.toJSONString(blog, true));
        return Response.ok();
    }
}
