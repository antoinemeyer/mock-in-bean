# Mock in Bean

[@MockInBean](src/main/java/com/teketik/test/mockinbean/MockInBean.java) and [@SpyInBean](src/main/java/com/teketik/test/mockinbean/SpyInBean.java) are alternatives to @MockBean and @SpyBean for Spring Boot 2 tests *(>= 2.2.0)*.

They surgically replace a field value in a Spring Bean by a Mock/Spy for the duration of a test and set back the original value afterwards, leaving the Spring Context clean.

**@MockInBean 'mocks a bean in a bean'** whereas **@MockBean 'mocks a bean in the whole context'**.

# But why ?

## The problem:

Spring Context pollution was a fairly common problem before the introduction of @MockBean and @SpyBean. Many developers would inject mocks in beans during tests through `@InjectMock` or using manual setters and often forget to set back the original field values which would leave the spring context polluted and cause test failures in unrelated tests.

@MockBean and @SpyBean solved this issue by providing Mockito injection directly in the Spring Context but introduced an undesirable side-effect: their usage dirties the context and may lead to the re-creation of new spring contexts for any unique combination, which can be incredibly time-consuming. See [The Problems with @MockBean](https://www.baeldung.com/spring-tests#2-the-problems-withmockbean)

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
  - `MyService.thirdPartyService` will be reset to the original spring bean `thirdPartyService`
  - `MyService.expensiveProcessor` will be reset to the original spring bean `expensiveProcessor`

# Usage:

Simply include the maven dependency (from central maven) to start using @MockInBean and @SpyInBean in your tests.
```
<dependency>
  <groupId>com.teketik</groupId>
  <artifactId>mock-in-bean</artifactId>
  <version>boot2-v1.0</version>
  <scope>test</scope>
</dependency>
```

@MockInBean and @SpyInBean also support:
  - Injection in multiple spring beans: *Repeat the annotation on your field*.
  - Replacement of beans with different names: *Specify a `name` in your annotation*.
  
Checkout the javadoc for more information.

