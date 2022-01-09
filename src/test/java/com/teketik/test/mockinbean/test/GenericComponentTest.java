package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.ProxyManagerTestUtils;
import com.teketik.test.mockinbean.test.components.GenericMockableComponent;
import com.teketik.test.mockinbean.test.components.GenericTestComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

class GenericComponentTest extends BaseTest {

    private final Log logger = LogFactory.getLog(getClass());

    @MockInBean(GenericTestComponent.class)
    private GenericMockableComponent<String> genericMockableComponent;

    @Resource
    private GenericTestComponent genericTestComponent;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(genericMockableComponent));
        Object field = ReflectionTestUtils.getField(genericTestComponent, "genericMockableComponent");
        logger.debug("genericMockableComponent in test is " + genericMockableComponent);
        logger.debug("field that should be genericMockableComponent proxy in test is " + field);
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(field, genericMockableComponent));
    }

}
