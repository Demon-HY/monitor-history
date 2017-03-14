package monitor.service.log;

import java.io.File;
import java.util.Date;

import monitor.utils.Time;

public class TestLogger {

	public static void main(String[] args) throws Exception {
//		Logger.init("/tmp/log", "debug", "false", "-1");
//		Logger logger = Logger.getInst("hello");
//		Logger.setVerbose(true);
//		
//		logger.info("info");
//		logger.debug("debug");
//		logger.err("err");
		
		File file = new File("/tmp/log/acc.log");
		long lm = file.lastModified();
		String l = Time.getDateStr(new Date(lm));
		System.out.println(l);
	}

}
