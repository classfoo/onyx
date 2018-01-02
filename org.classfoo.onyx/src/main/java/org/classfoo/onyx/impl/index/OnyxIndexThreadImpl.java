package org.classfoo.onyx.impl.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexService;
import org.classfoo.onyx.api.index.OnyxIndexSession;
import org.classfoo.onyx.api.index.OnyxIndexThread;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingContext;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;
import org.classfoo.onyx.impl.OnyxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see OnyxStreamingConsumer
 * @author ClassFoo
 *
 */
public class OnyxIndexThreadImpl implements OnyxIndexThread {

    private static final Logger logger = LoggerFactory.getLogger(OnyxIndexThreadImpl.class);

    private OnyxService onyxService;

    private Map<String, OnyxStreamingMessageListener> listeners;

    private Queue<Map<String, Object>> queue = new LinkedBlockingQueue<Map<String, Object>>();

    private AtomicBoolean closed = new AtomicBoolean(true);

    private long startTime;

    private long messageCount;

    private OnyxIndexService indexService;

    public OnyxIndexThreadImpl(OnyxService onyxService) {
        this.onyxService = onyxService;
        this.indexService = this.onyxService.getIndexService();
    }

    @Override
    public void start() {
        this.closed.set(false);
        OnyxUtils.run(this);
        this.startTime = System.currentTimeMillis();
        this.messageCount = 0;
    }

    @Override
    public synchronized void addIndex(int operate, String index, String type, String id, Map<String, Object> object) {
        if (this.closed.get()) {
            this.start();
        }
        HashMap<String, Object> item = new HashMap<String, Object>(5);
        item.put("operate", operate);
        item.put("index", index);
        item.put("type", type);
        item.put("id", id);
        item.put("object", object);
        this.queue.add(item);
        notifyAll();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (!closed.get()) {
            try {
                Map<String, Object> index = removeFromQueue();
                if (index == null) {
                    continue;
                }
                this.onIndex(index);
                messageCount++;
                if (messageCount % 1000 == 0) {
                    long current = System.currentTimeMillis();
                    long period = current - startTime;
                    logger.info("已经索引{}个对象，耗时：{}秒，速度：{}行/秒...", messageCount, ((double) period) / 1000,
                            1000 * 1000 / (period == 0 ? 1 : period));
                    startTime = current;
                }
            }
            catch (Exception e) {
                logger.error("记录DIFlowSession会话信息失败!", e);
            }
        }
    }

    private void onIndex(Map<String, Object> item) {
        int operate = MapUtils.getIntValue(item, "operate");
        String index = MapUtils.getString(item, "index");
        String type = MapUtils.getString(item, "type");
        String id = MapUtils.getString(item, "id");
        Map<String, Object> object = (Map<String, Object>) MapUtils.getObject(item, "object");
        OnyxIndexSession session = this.indexService.openSession();
        try {
            switch (operate) {
                case INSERT: {
                    session.addIndex(index, type, id, object);
                    break;
                }
                case MERGE: {
                    session.mergeIndex(index, type, id, object);
                    break;
                }
                case UPDATE: {
                    session.updateIndex(index, type, id, object);
                    break;
                }
                case UPSERT: {
                    session.upsertIndex(index, type, id, object);
                    break;
                }
                default:
            }

        }
        finally {
            session.close();
        }
    }

    private synchronized Map<String, Object> removeFromQueue() throws InterruptedException {
        if (this.closed.get()) {
            return null;
        }
        int count = 0;
        while (queue.size() == 0) {
            if (this.closed.get()) {
                return null;
            }
            if (count++ == 10) {
                this.stop();
                return null;
            }
            this.wait(1000);
        }
        return queue.poll();
    }

    /**
     * 关闭线程
     */
    private synchronized void stop() {
        this.closed.set(true);
        this.notify();
    }

    @Override
    public void shutdown() {
        this.stop();
    }
}
