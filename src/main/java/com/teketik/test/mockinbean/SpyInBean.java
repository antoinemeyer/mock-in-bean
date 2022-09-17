package com.teketik.test.mockinbean;

import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation used to inject a {@link Spy} of a Spring Bean in another Spring Bean for the duration of a test.
 * <p>This is a convenient alternative to {@link SpyBean @SpyBean} that provides surgical spy injection without dirtying or polluting the Spring context:<br>
 * Spys are injected for every test method and the original Spring Beans(s) are re-injected after the test is done.
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
 *     public void doSomethingWithThirdPartyApi() {
 *         thirdPartyApi.doSomething(new Object());
 *     }
 * }
 * </pre>
 * You can create a test for your service with a spied {@code ThirdPartyApi} like:
 * <pre class="code">
 * &#064;SpringBootTest
 * public class MyServiceTest {
 *
 *     &#064;SpyInBean(MyService.class)
 *     private ThirdPartyApi thirdPartyApi;
 *
 *     &#064;Autowired
 *     private MyService myService;
 *
 *     &#064;Test
 *     public void test() {
 *         myService.doSomethingWithThirdPartyApi();
 *         Mockito.verify(thirdPartyApi).doSomething(Mockito.any(Object.class));
 *     }
 * }
 * </pre>
 * {@code thirdPartyApi} will be a {@link Spy} of the actual {@code ThirdPartyApi} Spring Bean that is recreated for every test method in {@code MyServiceTest} and {@code MyService}.<br>
 * The original {@code ThirdPartyApi} Spring bean will be re-injected in {@code MyService} after the test.
 * <hr>
 * <p>In case the bean in which you are trying to inject a spy has multiple instances registered in the context, you can specify the {@link #name() name} of the bean:
 * <pre class="code">
 * &#064;SpyInBean(value = MyService.class, name = "nameOfMyService")
 * private ThirdPartyApi thirdPartyApi;
 * </pre>
 * <hr>
 * <p>You can also inject your spy in multiple beans by repeating the annotation:
 * <pre class="code">
 * &#064;SpyInBean(MyFirstService.class),
 * &#064;SpyInBean(MySecondService.class)
 * private ThirdPartyApi thirdPartyApi;
 * </pre>
 * @author Antoine Meyer
 * @see MockInBean
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SpyInBeans.class)
public @interface SpyInBean {

    /**
     * @return the {@code class} of the Spring Bean in which you want your {@link Spy} to be injected for the duration of the test.
     */
    Class<?> value();

    /**
     * @return the {@code name} of the Spring Bean in which you want your {@link Spy} to be injected for the duration of the test.<br>
     * This is not necessary and is only useful if your context contains multiple Beans of the same type with different names.
     */
    String name() default "";

}
