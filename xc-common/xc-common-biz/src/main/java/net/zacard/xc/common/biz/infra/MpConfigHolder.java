package net.zacard.xc.common.biz.infra;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.zacard.xc.common.biz.entity.MiniProgramConfig;
import net.zacard.xc.common.biz.infra.web.SpringContextHandle;
import net.zacard.xc.common.biz.repository.MiniProgramConfigRepository;

import java.util.concurrent.TimeUnit;

/**
 * @author guoqw
 * @since 2020-06-13 13:53
 */
public class MpConfigHolder {

    private static final LoadingCache<String, MiniProgramConfig> HOLDER = CacheBuilder.newBuilder()
            .maximumSize(200)
            // 30分钟没有访问缓存项，将被清除
            .expireAfterAccess(30, TimeUnit.MINUTES)
            // 5分钟刷新数据
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, MiniProgramConfig>() {
                @Override
                public MiniProgramConfig load(String key) {
                    return loadCache(key);
                }
            });

    private static MiniProgramConfigRepository miniProgramConfigRepository = SpringContextHandle.getBean(
            MiniProgramConfigRepository.class);

    private static MiniProgramConfig loadCache(String appId) {
        return miniProgramConfigRepository.findByAppId(appId);
    }

    public static MiniProgramConfig get(String appId) {
        return HOLDER.getUnchecked(appId);
    }

}
