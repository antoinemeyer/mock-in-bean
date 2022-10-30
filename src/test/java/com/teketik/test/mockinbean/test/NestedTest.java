package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.example.ExpensiveProcessor;
import com.teketik.test.mockinbean.test.example.MyService;
import com.teketik.test.mockinbean.test.example.ThirdPartyApiService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

class NestedTest extends BaseTest {

    @MockInBean(MyService.class)
    private ThirdPartyApiService thirdPartyApiService;

    @SpyInBean(MyService.class)
    private ExpensiveProcessor expensiveProcessor;

    @Autowired
    private MyService myService;

    @Nested
    class Inner1 {

        @Test
        void resultReturnMockedValue() {
            final Object somethingExpensive = new Object();
            Mockito.when(expensiveProcessor.returnSomethingExpensive()).thenReturn(somethingExpensive);
            myService.doSomething();
            Mockito.verify(thirdPartyApiService).doSomethingOnThirdPartyApi(somethingExpensive);
        }

        @Test
        public void test2() {
            Mockito.when(expensiveProcessor.returnSomethingExpensive()).thenThrow(RuntimeException.class);
            Assertions.assertThrows(RuntimeException.class, () -> myService.doSomething());
        }
    }

    @Nested
    class Inner2 {

        @Test
        void resultReturnMockedValue() {
            final Object somethingExpensive = new Object();
            Mockito.when(expensiveProcessor.returnSomethingExpensive()).thenReturn(somethingExpensive);
            myService.doSomething();
            Mockito.verify(thirdPartyApiService).doSomethingOnThirdPartyApi(somethingExpensive);
        }
    }

    @Test
    void resultReturnMockedValue() {
        final Object somethingExpensive = new Object();
        Mockito.when(expensiveProcessor.returnSomethingExpensive()).thenReturn(somethingExpensive);
        myService.doSomething();
        Mockito.verify(thirdPartyApiService).doSomethingOnThirdPartyApi(somethingExpensive);
    }

}
