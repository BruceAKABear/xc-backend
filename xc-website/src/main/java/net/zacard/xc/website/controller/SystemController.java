package net.zacard.xc.website.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqw
 * @since 2020-07-07 14:11
 */
@RestController
@RequestMapping(path = "/api/system")
public class SystemController {

    @RequestMapping(path = "/health")
    public String health() {
        return "ok";
    }

    @RequestMapping(path = "/version")
    public String version() {
        return "1.0.0";
    }
}
