package cz.benes.guice;


import com.google.inject.Guice;
import com.google.inject.Injector;

public interface TestInjectorAware {


    Injector injector = Guice.createInjector(new TestModule());

    default <T> T getInstance(Class<T> tClass) {
        return injector.getInstance(tClass);
    }
}
