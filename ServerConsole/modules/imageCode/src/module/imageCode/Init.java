package module.imageCode;

import module.SDK.SdkCenter;

public class Init {
    
    public static String MODULE_NAME = "imageCode";

    public static void init(String moduleDir) throws Exception {
        ImageCodeApi.init();
        
        SdkCenter.getInst().registHttpApi(MODULE_NAME, new ImageCodeHttpApi(ImageCodeApi.getInst()));
    }
}