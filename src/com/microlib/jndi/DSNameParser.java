package com.microlib.jndi;

import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.CompoundName;
import javax.naming.NamingException;
import java.util.Properties;

class DSNameParser implements NameParser {

    static Properties syntax = new Properties();
    public DSNameParser() {
        syntax.put("jndi.syntax.direction", "flat");
        syntax.put("jndi.syntax.ignorecase", "false");
    }
    
    public Name parse(String name) throws NamingException {
        return new CompoundName(name, syntax);
    }
}

