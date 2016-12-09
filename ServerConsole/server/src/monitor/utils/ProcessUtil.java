package monitor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class ProcessUtil {
    
    // TODO 注释一下，如果 command 运行的时间超过 timeout 了，会有什么行为
    // TODO 如何得到 command 执行的返回码？如何得到 command 执行的 stout, 如何将数据从 stdin 中输入给 command？
    /**
     * 返回命令执行的过程
     * @param timeout
     * @param command
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static ProcessStatus execute(final long timeout, final String command)
            throws IOException, InterruptedException, TimeoutException {

        Process process = Runtime.getRuntime().exec(command);

        Worker worker = new Worker(process);
        worker.start();
        ProcessStatus ps = worker.getProcessStatus();
        try {
            worker.join(timeout);
            if (ps.exitCode != 0) {
                // not finished
                worker.interrupt();
                throw new TimeoutException();
            } else {
                return ps;
            }
        } catch (InterruptedException e) {
            // canceled by other thread.
            worker.interrupt();
            throw e;
        } finally {
            process.destroy();
        }
    }

    private static class Worker extends Thread {
        private final Process process;
        private ProcessStatus ps;

        private Worker(Process process) {
            this.process = process;
            this.ps = new ProcessStatus();
        }

        public void run() {
            try {
                InputStream is = process.getInputStream();
                try {
                    ps.output = getString(is);
                } catch (IOException ignore) { }
                ps.exitCode = process.waitFor();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
          
        private String getString(InputStream is) throws IOException{
            
            String result = null;
            is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = br.readLine()) != null) {
                result += s;
            }
            is.close();
            br.close();
            return result;
        }
        
        public ProcessStatus getProcessStatus() {
            return this.ps;
        }
    }
    
    public static class ProcessStatus {
        public volatile int exitCode = -1;
        public volatile String output;
    }
}
