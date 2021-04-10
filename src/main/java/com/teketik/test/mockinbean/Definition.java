package com.teketik.test.mockinbean;

import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.ResolvableType;

import java.util.Objects;

/**
 * <p>Definition of a {@link Mock mock} or a {@link Spy spy}.
 * <p>Corresponding entity can be created using {@link #create(Object)}.
 * @author Antoine Meyer
 */
abstract class Definition {

    protected final String name;
    protected final ResolvableType resolvableType;

    Definition(String name, ResolvableType resolvableType) {
        super();
        this.name = name;
        this.resolvableType = resolvableType;
    }

    String getName() {
        return name;
    }

    ResolvableType getResolvableType() {
        return resolvableType;
    }

    /**
     * Creates a mock or a spy of the provided original value.
     * @param <T>
     * @param originalValue
     * @return
     */
    abstract <T> T create(Object originalValue);

    @Override
    public int hashCode() {
        return Objects.hash(name, resolvableType);
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
        Definition other = (Definition) obj;
        return Objects.equals(name, other.name) && Objects.equals(resolvableType, other.resolvableType);
    }

    @Override
    public String toString() {
        return "Definition [name=" + name + ", resolvableType=" + resolvableType + "]";
    }

}
