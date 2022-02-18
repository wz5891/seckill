package com.example.seckill;

import com.example.seckill.domain.Product;
import com.example.seckill.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ProductServiceTest {
    @Resource
    private ProductMapper productMapper;

    @Test
    public void test(){
        Product product = new Product();
        product.setName("电冰箱");
        product.setStock(200);
        productMapper.insert(product);
    }
}
