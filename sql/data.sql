CREATE TABLE t_product(
                          id INT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
                          name VARCHAR(255)    COMMENT '名称' ,
                          stock INT    COMMENT '库存量' ,
                          PRIMARY KEY (id)
)  COMMENT = '订单表';

CREATE TABLE t_order(
                        id INT NOT NULL AUTO_INCREMENT  COMMENT '订单id' ,
                        product_id INT    COMMENT '产品id' ,
                        quantity INT    COMMENT '产品数量' ,
                        PRIMARY KEY (id)
)  COMMENT = '订单';

INSERT INTO `t_product` (`name`, `stock`) VALUES ('洗衣机', 100);
INSERT INTO `t_product` (`name`, `stock`) VALUES ('电冰箱', 200);