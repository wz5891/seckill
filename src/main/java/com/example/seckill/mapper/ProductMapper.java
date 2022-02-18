package com.example.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckill.domain.Product;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {
    @Update("update t_product set stock=stock-1 where id=#{productId}")
    int deductProductStock(Integer productId);
}
