package com.fit2cloud.itsm;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.utils.BeanTypeUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BeatUtilsTest {

    @Test
    public void testBeanParse(){
        Map<String, String> data = new HashMap<>();
        data.put("a", "1");
        data.put("b", "2");
        data.put("c", "3");
        Map parsedData = BeanTypeUtil.fromJson(JSONObject.toJSONString(data), HashMap.class, String.class, Integer.class);
        System.out.println(JSONObject.toJSONString(parsedData));
    }

}
