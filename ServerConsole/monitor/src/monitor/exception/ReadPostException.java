package monitor.exception;

//@javadoc

/**
 * 读取端口异常
 * @author monitor
 *
 */
public class ReadPostException extends Exception {

    private static final long serialVersionUID = -3233912315793368511L;

    public ReadPostException(String message) {
		super(message);
	}
}