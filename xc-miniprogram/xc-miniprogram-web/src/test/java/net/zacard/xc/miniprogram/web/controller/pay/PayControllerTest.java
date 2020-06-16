package net.zacard.xc.miniprogram.web.controller.pay;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.zacard.xc.common.biz.entity.PrepareOrderReq;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import net.zacard.xc.common.biz.repository.UserAccessLogRepository;
import net.zacard.xc.miniprogram.web.Application;
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
 * @since 2020-06-12 13:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PayControllerTest {

    private MockMvc restMockMvc;

    @Autowired
    private WebApplicationContext wac;

    private XmlMapper xmlMapper;

    @Autowired
    private MiniProgramConfigRepository miniProgramConfigRepository;

    @Autowired
    private UserAccessLogRepository userAccessLogRepository;

    @Before
    public void setUp() throws Exception {
        this.restMockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .alwaysDo(print())
                .build();

        xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void prepareOrder() throws Exception {
        String userToken = "067bef6d80f54166befe3081b4519c68";
        UserAccessLog userAccessLog = userAccessLogRepository.findByUserToken(userToken);
        Session.create("067bef6d80f54166befe3081b4519c68", userAccessLog);

        PrepareOrderReq req = new PrepareOrderReq();
        req.setUserToken("067bef6d80f54166befe3081b4519c68");
        req.setChannelOrderId("202006061728161161902010000");
        req.setPrice(100);
        req.setItemId("001");
        req.setItemName("云宝");

        MvcResult result = restMockMvc.perform(post("/pay/prepare/info")
                .content(JSON.toJSONBytes(req))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String bodyStr = result.getResponse().getContentAsString();
        System.out.println("result:" + JSON.toJSONString(JSON.parseObject(bodyStr), true));
    }
}
