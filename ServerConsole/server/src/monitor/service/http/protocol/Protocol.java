package monitor.service.http.protocol;

import java.lang.reflect.Method;

import monitor.service.http.Env;

public interface Protocol {
    public void process(Env env, Object apiSuite, Method m) throws Exception;
    public void setOption(int option);
    public void setStrOption(String setStrOption);
    public boolean isValidMethod(Method m);
}
