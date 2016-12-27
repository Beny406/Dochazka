package cz.benes.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Container {

    private static Injector injector;

    public static Injector getInjector(){
        if (injector == null) {
            injector = Guice.createInjector(new Module());
        }
        return injector;
    }

    public static <T> T getInstance(Class<T> clazz){
        return injector.getInstance(clazz);
    }

}
