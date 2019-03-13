package com.example.demo.service.impl;

import com.example.demo.service.IHelloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IHelloServiceImpl implements IHelloService {
    @Override
    @Transactional(readOnly = true)
    public String getInfo(String name) {
        return name;
    }

}
