package monitor.service.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.Date;

import monitor.exception.UnInitilized;
import monitor.utils.Time;

class FileLogger {
	
    /**
     * 日志文件路径
     */
	private String path = "";
	
	/**
	 * 日志文件最大大小
	 */
	private long maxSize;
	
	/**
	 * 距离上次检测文件的大小到目前写了多少条日志
	 */
	private int checkCount;
	
	/**
	 * 按天切分日志
	 */
	private boolean rotated = false;
	
	/**
	 * 当前写日志的日期
	 */
	private String writtingDate;
	
	/**
	 * 进程ID
	 */
	private String pid = "";
	
	/**
	 * 文件写入流
	 */
	private FileWriter f = null;

	/**
	 * 是否将日志打印到控制台
	 */
	public static boolean verbose = false;
	
	/**
	 * @param path 日志文件路径
	 */
	public FileLogger(String path) {
	    new FileLogger(path, false, -1);
	}
	
	/**
	 * @param path 日志文件路径
	 * @param rotated 是否按天切分日志
	 * @param maxSize 按日志文件大小切分日志
	 */
    public FileLogger(String path, boolean rotated, long maxSize) {
	    
		this.path = path;
		this.rotated = rotated;
		this.maxSize = maxSize;
		
		for (String _segment : 
			ManagementFactory.getRuntimeMXBean().getName().split("@")) {
			pid = _segment;
			break;
		}
	}
	
	private boolean _checkFile() throws IOException {
	    
		if (path == null || path.isEmpty()) {
			return false;
		}
		
		boolean isEmptyFile = false;
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            isEmptyFile = true;
        }
        
        if (f == null) {
            f = new FileWriter(path, true);
        }
        
		if (rotated) {
		    
		    String currentDate = Time.getDateStr();
		    
		    if (!isEmptyFile && writtingDate == null) {
		        long lm = file.lastModified();
		        writtingDate = Time.getDateStr(new Date(lm));
		    }
		    
	        if (!isEmptyFile && !currentDate.equalsIgnoreCase(writtingDate)) {
	            f.close();
                String tmp = path + "." + writtingDate;
                file.renameTo(new File(tmp));
                f = new FileWriter(path, true);
	        }
	        writtingDate = currentDate;
		    
		} else if (maxSize > 0) {
		    
		    checkCount = ++checkCount % 100;
		    if (checkCount == 0) {
    		    if (file.length() > maxSize) {
    		        f.close();
    		        String tmp = path + "." + Time.getDateStr() + " " + Time.currentTimeMillis();
    		        file.renameTo(new File(tmp));
    		        f = new FileWriter(path, true);
    		    }
		    }
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param name 模块名称
	 * @param msg 日志信息
	 * @throws IOException
	 */
	public synchronized void write(String name, String msg) throws IOException {
		if (!_checkFile()) {
			return;
		}
		
		String logLine = String.format("%s %s %s %s\n", Time.getDateTimeStr(), pid, name, msg);
		
		if (verbose)
			System.out.print(logLine);
		f.write(logLine);
		f.flush();
	}
	
	/**
	 * @param name 模块名称
	 * @param msg 日志信息
	 * @param e 异常对象
	 * @throws IOException
	 */
	public void write(String name, String msg, Throwable e) throws IOException {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		msg = String.format("%s Exception: %s", msg, sw.toString());
		this.write(name, msg);
	}
}

public class Logger {

    /**
     * m模块名称
     */
	private String moduleName;
	
