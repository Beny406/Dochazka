package cz.benes.guice;

import com.google.inject.Injector;

public interface InjectorAware {

    Injector injector = Container.getInjector();

    public default <T> T getInstance(Class<T> tClass){
        return injector.getInstance(tClass);
    }

}
