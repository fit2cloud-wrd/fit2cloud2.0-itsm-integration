package com.fit2cloud.itsm.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IpUtil {

    public static final String PREFIX = "[";

    public static final String SUFFIX = "]";

    public static final String COMMA = ",";

    public static List<String> getIpArrays (String ipArrays) {
        if (StringUtils.isEmpty(ipArrays)) {
            return Collections.emptyList();
        }
        String betweenIp = StringUtils.substringBetween(ipArrays, PREFIX, SUFFIX);
        if (StringUtils.isEmpty(betweenIp)) {
            return Collections.emptyList();
        }
        String replace = StringUtils.replace(betweenIp, "\"", StringUtils.EMPTY);
        String[] split = StringUtils.split(replace, COMMA);
        return split.length > 0 ? Arrays.asList(split) : Collections.emptyList();
    }
}
