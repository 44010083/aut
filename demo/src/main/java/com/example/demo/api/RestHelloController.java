package com.example.demo.api;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.ArgsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController

public class RestHelloController {
    @Autowired
    private com.example.demo.service.IHelloService iHelloService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String sayHello() {
        return "hello";
    }

    @RequestMapping(value = "/hello/{name}/{id}", method = RequestMethod.GET)
    public String getInfo(@PathVariable String name, @PathVariable Integer id) {
        return iHelloService.getInfo(name) + ":" + (id + 0);
    }

    @RequestMapping(value = "/hello/body2", method = RequestMethod.GET)
    public ArgsModel helloBody2(@RequestBody ArgsModel vo) {
        return vo;
    }
    @RequestMapping(value = "/hello/body2", method = RequestMethod.POST)
    public ArgsModel helloBody3(@RequestBody ArgsModel vo) {
        return vo;
    }
    @RequestMapping(value = "/hello/body", method = RequestMethod.GET)
    public JSONObject helloBody(@RequestBody JSONObject vo) {
        return vo;
    }

    @RequestMapping(value = "/hello/param", method = RequestMethod.GET)
    public String helloParam(@RequestParam String vo) {
        return vo;
    }
}

