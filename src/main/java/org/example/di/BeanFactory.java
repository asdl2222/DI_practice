package org.example.di;

import org.example.annotation.Inject;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BeanFactory {
    private static Set<Class<?>> pre;
    private Map<Class<?>, Object> beans = new HashMap<>();
    public BeanFactory(Set<Class<?>> pre) {
        this.pre = pre;
        Initialize();
    }
    @SuppressWarnings("unchecked")
    public void Initialize() {
        for (Class<?> c : pre) {
            Object instance = createInstance(c);
            beans.put(c, instance);
        }
    }

    private Object createInstance(Class<?> c) {
        Constructor<?> constructor = findConstructor(c);
        List<Object> parameters = new ArrayList<>();
        for (Class<?> typeClass : Objects.requireNonNull(constructor).getParameterTypes()) {
            parameters.add(getBeans(typeClass));
        }
        try {
            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> findConstructor(Class<?> c) {
       Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(c);
       if(Objects.nonNull(injectedConstructor)) {
           return null;
       }
       return c.getConstructors()[0];
    }

    public <T> T getBeans(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }
}
