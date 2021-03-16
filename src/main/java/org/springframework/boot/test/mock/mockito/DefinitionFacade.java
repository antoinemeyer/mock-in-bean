package org.springframework.boot.test.mock.mockito;

import java.util.Objects;
import java.util.function.Function;

/**
 * <p>{@link Definition} facade exposing common methods for {@link MockDefinition} and {@link SpyDefinition}.
 * @author Antoine Meyer
 */
class DefinitionFacade {

    private final Definition definition;
    private final Class<?> type;
    private final Function<Object, Object> makeMockOrSpyFunction;

    public DefinitionFacade(Definition definition) {
        this.definition = definition;
        if (definition instanceof MockDefinition) {
            type = (Class<?>) ((MockDefinition) definition).getTypeToMock().getType();
            makeMockOrSpyFunction = o -> ((MockDefinition) definition).createMock();
        } else if (definition instanceof SpyDefinition) {
            type = (Class<?>) ((SpyDefinition) definition).getTypeToSpy().getType();
            makeMockOrSpyFunction = o -> ((SpyDefinition) definition).createSpy(o);
        } else {
            throw new IllegalStateException("Invalid definition " + definition);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DefinitionFacade other = (DefinitionFacade) obj;
        return Objects.equals(definition, other.definition);
    }

    @Override
    public String toString() {
        return "DefinitionFacade [definition=" + definition + "]";
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return definition.getName();
    }

    public Object makeMockOrSpy(Object originalValue) {
        return this.makeMockOrSpyFunction.apply(originalValue);
    }

}
