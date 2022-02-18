# 工程简介

参考B站诸葛老师[1小时带你用Redis轻松实现秒杀系统](https://www.bilibili.com/video/BV1CE411s7xN?p=1)


# 延伸阅读

## Docker部署 Zookeeper单机
```
docker run --name zookeeper --privileged -p 2181:2181  -d zookeeper:3.4.9
```

进入docker容器
```
docker exec -it zookeeper /bin/bash
```

启动服务端和客户端
```
./zkServer.sh start
./zkCli.sh
```


## Docker部署Mysql
```$xslt
docker run --name MySQL8 -p 3306:3306 -v D:/Docker/mysql/data:/var/lib/mysql -e TZ=Asia/Shanghai -e MYSQL_ROOT_PASSWORD=1 -d mysql:8.0.27 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

## Docker部署Redis
```
docker run --name redis -p 6379:6379 redis
```