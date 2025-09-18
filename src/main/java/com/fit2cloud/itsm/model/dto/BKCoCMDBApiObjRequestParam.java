package com.fit2cloud.itsm.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class BKCoCMDBApiObjRequestParam {

    String apiLogId;

    String baseUrl;
    String endpointPath;

    String requestMethod;

    // credential
    String appCode;
    String appSecret;
    String userName;


    public Map<String, String> getCredentialMap() {
        Map<String, String> map = new HashMap<>();
        map.put("bk_app_code", appCode);
        map.put("bk_app_secret", appSecret);
        map.put("bk_username", userName);
        return Collections.unmodifiableMap(map);
    }

    public String getFullPath() {
        return getFullPath(this.endpointPath);
    }

    public String getFullPath(String endpointPath) {
        String url = this.baseUrl;
        if (!this.baseUrl.endsWith("/")) {
            url = url + "/";
        }

        if (endpointPath.startsWith("/")) {
            url = url + endpointPath.replaceFirst("/", "");
        } else {
            url = url + endpointPath;
        }
        return url;
    }


}
