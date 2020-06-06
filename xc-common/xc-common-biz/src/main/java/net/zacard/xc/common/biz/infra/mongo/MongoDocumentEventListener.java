package net.zacard.xc.common.biz.infra.mongo;

import com.mongodb.DBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import java.util.Date;

/**
 * @author guoqw
 * @since 2020-05-05 15:26
 */
public class MongoDocumentEventListener extends AbstractMongoEventListener<BaseDocument> {

    @Override
    public void onBeforeSave(BeforeSaveEvent<BaseDocument> event) {
        super.onBeforeSave(event);

        BaseDocument baseDocument = event.getSource();
        // TODO 测试2.x的使用方式，是否还有必要设置document.put？
        DBObject dbObject = event.getDBObject();
        Date now = new Date();
        if (baseDocument.getCreateTime() == null) {
            baseDocument.setCreateTime(now);
            // spring-data-mongo默认不保存父类属性,暂时使用这个方式设置
            dbObject.put("create_time", now);
        }
        // update_time总是会更新
        baseDocument.setUpdateTime(now);
        // spring-data-mongo默认不保存父类属性,暂时使用这个方式设置
        dbObject.put("update_time", now);

        // 设置是否删除，默认为false
        if (baseDocument.getDeleted() == null) {
            baseDocument.setDeleted(Boolean.FALSE);
            dbObject.put("deleted", Boolean.FALSE);
        }
    }
}
