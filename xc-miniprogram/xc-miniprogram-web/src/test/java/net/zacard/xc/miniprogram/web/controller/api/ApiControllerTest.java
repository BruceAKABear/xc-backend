package net.zacard.xc.miniprogram.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.zacard.xc.common.api.entity.RoleInfoDto;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.PayCallbackReq;
import net.zacard.xc.common.biz.entity.PayCallbackRes;
import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.entity.WxMessageReq;
import net.zacard.xc.common.biz.infra.web.Session;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;
import net.zacard.xc.common.biz.repository.UserAccessLogRepository;
import net.zacard.xc.common.biz.util.EncryptUtil;
import net.zacard.xc.common.biz.util.ObjectUtil;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import net.zacard.xc.common.biz.util.XmlUtil;
import net.zacard.xc.miniprogram.web.Application;
import org.junit.After;
import org.junit.Assert;
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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author guoqw
 * @since 2020-06-11 11:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApiControllerTest {

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
    public void payCallback() throws Exception {
        PayCallbackReq req = new PayCallbackReq();
        req.setReturnCode("SUCCESS");
        req.setResultCode("SUCCESS");
        req.setAppId("wx0e63bb140eabbcab");
        req.setMchId("1597282921");
        req.setNonceStr(RandomStringUtil.getRandomUpperString());
        req.setResultCode("SUCCESS");
        req.setOpenid("oFtQw5YlC2hYGE2W_DrtridM9jZk");
        req.setSubscribe("Y");
        req.setTradeType("JSAPI");
        req.setBankType("CMC");
        req.setTotalFee(100);
        req.setCashFee(0);
        req.setTransactionId("abc");
        req.setOutTradeNo("202006121346458161902010011");
        req.setTimeEnd("20200611131756");

        // 生成签名
        Map<String, String> signMap = ObjectUtil.objectToMapNonNull(req);
        MiniProgramConfig config = miniProgramConfigRepository.findByAppId(req.getAppId());
        req.setSign(EncryptUtil.wxPaySign(signMap, config.getKey()));
        System.out.println("sign:" + req.getSign());

        // req转成xml
        String xmlStr = xmlMapper.writeValueAsString(req);

        MvcResult result = restMockMvc.perform(post("/api/pay/wx/callback")
                .content(xmlStr)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("1"))
                .andReturn();
        String bodyStr = result.getResponse().getContentAsString();

        PayCallbackRes res = xmlMapper.readValue(bodyStr, PayCallbackRes.class);
        System.out.println("response:" + JSON.toJSONString(res, true));

        // 这里测试是否有收到4次回调
        TimeUnit.SECONDS.sleep(80);
    }

    @Test
    public void payQuery() {
    }

    @Test
    public void roleInfoAdd() {
        RoleInfoDto roleInfoDto = new RoleInfoDto();
        roleInfoDto.setType("CREATE");
        roleInfoDto.setName("张三");
        roleInfoDto.setArea("一区");
        roleInfoDto.setLevel("1");
        roleInfoDto.setMoney(0);
        roleInfoDto.setUserToken("");

    }

    @Test
    public void roleInfoUpdate() {
        RoleInfoDto roleInfoDto = new RoleInfoDto();

    }

    @Test
    public void userInfo() throws Exception {
        String userToken = "067bef6d80f54166befe3081b4519c68";
        MvcResult result = restMockMvc.perform(get("/api/user/info?userToken=" + userToken)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String bodyStr = result.getResponse().getContentAsString();
        System.out.println("result:" + JSON.toJSONString(JSON.parseObject(bodyStr), true));
    }

    @Test
    public void wxMessage() throws Exception {
        WxMessageReq req = new WxMessageReq();
        req.setFromUserName("oFtQw5YlC2hYGE2W_DrtridM9jZk");
        req.setToUserName("wx0e63bb140eabbcab");
        req.setCreateTime(System.currentTimeMillis() / 1000);
        req.setMsgType("text");
        req.setContent("测试消息");
        String xmlStr = XmlUtil.toXml(req);

        MvcResult result = restMockMvc.perform(post("/api/wx/message/wx0e63bb140eabbcab")
                .content(xmlStr)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        String bodyStr = result.getResponse().getContentAsString();
        Assert.assertEquals("success", bodyStr);
    }

    @Test
    public void roleInfo() throws Exception {
        String json = " [\n" +
                "    'area' => '447',\n" +
                "    'level' => '1',\n" +
                "    'money' => 1,\n" +
                "    'name' => '弑神丿滨海',\n" +
                "    'type' => 'UPDATE-LEVEL',\n" +
                "    'userToken' => 'ae9d5df325fb4a97bfcc7493a6839dbe',\n" +
                "    'sign' => '24D11FD04824C5FD4E0448892AAE0BC8',\n" +
                "]";
        String userToken = "ae9d5df325fb4a97bfcc7493a6839dbe";
        RoleInfoDto roleInfoDto = new RoleInfoDto();
        roleInfoDto.setUserToken(userToken);
        roleInfoDto.setMoney(1);
        roleInfoDto.setLevel("1");
        roleInfoDto.setArea("447");
        roleInfoDto.setType("UPDATE-LEVEL");
        roleInfoDto.setName("弑神丿滨海");
        roleInfoDto.setSign("24D11FD04824C5FD4E0448892AAE0BC8");

        UserAccessLog userAccessLog = userAccessLogRepository.findByUserToken(userToken);
        Session.create(userToken,userAccessLog);

        MvcResult result = restMockMvc.perform(post("/api/game/role/info")
                .content(JSON.toJSONBytes(roleInfoDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String bodyStr = result.getResponse().getContentAsString();
        System.out.println("result:" + JSON.toJSONString(JSON.parseObject(bodyStr), true));
    }

    @Test
    public void wxMessageCheck() {
    }
}
