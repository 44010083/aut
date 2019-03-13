package com.example.demo.service;

import org.springframework.transaction.annotation.Transactional;

public interface IHelloService {
    @Transactional(readOnly = true)
    String getInfo(String name);
}
