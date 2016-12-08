package monitor.service.http.protocol;

import java.lang.reflect.Method;

import monitor.service.http.Env;

public class DefaultProtocol implements Protocol {

	@Override
	public void process(Env env, Object apiSuite, Method m) throws Exception {
			m.invoke(apiSuite, env);
	}

	@Override
	public void setOption(int option) {
		
	}
	
	public void setStrOption(String strOption){
	    
	}
	
	public boolean isValidMethod(Method m){
        Class<?>[] params = m.getParameterTypes();
        if (!(params.length == 1 && params[0].equals(Env.class))) {
            return false;
        }
        
        if (m.getReturnType() != void.class) {
            return false;
        }
        
        return true;
    }
	
}
