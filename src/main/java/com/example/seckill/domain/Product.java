package com.example.seckill.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@TableName("t_product")
@Data
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer stock;
}
