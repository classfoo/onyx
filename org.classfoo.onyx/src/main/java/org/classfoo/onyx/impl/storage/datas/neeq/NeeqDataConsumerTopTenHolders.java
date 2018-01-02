package org.classfoo.onyx.impl.storage.datas.neeq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;
import org.classfoo.onyx.impl.OnyxUtils;

/**
 * Neeq consumer of top ten holders datas
 * @author ClassFoo
 * @createdate 20180102
 */
public class NeeqDataConsumerTopTenHolders implements OnyxStreamingMessageListener {

    private static final String COLOR = OnyxUtils.getRandomColor();

    private OnyxService onyxService;

    private String kid;

    private OnyxStorageSession session;

    public NeeqDataConsumerTopTenHolders(OnyxService onyxService, String kid) {
        this.onyxService = onyxService;
        this.kid = kid;
    }

    @Override
    public void onStart(OnyxStreamingConsumer consumer) {
        OnyxStorage storage = this.onyxService.getStorageService().getStorage();
        this.session = storage.openSession();
    }

    @Override
    public void onMessage(OnyxStreamingConsumer consumer, OnyxStreamingMessage message) {
        String[] line = (String[]) message.getBody();
        HashMap<String, Object> properties = new HashMap<String, Object>(32);
        String changeQty = line[0];
        properties.put("变更股份", changeQty);
        String date = line[1];
        properties.put("日期", date);
        String code = line[2];
        properties.put("股票代码", code);
        String lastQuantity = line[3];
        properties.put("最近股份", lastQuantity);
        String limitedQuantity = line[4];
        properties.put("限制股份", limitedQuantity);
        String name = OnyxUtils.removeBlank(line[5]);
        properties.put("姓名", name);
        String num = line[6];
        properties.put("排名", num);
        String quantity = line[7];
        properties.put("股份", quantity);
        String ratio = line[8];
        properties.put("占比", ratio);
        String unlimitedQuantity = line[9];
        properties.put("解禁股份", unlimitedQuantity);
        Map<String, Object> company = consumer.getContext().getEntityByProperty("code", code);
        String companyName = MapUtils.getString(company, "name");
        Map<String, Object> entity = consumer.getContext().getEntityByProperty("people", name + ":" + code);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add(companyName);
        labels.add("股东");
        if (entity == null) {
            entity = session.addEntity(this.kid, name, labels, properties);
            consumer.getContext().putEntityByProperty("people", name + ":" + code, entity);
        }
        else {
            String eid = MapUtils.getString(entity, "id");
            session.addEntityLabels(eid, labels);
            session.addEntityProperties(eid, properties);
        }
        String targetid = MapUtils.getString(entity, "id");
        String targetname = MapUtils.getString(entity, "name");

        String sourceid = MapUtils.getString(company, "id");
        String sourcename = MapUtils.getString(company, "name");
        HashMap<String, Object> linkProperties = new HashMap<String, Object>(1);
        linkProperties.put("color", COLOR);
        session.addLink("股东", sourceid, sourcename, targetid, targetname, linkProperties);
    }

    @Override
    public void onShutdown(OnyxStreamingConsumer consumer) {
        this.session.close();
    }

}
