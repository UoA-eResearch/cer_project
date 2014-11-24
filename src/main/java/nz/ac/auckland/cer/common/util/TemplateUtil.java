package nz.ac.auckland.cer.common.util;

import java.util.Map;

public class TemplateUtil {

    public String substituteParameters(
            String templateText,
            Map<String, String> templateParams) throws Exception {

        String tmp = templateText;
        if (tmp != null && templateParams != null) {
            for (String key : templateParams.keySet()) {
                String val = (String) templateParams.get(key);
                if (val == null) {
                    val = "N/A";
                }
                tmp = tmp.replace(key, val);
            }
        }
        return tmp;
    }

}
