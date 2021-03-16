package com.teketik.mockinbean.example;

import org.springframework.stereotype.Component;

@Component
public class ExpensiveProcessor {

    public Object returnSomethingExpensive() {
        return new Object();
    }

}
