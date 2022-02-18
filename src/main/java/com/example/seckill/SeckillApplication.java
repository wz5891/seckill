package com.example.seckill;

import com.example.seckill.watcher.ZookeeperWatcher;
import org.apache.zookeeper.ZooKeeper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.example.seckill.mapper")
public class SeckillApplication {

    @Bean
    public ZooKeeper initZookeeper() throws Exception {
        // 创建观察者
        ZookeeperWatcher watcher = new ZookeeperWatcher();
        // 创建 Zookeeper 客户端
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 30000, watcher);
        // 将客户端注册给观察者
        watcher.setZooKeeper(zooKeeper);
        // 将配置好的 zookeeper 返回
        return zooKeeper;
    }

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }

}
