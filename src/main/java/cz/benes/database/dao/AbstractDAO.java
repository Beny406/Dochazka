package cz.benes.database.dao;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import cz.benes.guice.InjectorAware;
import org.sql2o.Sql2o;

public abstract class AbstractDAO implements InjectorAware {

//    protected final String dbIP = Preferences.systemRoot().get("DOCHAZKA_DB_ADDRESS", "127.0.0.1");

    @Inject
    @Named("dbServer")
    protected String dbServer;

    @Inject
    @Named("db")
    protected String db;

    @Inject
    @Named("dbName")
    protected String dbName;

    @Inject
    @Named("login")
    protected String login;

    @Inject
    @Named("password")
    protected String password;

    protected Sql2o sql2o;
    protected Sql2o sql2oServer;

    @Inject
    protected void init(){
        sql2o = new Sql2o(db, login, password);
        sql2oServer = new Sql2o(dbServer, login, password);
    }

}
