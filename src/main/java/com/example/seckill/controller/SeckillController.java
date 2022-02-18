package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seckill.Constants;
import com.example.seckill.domain.Product;
import com.example.seckill.mapper.ProductMapper;
import com.example.seckill.service.SeckillService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class SeckillController {
    @Resource
    private SeckillService seckillService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ProductMapper productMapper;

    @Getter
    private static ConcurrentHashMap<Integer, Boolean> productSoldOutMap = new ConcurrentHashMap<>();

    @Resource
    private ZooKeeper zooKeeper;

    @PostConstruct
    public void init() throws KeeperException, InterruptedException {
        List<Product> productList = productMapper.selectList(Wrappers.query());

        for (Product product : productList) {
            stringRedisTemplate.opsForValue().set(Constants.REDIS_PRODUCT_STOCK_PREFIX + product.getId(), String.valueOf(product.getStock()));

            if(product.getStock()>0){
                // 通过 zookeeper 回滚其他服务器的 JVM 缓存中的商品售完标记
                String path = "/" + Constants.ZK_PRODUCT_SOLD_OUT_FLAG + "/" + product.getId();
                if (zooKeeper.exists(path, true) != null) {
                    zooKeeper.setData(path, "false".getBytes(), -1);
                }
            }
        }
    }

    @GetMapping("/kill")
    public String kill(@RequestParam("productId") Integer productId) throws KeeperException, InterruptedException {
        log.info("秒杀产品：{}",productId);
        // 内存级别缓存
        if (productSoldOutMap.get(productId) != null && productSoldOutMap.get(productId) == true) {
            log.info("内存判断：库存不足");
            return "库存不足";
        }

        // Redis缓存
        Long stock = stringRedisTemplate.opsForValue().decrement(Constants.REDIS_PRODUCT_STOCK_PREFIX + productId);
        if (stock < 0) {
            log.info("Redis判断：库存不足");
            productSoldOutMap.put(productId, true);

            stringRedisTemplate.opsForValue().increment(Constants.REDIS_PRODUCT_STOCK_PREFIX + productId);

            // zookeeper 中设置售完标记， zookeeper 节点数据格式 product/1 true
            String productPath = "/" + Constants.ZK_PRODUCT_SOLD_OUT_FLAG + "/" + productId;
            if (zooKeeper.exists(productPath, true) == null) {
                log.info("设置 zookeeper 售完");
                zooKeeper.create(productPath, "true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }else {
                log.info("更新 zookeeper 售完");
                zooKeeper.setData(productPath,"true".getBytes(),-1);
            }

            // 监听 zookeeper 售完节点
            zooKeeper.exists(productPath, true);

            return "库存不足";
        }

        try {
            seckillService.kill(productId);
        } catch (Exception e) {
            log.info("订单创建出错");
            productSoldOutMap.remove(productId);
            stringRedisTemplate.opsForValue().increment(Constants.REDIS_PRODUCT_STOCK_PREFIX + productId);


            // 通过 zookeeper 回滚其他服务器的 JVM 缓存中的商品售完标记
            String path = "/" + Constants.ZK_PRODUCT_SOLD_OUT_FLAG + "/" + productId;
            if (zooKeeper.exists(path, true) != null) {
                log.info("设置 zookeeper 售完为否");
                zooKeeper.setData(path, "false".getBytes(), -1);
            }

            return "创建失败:" + e.getMessage();
        }
        return "秒杀成功";
    }
}
