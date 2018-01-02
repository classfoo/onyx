package org.classfoo.onyx.impl.storage.datas.neeq;

import java.util.Arrays;
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
 * Neeq consumer of trade info datas
 * @author ClassFoo
 * @createdate 20180102
 */
public class NeeqDataConsumerTradeInfo implements OnyxStreamingMessageListener {

    private OnyxService onyxService;

    private String kid;

    private OnyxStorageSession session;

    public NeeqDataConsumerTradeInfo(OnyxService onyxService, String kid) {
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
        //买方主办券商证券营业部	买方证券账户名称	交易日期	卖方主办券商证券营业部	卖方证券账户名称	成交价格（元）	成交数量（股）	证券代码	证券简称
        String buybroker = OnyxUtils.removeBlank(line[0]);
        properties.put("买方营业部", buybroker);
        String buyaccount = OnyxUtils.removeBlank(line[1]);
        properties.put("买方账号", buyaccount);
        String date = line[2];
        properties.put("交易日期", date);
        String sellbroker = OnyxUtils.removeBlank(line[3]);
        properties.put("卖方营业部", sellbroker);
        String sellaccount = OnyxUtils.removeBlank(line[4]);
        properties.put("卖方账号", sellaccount);
        String price = line[5];
        properties.put("价格", price);
        String amount = line[6];
        properties.put("数量", amount);
        String code = line[7];
        properties.put("代码", code);
        String companyName = line[8];
        properties.put("公司", companyName);
        Map<String, Object> company = consumer.getContext().getEntityByProperty("code", code);
        String companyId = MapUtils.getString(company, "id");
        this.session.addEvent(companyId, "交易", buyaccount + "从" + sellaccount + "买入：" + price + "元/股，" + amount + "股",
                date, properties);
        Map<String, Object> buyer = consumer.getContext().getEntityByProperty("people", buyaccount + ':' + code);
        if (buyer == null) {
            HashMap<String, Object> buyerProperties = new HashMap<String, Object>(32);
            buyerProperties.put("营业部", buybroker);
            buyer = this.session.addEntity(this.kid, buyaccount, Arrays.asList("买家"), buyerProperties);
            consumer.getContext().putEntityByProperty("people", buyaccount + ':' + code, buyer);
        }
        else {
            String eid = MapUtils.getString(buyer, "id");
            this.session.addEntityLabels(eid, Arrays.asList("买家"));
            HashMap<String, Object> buyerProperties = new HashMap<String, Object>(32);
            buyerProperties.put("营业部", buybroker);
            this.session.addEntityProperties(eid, buyerProperties);
        }
        String buyerId = MapUtils.getString(buyer, "id");
        this.session.addLink("股东", companyId, companyName, buyerId, buyaccount, null);
        this.session.addEvent(buyerId, "交易",
                "从" + sellaccount + "买入股票'" + companyName + "'：" + price + "元/股，" + amount + "股", date, properties);
        Map<String, Object> seller = consumer.getContext().getEntityByProperty("people", sellaccount + ':' + code);
        if (seller == null) {
            HashMap<String, Object> sellerProperties = new HashMap<String, Object>(32);
            sellerProperties.put("营业部", sellbroker);
            seller = this.session.addEntity(this.kid, sellaccount, Arrays.asList("卖家"), sellerProperties);
            consumer.getContext().putEntityByProperty("people", sellaccount + ':' + code, seller);
        }
        else {
            String eid = MapUtils.getString(seller, "id");
            session.addEntityLabels(eid, Arrays.asList("卖家"));
            HashMap<String, Object> sellerProperties = new HashMap<String, Object>(32);
            sellerProperties.put("营业部", sellbroker);
            session.addEntityProperties(eid, sellerProperties);
        }
        String sellerId = MapUtils.getString(seller, "id");
        this.session.addLink("股东", companyId, companyName, sellerId, sellaccount, null);
        this.session.addEvent(sellerId, "交易",
                "卖给" + buyaccount + "股票：'" + companyName + "':" + price + "元/股，" + amount + "股", date, properties);
        // 添加买方和卖方
        this.session.addLink("交易", buyerId, buyaccount, sellerId, sellaccount, null);
    }

    @Override
    public void onShutdown(OnyxStreamingConsumer consumer) {
        this.session.close();
    }
}
