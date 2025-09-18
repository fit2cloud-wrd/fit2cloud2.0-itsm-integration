package com.fit2cloud.itsm.common.utils;

import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Objects;

public class ReadConfigFileUtil {

    public static String readConfigFile(String fileName) {
        Reader reader = null;
        FileReader fileReader = null;

        try {
            Resource resource = new ClassPathResource(fileName);
            InputStream stream = resource.getInputStream();
            reader = new InputStreamReader(stream,"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (Exception var20) {
            LogUtil.error("Failed to load json file. ");
            throw new RuntimeException(var20.getMessage());
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException var19) {
                }

                fileReader = null;
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var18) {
                }

                reader = null;
            }
        }
    }
}
