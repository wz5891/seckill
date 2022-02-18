package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seckill.Constants;
import com.example.seckill.domain.Product;
import com.example.seckill.mapper.ProductMapper;
import com.example.seckill.service.SeckillService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SeckillController {
    @Resource
    private SeckillService seckillService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ProductMapper productMapper;

    private static ConcurrentHashMap<Integer, Boolean> productSoldOutMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<Product> productList = productMapper.selectList(Wrappers.query());

        for (Product product : productList) {
            stringRedisTemplate.opsForValue().set(Constants.REDIS_PRODUCT_STOCK_PREFIX + product.getId(), String.valueOf(product.getStock()));
        }
    }

    @GetMapping("/kill")
    public String kill(@RequestParam("productId") Integer productId) {
        // 内存级别缓存
        if(productSoldOutMap.get(productId)!=null && productSoldOutMap.get(productId)==true){
            return "库存不足";
        }

        // Redis缓存
        Long stock = stringRedisTemplate.opsForValue().decrement(Constants.REDIS_PRODUCT_STOCK_PREFIX + productId);
        if (stock < 0) {
            productSoldOutMap.put(productId,true);
            stringRedisTemplate.opsForValue().increment(Constants.REDIS_PRODUCT_STOCK_PREFIX + productId);

            return "库存不足";
        }

        try {
            seckillService.kill(productId);
        } catch (Exception e) {
            productSoldOutMap.remove(productId);
            stringRedisTemplate.opsForValue().increment(Constants.REDIS_PRODUCT_STOCK_PREFIX + productId);

            return "创建失败:" + e.getMessage();
        }
        return "秒杀成功";
    }
}
