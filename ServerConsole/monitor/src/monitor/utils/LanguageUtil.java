package monitor.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import monitor.Config;
import monitor.exception.LogicalException;
import monitor.service.log.Logger;

public class LanguageUtil {
    //错误码 用于写日志 抛Logical异常
    public static final String ERR_LANGUAGE_KEY_CONFLICT = "ERR_LANGUAGE_KEY_CONFLICT";

    public static final String ERR_READ_LANGUAGE_XML = "ERR_READ_LANGUAGE_XML";

    //server.properties 中 配置支持的语言类型
    public static final String CONF_LANGUAGE_TYPE = "monitor.LanguageUtil.supportedLanguage";

    public static final String supportedLanguage = Config.get(CONF_LANGUAGE_TYPE);

    public static final List<String> languages = Arrays.asList(supportedLanguage.split(","));

    //server.properties 中配置默认语言类型
    public static final String CONF_DEFAULT_LANGUAGE_TYPE = "monitor.LanguageUtil.defaultLanuage";

    public static final String defaultLanguage = Config.get(CONF_DEFAULT_LANGUAGE_TYPE);

    //所有语言资源放在该Map中
    private Map<String, Map<String, String>> LanguageMap = new HashMap<String, Map<String, String>>();

    private static LanguageUtil languageUtil = new LanguageUtil();

    protected LanguageUtil() {
    }

    public static LanguageUtil getInst() {
        return languageUtil;
    }

    @SuppressWarnings("unchecked")
    public void regist(String file) throws LogicalException {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            Logger.getInst().err("Load Language xml failed! file--" + file + "\n" + e);
        }
        if (document == null) {
            throw new LogicalException(ERR_READ_LANGUAGE_XML, "Fail to read language.xml! file--" + file);
        }
        Element docs = document.getRootElement();
        List<Element> dictionarys = docs.elements();
        for (Element dictionary : dictionarys) {
            String languageType = dictionary.getName();
            if (!languages.contains(languageType)) {
                continue;
            }

            Map<String, String> desc = this.LanguageMap.get(dictionary.getName());
            if (null == desc) {
                desc = new HashMap<String, String>();
                this.LanguageMap.put(dictionary.getName(), desc);
            }

            List<Element> words = dictionary.elements();
            for (Element word : words) {
                if (!isKeyConflict(word.attributeValue("textKey"), dictionary.getName())) {
                    desc.put(word.attributeValue("textKey"), word.getText());
                } else {
                    throw new LogicalException(ERR_LANGUAGE_KEY_CONFLICT, "Conflict! language key--" + word.attributeValue("textKey"));
                }
            }
        }
    }

    public String getText(String key, String languageType) {
        Map<String, String> language = this.LanguageMap.get(languageType);
        if (null == language) {
            language = this.LanguageMap.get(defaultLanguage);
            if (null == language) {
                Logger.getInst().err(
                        "Default language--" + languageType + " is not supported! Please check server.properties and language.xml! key--"
                                + key);
                return "no text matched. no  language:" + languageType;
            }
        }

        String text = language.get(key);
        if (null == text) {
            text = "no text matched:" + key;
        }

        return text;
    }

    private boolean isKeyConflict(String key, String Language) {
        Map<String, String> map = this.LanguageMap.get(Language);
        if (map != null && map.containsKey(key)) {
            return true;
        }
        return false;
    }

}
