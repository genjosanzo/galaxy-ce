package org.mule.galaxy.script;

import org.mule.galaxy.Dao;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;

public interface ScriptManager extends Dao<Script> {
    String execute(String script) throws AccessException, RegistryException;
}
