package com.teketik.test.mockinbean.test.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Autowired
    protected ThirdPartyApiService thirdPartyService;

    @Autowired
    protected ExpensiveProcessor expensiveProcessor;

    public void doSomething() {
        final Object somethingExpensive = expensiveProcessor.returnSomethingExpensive();
        thirdPartyService.doSomethingOnThirdPartyApi(somethingExpensive);
    }

}
