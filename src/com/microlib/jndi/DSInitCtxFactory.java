
package com.microlib.jndi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;


public class DSInitCtxFactory implements InitialContextFactory {

	private static DSCtx ds = null;
	
	@SuppressWarnings("unchecked")
    public Context getInitialContext(Hashtable<?,?> env) {
        //return new DSCtx(env);
		if (ds == null) {
			ds = new DSCtx(env);
		}
		
		return ds;
    }
}
