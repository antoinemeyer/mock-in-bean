package com.teketik.test.mockinbean;

/**
 * Identifies a named object in the spring context.
 * @author Antoine Meyer
 */
public class NamedObject {
    
    final String name;
    final Object object;
    
    public NamedObject(String name, Object object) {
        super();
        this.name = name;
        this.object = object;
    }

    @Override
    public String toString() {
        return "NamedObject [name=" + name + ", object=" + object + "]";
    }
    
}
