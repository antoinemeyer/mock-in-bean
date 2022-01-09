package com.teketik.test.mockinbean.test.example;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.SpyInBean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyServiceTest {

    //This needs to be an actual mock or spy to do the mockito verifications
    //if this is a proxy, it does not work.
    @MockInBean(MyService.class)
    private ThirdPartyApiService thirdPartyApiService;

    @SpyInBean(MyService.class)
    private ExpensiveProcessor expensiveProcessor;

    @Autowired
    private MyService myService;

    @Test
    public void test() {
        final Object somethingExpensive = new Object();
        Mockito.doReturn(somethingExpensive).when(expensiveProcessor).returnSomethingExpensive();
        myService.doSomething();
        Mockito.verify(thirdPartyApiService).doSomethingOnThirdPartyApi(somethingExpensive);
    }
    
    @AfterEach
    public void after() {
        Mockito.validateMockitoUsage();
    }
    
}
