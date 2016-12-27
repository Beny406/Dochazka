package cz.benes;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import cz.benes.guice.TestInjectorAware;
import org.h2.tools.RunScript;
import org.junit.After;
import org.junit.Before;

import java.nio.charset.Charset;

public class CommonTest implements TestInjectorAware{

    @Inject
    @Named("db")
    protected String db;

    @Inject
    @Named("login")
    protected String login;

    @Inject
    @Named("password")
    protected String password;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        injector.injectMembers(this);
        RunScript.execute(db, login, password, "classpath:setup.sql", Charset.defaultCharset(), true);
    }

    @After
    public void tearDown() throws Exception {
        RunScript.execute(db, login, password, "classpath:teardown.sql", Charset.defaultCharset(), true);
    }

}
