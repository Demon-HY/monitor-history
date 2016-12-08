package monitor.service.http.protocol;

import monitor.service.http.Env;

/**
 * 错误码解析器
 */
public interface ErrTextFormat {
	/**
	 * 获取错误码的解析文本
	 * 
	 * @param env
	 * @param stat 错误码
	 * @return
	 */
    public String getErrText(Env env, String stat);
}
