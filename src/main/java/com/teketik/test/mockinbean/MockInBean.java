package com.teketik.test.mockinbean;

import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation used to inject a mockito {@link Mock} in a Spring Bean for the duration of a test.
 * <p>This is a convenient alternative to {@link MockBean @MockBean} that provides surgical mock injection without dirtying or polluting the Spring context:<br>
 * Mocks are injected for every test method and the original Spring Beans(s) are re-injected after the test class is done.
 * <hr>
 * <p><strong>Example:</strong><br>
 * Assuming you have the following service:<br>
 * <pre class="code">
 * &#064;Service
 * public class MyService {
 *
 *     &#064;Autowired
 *     private ThirdPartyApi thirdPartyApi;
 *
 *     public Object returnSomethingFromThirdPartyApi() {
 *         return thirdPartyApi.returnSomething();
 *     }
 * }
 * </pre>
 * You can create a test for your service with a mocked {@code ThirdPartyApi} like:
 * <pre class="code">
 * &#064;SpringBootTest
 * public class MyServiceTest {
 *
 *     &#064;MockInBean(MyService.class)
 *     private ThirdPartyApi thirdPartyApi;
 *
 *     &#064;Autowired
 *     private MyService myService;
 *
 *     &#064;Test
 *     public void test() {
 *         final Object expected = new Object();
 *         Mockito.when(thirdPartyApi.returnSomething()).thenReturn(expected);
 *         final Object actual = myService.returnSomethingFromThirdPartyApi();
 *         Assert.assertEquals(expected, actual);
 *     }
 * }
 * </pre>
 * {@code thirdPartyApi} will be a {@link Mock} that is recreated for every test method in {@code MyServiceTest} and {@code MyService}.<br>
 * The original {@code ThirdPartyApi} Spring bean will be re-injected in {@code MyService} after all the tests of {@code MyServiceTest}.
 * <hr>
 * <p>In case the bean in which you are trying to inject a mock has multiple instances registered in the context, you can specify the {@link #name() name} of the bean:
 * <pre class="code">
 * &#064;MockInBean(value = MyService.class, name = "nameOfMyService")
 * private ThirdPartyApi thirdPartyApi;
 * </pre>
 * <hr>
 * <p>You can also inject your mock in multiple beans by repeating the annotation:
 * <pre class="code">
 * &#064;MockInBean(MyFirstService.class),
 * &#064;MockInBean(MySecondService.class)
 * private ThirdPartyApi thirdPartyApi;
 * </pre>
 * @author Antoine Meyer
 * @see SpyInBean
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MockInBeans.class)
public @interface MockInBean {

    /**
     * @return the {@code class} of the Spring Bean in which you want your {@link Mock} to be injected for the duration of the test.
     */
    Class<?> value();

    /**
     * @return the {@code name} of the Spring Bean in which you want your {@link Mock} to be injected for the duration of the test.<br>
     * This is not necessary and is only useful if your context contains multiple Beans of the same type with different names.
     */
    String name() default "";

}
