package com.example.seckill.controller;

import com.example.seckill.service.SeckillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SeckillController {
    @Resource
    private SeckillService seckillService;

    @GetMapping("/kill")
    public String kill(@RequestParam("productId") Integer productId){
        seckillService.kill(productId);

        return "秒杀成功";
    }
}
