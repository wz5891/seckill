package com.example.seckill.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@TableName("t_order")
@Data
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer productId;
    private Integer quantity;
}
