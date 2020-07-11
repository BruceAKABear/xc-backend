package net.zacard.xc.manage.web.controller;

import com.google.common.io.Files;
import net.zacard.xc.common.biz.util.StaticResourceUtil;
import net.zacard.xc.manage.web.Application;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author guoqw
 * @since 2020-07-11 10:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ResourceControllerTest {

    private MockMvc restMockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() throws Exception {
        this.restMockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .alwaysDo(print())
                .build();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void upload() throws Exception {
        StaticResourceUtil.changePath("/Users/guoqw/netease/docs/技术工作组项目/日志平台/test");
        File file = new File("/Users/guoqw/netease/docs/技术工作组项目/日志平台/参数调优后1.jpg");
        Assert.assertTrue(file.exists());

        MvcResult result = this.restMockMvc.perform(fileUpload("/resource/upload")
                .file("file", Files.toByteArray(file)))
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        System.out.println("response:" + response);
    }
}
