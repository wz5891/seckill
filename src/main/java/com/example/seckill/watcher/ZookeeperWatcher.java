package com.example.seckill.watcher;

import com.example.seckill.Constants;
import com.example.seckill.controller.SeckillController;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

@Slf4j
@Setter
public class ZookeeperWatcher implements Watcher {
    private ZooKeeper zooKeeper;
    @Override
    public void process(WatchedEvent watchedEvent) {
        log.info("收到Zookeeper通知 notification. {}",watchedEvent);

        if (watchedEvent.getType() == Event.EventType.None && watchedEvent.getPath() == null) {
            log.info("connect successfully.");

            try {
                // 创建 zookeeper 商品售罄信息根节点
                String path = "/" + Constants.ZK_PRODUCT_SOLD_OUT_FLAG;
                if (zooKeeper != null && zooKeeper.exists(path, false) == null) {
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

        } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            try {
                // 获取节点路径
                String path = watchedEvent.getPath();
                // 获取节点数据
                String soldOut = new String(zooKeeper.getData(path, true, new Stat()));
                // 处理当前服务器对应 JVM 缓存
                if ("false".equals(soldOut)) {
                    // 获取商品 Id
                    String productId = path.substring(path.lastIndexOf("/") + 1, path.length());
                    log.info("根据Zookeeper通知，设置[{}]售完标记为否",productId);
                    // 同步当前 JVM 缓存
                    if (SeckillController.getProductSoldOutMap().contains(productId)) {
                        SeckillController.getProductSoldOutMap().remove(productId);
                    }
                }

            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
