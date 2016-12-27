package cz.benes.controllers;

import cz.benes.guice.InjectorAware;
import cz.benes.services.WindowService;

public class AbstractController implements InjectorAware {

    protected WindowService windowService = getInstance(WindowService.class);

}
