package net.zacard.xc.manage.web.controller;

import com.alibaba.fastjson.JSON;
import net.zacard.xc.common.api.entity.StatDto;
import net.zacard.xc.manage.web.Application;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author guoqw
 * @since 2020-07-18 17:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class StatControllerTest {

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
    public void list() throws Exception {
        StatDto statDto = new StatDto();
        statDto.setStart(DateTime.now().minusDays(2).getMillis());
        statDto.setEnd(DateTime.now().getMillis());

        MvcResult result = restMockMvc.perform(post("/stat/list")
                .content(JSON.toJSONBytes(statDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println("result：" + JSON.toJSONString(JSON.parseObject(json), true));
    }
}
