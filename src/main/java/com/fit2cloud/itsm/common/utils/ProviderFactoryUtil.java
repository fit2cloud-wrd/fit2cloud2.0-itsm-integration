package com.fit2cloud.itsm.common.utils;

import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.integration.ITSMIntegration;
import com.fit2cloud.itsm.model.dto.VendorDTO;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.*;

@Configuration
public class ProviderFactoryUtil {

    private static List<VendorDTO> vendorDTOList = new ArrayList<>();
    private static Map<String, VendorDTO> vendorDTOMap = new HashMap<>();
    private static Map<String, ITSMIntegration> providerMap = new HashMap<>();
    static {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(ITSMIntegration.class.getPackage().getName())
                    .addScanners(new SubTypesScanner()));

            Set<Class<? extends ITSMIntegration>> implClass = reflections.getSubTypesOf(ITSMIntegration.class);
            for(Class<? extends ITSMIntegration> subClass : implClass){
                Class<?> cls = Class.forName(subClass.getName());

                Method getNameMethod = subClass.getDeclaredMethod("getName");
                String name = (String) getNameMethod.invoke(cls.newInstance());

                Method getIdMethod = subClass.getDeclaredMethod("getId");
                String id = (String) getIdMethod.invoke(cls.newInstance());

                VendorDTO vendorDTO = new VendorDTO();
                vendorDTO.setId(id);
                vendorDTO.setName(name);
                vendorDTOList.add(vendorDTO);
                vendorDTOMap.put(id, vendorDTO);
                providerMap.put(id, subClass.newInstance());
            }
        } catch (Exception exception) {
            LogUtil.info("ProviderFactoryUtil 反射获取 IProviderFactory 实现类异常：" + exception.getMessage());
            LogUtil.error("ProviderFactoryUtil 反射获取 IProviderFactory 实现类异常：" + exception.getMessage());
        }
    }

    public static Map<String, ITSMIntegration> getProviderMap() {
        return providerMap;
    }

    public static Map<String, VendorDTO> getVendorDTOMap() {
        return vendorDTOMap;
    }

    public static List<VendorDTO> getVendorDTOList() {
        return vendorDTOList;
    }
}
