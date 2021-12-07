# Mock in Bean

[@MockInBean](src/main/java/com/teketik/test/mockinbean/MockInBean.java) and [@SpyInBean](src/main/java/com/teketik/test/mockinbean/SpyInBean.java) are alternatives to @MockBean and @SpyBean for Spring Boot 2 tests *(>= 2.2.0)*.

They surgically replace a field value in a Spring Bean by a Mock/Spy for the duration of a test and set back the original value afterwards, leaving the Spring Context clean.

**@MockInBean 'mocks a bean in a bean'** whereas **@MockBean 'mocks a bean in the whole context'**.

# But why ?

## The problem:

Spring Context pollution was a fairly common problem before the introduction of @MockBean and @SpyBean. Many developers would inject mocks in beans during tests through `@InjectMock` or using manual setters and often forget to set back the original field values which would leave the Spring context polluted and cause test failures in unrelated tests.

@MockBean and @SpyBean solved this issue by providing Mockito injection directly in the Spring Context but introduced an undesirable side-effect: their usage dirties the context and may lead to the re-creation of new Spring contexts for any unique combination, which can be incredibly time-consuming. See [The Problems with @MockBean](https://www.baeldung.com/spring-tests#2-the-problems-withmockbean)

### The solution:

Assuming you really need to run the test in the Spring Context, the most straight-forward solution is still to inject your mock/spy in your bean and reset it afterwards. 

**@MockInBean and @SpyInBean brings the convenience of @MockBean and @SpyBean and do just that:**
  1. Set the mock/spy in the bean.
  2. Replace the mock/spy by the original value afterwards.

# Example:

Assuming that you want to test the following service:
```java
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

```

You can write your test this way:
```java
@SpringBootTest
public class MyServiceTest {

    @MockInBean(MyService.class)
    private ThirdPartyApiService thirdPartyApiService;

    @SpyInBean(MyService.class)
    private ExpensiveProcessor expensiveProcessor;

    @Autowired
    private MyService myService;

    @Test
    public void test() {
        final Object somethingExpensive = new Object();
        Mockito.when(expensiveProcessor.returnSomethingExpensive()).thenReturn(somethingExpensive);
        myService.doSomething();
        Mockito.verify(thirdPartyApiService).doSomethingOnThirdPartyApi(somethingExpensive);
    }

}
```

**What happens**:

Before each test:
  - `MyServiceTest.thirdPartyService` will be created as a mock and injected in the target of @MockInBean: `MyService`.
  - `MyServiceTest.expensiveProcessor` will be created as a spy of the bean `expensiveProcessor` and injected in the target of @SpyInBean: `MyService`.
  
After the tests of `MyServiceTest` are done:
  - `MyService.thirdPartyService` will be reset to the original Spring bean `thirdPartyService`
  - `MyService.expensiveProcessor` will be reset to the original Spring bean `expensiveProcessor`

# Usage:

Simply include the maven dependency (from central maven) to start using @MockInBean and @SpyInBean in your tests.
```
<dependency>
  <groupId>com.teketik</groupId>
  <artifactId>mock-in-bean</artifactId>
  <version>boot2-v1.3</version>
  <scope>test</scope>
</dependency>
```

@MockInBean and @SpyInBean also support:
  - Injection in multiple Spring beans: *Repeat the annotation on your field*.
  - Injection in bean identified by name if multiple instances exist in the context: *Specify a `name` in your annotation*.
  
Checkout the javadoc for more information.

# Limitations:

This approach has some limitations compared to the @MockBean/@SpyBean equivalent:
  - There is currently no isolation per thread. It is not advised to use this library in parallel test suites. (Might be supported at some point, let me know if you need that feature).
  - Operations on beans happening within another bean's constructor will not be performed against the mocks since the mocks are injected directly into the fields. Do not use @MockInBean/@SpyInBean for beans that are manipulated in constructors.
