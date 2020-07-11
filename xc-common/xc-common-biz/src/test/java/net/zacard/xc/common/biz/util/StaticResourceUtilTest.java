package net.zacard.xc.common.biz.util;

import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author guoqw
 * @since 2020-07-08 11:07
 */
public class StaticResourceUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void save() throws Exception {
        StaticResourceUtil.changePath("/Users/guoqw/netease/docs/技术工作组项目/日志平台/test");
        String url = StaticResourceUtil.save("image", "参数调优后1.jpg",
                Files.toByteArray(new File("/Users/guoqw/netease/docs/技术工作组项目/日志平台/参数调优后1.jpg")));
        System.out.println("url:" + url);

    }

    @Test
    public void est(){
        String s = EncryptUtil.aesDecrypt(
                "98c0fc8e5963120c15b32dafb035203a50fd4b1a6929269e51ac514aba9ae03f5b3682f283c566911d763568f81abfe3");
        System.out.println("s:" + s);
    }
}
