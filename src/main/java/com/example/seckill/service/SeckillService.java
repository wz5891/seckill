package com.example.seckill.service;

import com.example.seckill.domain.Order;
import com.example.seckill.domain.Product;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.mapper.ProductMapper;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SeckillService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private OrderMapper orderMapper;

    @Transactional
    public void kill(Integer productId){
        // 检查库存
        Product product = productMapper.selectById(productId);
        if(product.getStock()<=0){
            throw new RuntimeException("商品库存已售完");
        }

        // 创建订单
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(1);

        orderMapper.insert(order);


        // 减库存
        productMapper.deductProductStock(productId);
    }
}