	public Logger(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * debug类日志
	 * @param msg 日志信息
	 * @param t 异常对象
	 */
	public void debug(String msg, Throwable t) {
		try {
		    if (checkLevel("debug")) {
		        debugLog.write(moduleName, msg, t);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 访问类日志
	 * @param msg 日志信息
	 * @param t 异常对象
	 */
	public void acc(String msg, Throwable t) {
		try {
			accLog.write(moduleName, msg, t);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 普通信息类日志
	 * @param msg
	 * @param t
	 */
	public void info(String msg, Throwable t) {
		try {
		    if (checkLevel("info")) {
		        infoLog.write(moduleName, msg, t);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 警告类日志
	 * @param msg
	 * @param t
	 */
	public void warn(String msg, Throwable t) {
		try {
		    if (checkLevel("warn")) {
		        warnLog.write(moduleName, msg, t);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 错误类日志
	 * @param msg
	 * @param t
	 */
	public void err(String msg, Throwable t) {
		try {
		    if (checkLevel("error")) {
		        errLog.write(moduleName, msg, t);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 严重信息类日志
	 * @param msg
	 * @param t
	 */
	public void crit(String msg, Throwable t) {
		try {
		    if (checkLevel("crit")) {
		        critLog.write(moduleName, msg, t);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * debug类日志
     * @param msg 日志信息
     */
	public void debug(String msg) {
		try {
			debugLog.write(moduleName, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * 访问类日志
     * @param msg 日志信息
     */
	public void acc(String msg) {
		try {
			accLog.write(moduleName, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * 普通信息类日志
     * @param msg
     */
	public void info(String msg) {
		try {
			infoLog.write(moduleName, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 警告类日志
     * @param msg
     */
	public void warn(String msg) {
		try {
			warnLog.write(moduleName, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 错误类日志
     * @param msg
     */
	public void err(String msg) {
		try {
			errLog.write(moduleName, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 严重信息类日志
     * @param msg
     */
	public void crit(String msg) {
		try {
			critLog.write(moduleName, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* ------------------------------------------------------------ */
	
	public static FileLogger debugLog, accLog, infoLog, warnLog, errLog, critLog;
	
	private static int logLevel = 0;
	public static String[] levels = {"debug", "info", "warn", "error", "crit"};

	// 日志文件名称
	public static final String debugLogFileName = "/debug.log";
	public static final String accLogFileName = "/acc.log";
	public static final String infoLogFileName = "/info.log";
	public static final String warnLogFileName = "/warn.log";
	public static final String errLogFileName = "/err.log";
	public static final String critLogFileName = "/crit.log";
	
	public static Logger globalLogger;
	
	public static Logger getInst(String moduleName) throws UnInitilized {
		if (debugLog == null) {
			throw new UnInitilized("Logger not inited.");
		}
		return new Logger(moduleName);
	}
	
	public static Logger getInst() {
		return globalLogger;
	}
	
	/**
	 * 设置是否将日志信息打印到控制台
	 * @param b
	 */
	public static void setVerbose(boolean b) {
		FileLogger.verbose = b;
	}
	
	/**
	 * 
	 * @param logPath 日志路径
	 * @param level 写日记的最低级别
	 * @param ratated 是否按天切分日志
	 * @param maxFileSize 按文件大小切分日志时的最大文件容量
	 */
	public static void init(String logPath, String level, String ratated, String maxFileSize) {
	    if (null != level) {
	        level = level.toLowerCase();
	    }
	    for (int i = 0; i < levels.length; i++) {
	        if (levels[i].equals(level)) {
	            logLevel = i;
	            break;
	        }
	    }
	    
	    File file = new File(logPath);
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    logPath = file.getAbsolutePath();
	    
	    long maxSize = -1;
        if (null != maxFileSize) {
            maxSize = Long.parseLong(maxFileSize.trim());
        }
	    
		debugLog = new FileLogger(logPath + debugLogFileName, new Boolean(ratated), maxSize);
		accLog = new FileLogger(logPath + accLogFileName, new Boolean(ratated), maxSize);
		infoLog = new FileLogger(logPath + infoLogFileName, new Boolean(ratated), maxSize);
		warnLog = new FileLogger(logPath + warnLogFileName, new Boolean(ratated), maxSize);
		errLog = new FileLogger(logPath + errLogFileName, new Boolean(ratated), maxSize);
		critLog = new FileLogger(logPath + critLogFileName, new Boolean(ratated), maxSize);
		
		globalLogger = new Logger("-");
	}
	
	/**
	 * 检查日志级别
	 * @param level
	 * @return
	 */
	private static boolean checkLevel(String level) {
	    int l = 0;
	    for (int i = 0; i < levels.length; i++) {
            if (levels[i].equals(level)) {
                l = i;
                break;
            }
        }
	    if (l > logLevel) {
	        return true;
	    }
	    
	    return false;
	}

	public static void main(String[] args) {
	    String writtingDate = "12134-23-4";
	    
	    if (!writtingDate.matches("^(\\d){2,4}-(\\d){1,2}-(\\d){1,2}$")) {
            System.out.println("1111");
        }
	    
    }
}
