package cz.benes.guice;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public interface InjectorAware {

    Injector injector = Container.getInjector();

    public default <T> T getInstance(Class<T> tClass){
        return injector.getInstance(tClass);
    }

    public default <T> T getInstance(Class<T> tClass, String annotation){
        return injector.getInstance(Key.get(tClass, Names.named(annotation)));
    }

}
