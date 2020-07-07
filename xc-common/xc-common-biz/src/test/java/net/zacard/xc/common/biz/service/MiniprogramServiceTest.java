package net.zacard.xc.common.biz.service;

import com.google.common.io.Files;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.entity.MiniProgramExtraConfig;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.HttpUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * @author guoqw
 * @since 2020-07-04 13:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MiniprogramServiceTest {

    @Autowired
    private MiniprogramService miniprogramService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getByAppId() {
    }

    @Test
    public void channels() {
    }

    @Test
    public void list() {
    }

    @Test
    public void add() {
    }

    @Test
    public void refreshAccessToken() {
    }

    @Test
    public void update() {
        String mediaId = "Hr7HR7lxwsX2diEQ3ISg1HnhlCqBOAAVQTjphy2DDr2KpnQ6pHVf85TpWNXPXFPN";
        MiniProgramConfig mini = miniprogramService.getByAppId("wx0e63bb140eabbcab");
        MiniProgramExtraConfig extraConfig = new MiniProgramExtraConfig();
        extraConfig.setResPayPagePath("pages/pay/pay");
        extraConfig.setPayThumbMediaId(mediaId);
        extraConfig.setPayTitle("点我充值");
        mini.setExtraConfig(extraConfig);
        miniprogramService.update(mini);
    }

    @Test
    public void uploadMedia() throws Exception {
        MiniProgramConfig config = miniprogramService.refreshAccessToken("wx0e63bb140eabbcab");
        String url = String.format(Constant.MINI_PROGRAM_UPLOAD_MEDIA_URL_FORMAT, config.getAccessToken(), "image");
        File file = new File("/Users/guoqw/myprojects/xc/doc/xz-mini/hb.jpeg");
        String json = HttpUtil.uploadFile(url, "hb.jpeg", "media", Files.toByteArray(file));
        System.out.println("json:" + json);
    }
}
