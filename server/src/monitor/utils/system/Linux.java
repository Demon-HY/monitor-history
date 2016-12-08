package monitor.utils.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Linux {
    /**
     * 获取Linux的 CPU ID
     * @return
     */
    public static String getLinuxCPUID() {
        String cpuId = null;
        BufferedReader bufferedReader = null;  
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("dmidecode -t 4 | grep ID");
            bufferedReader = new BufferedReader(new InputStreamReader(  
                    process.getInputStream()));
            String line = null;  
            int index = -1;  
            while ((line = bufferedReader.readLine()) != null) {  
                index = line.toLowerCase().indexOf("id:");  
                if (index >= 0) {
                    cpuId = line.substring(index + "id:".length() + 1).trim();  
                    break;  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (bufferedReader != null) {  
                    bufferedReader.close();  
                }  
            } catch (IOException e1) {  
                e1.printStackTrace();  
            }  
            bufferedReader = null;  
            process = null;  
        } 
        
        return cpuId;
    }
    
    /**
     * 获得Linux下主板 ID
     * @return
     */
    public static String getLinuxBoardID() {
        String biosId = null;
        BufferedReader bufferedReader = null;  
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("dmidecode | more");
            bufferedReader = new BufferedReader(new InputStreamReader(  
                    process.getInputStream()));
            String line = null;  
            int index = -1;  
            while ((line = bufferedReader.readLine()) != null) {  
                index = line.toLowerCase().indexOf("uuid:");  
                if (index >= 0) {
                    biosId = line.substring(index + "uuid:".length() + 1).trim();  
                    break;  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (bufferedReader != null) {  
                    bufferedReader.close();  
                }  
            } catch (IOException e1) {  
                e1.printStackTrace();  
            }  
            bufferedReader = null;  
            process = null;  
        } 
        return biosId;
    }
}
