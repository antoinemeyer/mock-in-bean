
package com.teketik.test.mockinbean.data;

import com.teketik.test.mockinbean.MockInBean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration
public class CustomerServiceMockTest {

    @Autowired
    private CustomerService customerService;

    @MockInBean(value = CustomerService.class)
    private CustomerRepository customerRepository;

    private final Customer customer = new Customer("first", "last");

    @Test
    public void test() {
        Customer mockReturn = new Customer();
        Mockito.when(customerRepository.save(customer)).thenReturn(mockReturn);

        Customer serviceReturn = customerService.save(customer);
        Assertions.assertSame(serviceReturn, mockReturn);
    }

    @Test
    public void test2() {
        Customer mockReturn = new Customer();
        Mockito.when(customerRepository.save(customer)).thenReturn(mockReturn);

        Customer serviceReturn = customerService.save(customer);
        Assertions.assertSame(serviceReturn, mockReturn);
    }
}
