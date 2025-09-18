package com.fit2cloud.itsm.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.service.MicroService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.model.pm.PhysicalMachineDTO;
import com.fit2cloud.itsm.model.fusionaccess.DesktopServerDTO;
import com.fit2cloud.itsm.model.loadbalancer.LbLtmVirtualRet;
import com.fit2cloud.itsm.model.loadbalancer.Virtual;
import com.fit2cloud.itsm.model.vm.VmCloudServerDTO;
import com.fit2cloud.sdk.model.F2CDisk;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ResourceModuleService {
    @Resource
    private MicroService microService;
    @Resource
    private UserCommonService userCommonService;

    public static final String LB_MODULE_ID = "loadbalancer-service";
    private final String LB_RESOURCE_URL = "ltm/resource/list/%s/%s";
    private final String LB_RESOURCE_DETAIL_URL = "ltm/resource/detail";

    public static final String FA_MODULE_ID = "fusionaccess-service";
    private final String FA_RESOURCE_URL = "/server/list/%s/%s";


    public static final String PM_MODULE_ID = "physical-machine";
    private final String PM_RESOURCE_URL = "physicalMachine/list/%s/%s";


    public static final String VM_MODULE_ID = "vm-service";
    private final String VM_RESOURCE_URL = "server/list/%s/%s";
    private final String VM_DISK_URL = "server/disk/";

    public static final String JUMPSERVER_CONNECTOR = "jumpserver-integration";
    private final String JC_INSTANCE_URL= "instance/list/%s/%s";


    public List<LbLtmVirtualRet> getF5ServerList() {
        if (!isActiveService(LB_MODULE_ID)) {
            return Collections.emptyList();
        }

        JSONObject req = new JSONObject();
        req.put("f5Status", Collections.singletonList("Running"));
        String reqStr = req.toJSONString();

        JSONArray res = fetchPageRes(LB_RESOURCE_URL, LB_MODULE_ID, reqStr);
        return res.toJavaList(LbLtmVirtualRet.class);
    }


    public Virtual getF5ServerDetail(String accountId, String fullPath) {
        if (!isActiveService(LB_MODULE_ID)) {
            return null;
        }

        User adminUser = userCommonService.getSessionUserOrAdmin();
        Map<String, String> req = new HashMap<>();
        req.put("accountId", accountId);
        req.put("fullPath", fullPath);
        ResultHolder resultHolder = microService.runAsUser(adminUser).postForResultHolder(LB_MODULE_ID, LB_RESOURCE_DETAIL_URL, req);

        JSONObject res = JSONObject.parseObject(JSON.toJSONString(resultHolder.getData()));
        return res.toJavaObject(Virtual.class);
    }

    public List<DesktopServerDTO> getDesktopServerList() {

        if (!isActiveService(FA_MODULE_ID)) {
            return Collections.emptyList();
        }

        JSONObject req = new JSONObject();
        req.put("instanceState", Arrays.asList("Running", "Stopped"));
        String reqStr = req.toJSONString();
        JSONArray res = fetchPageRes(FA_RESOURCE_URL, FA_MODULE_ID, reqStr);
        return res.toJavaList(DesktopServerDTO.class);
    }


    public List<PhysicalMachineDTO> getPhysicalMachineList() {

        if (!isActiveService(PM_MODULE_ID)) {
            return Collections.emptyList();
        }

        JSONObject req = new JSONObject();
        String reqStr = req.toJSONString();
        JSONArray res = fetchPageRes(PM_RESOURCE_URL, PM_MODULE_ID, reqStr);
        return res.toJavaList(PhysicalMachineDTO.class);
    }
    public Boolean queryVmFortressMachineManagement(String id) {

        if (!isActiveService(JUMPSERVER_CONNECTOR)) {
            return null;
        }
        JSONObject req = new JSONObject();
        req.put("cmpId", id);
        req.put("resourceViewType", "cmp");

        String reqStr = req.toJSONString();
        JSONArray res = fetchPageRes(JC_INSTANCE_URL, JUMPSERVER_CONNECTOR, reqStr);

        if (CollectionUtils.isEmpty(res)) {
            return null;
        }

        JSONObject jsonObject = res.getJSONObject(0);

        Boolean success = jsonObject.getBoolean("success");
        return !Objects.isNull(success) && success;
    }

    public VmCloudServerDTO getVmServiceById(String id) {
        if (!isActiveService(VM_MODULE_ID)) {
            return null;
        }
        JSONObject req = new JSONObject();
        req.put("cloudServerId", id);
        String reqStr = req.toJSONString();
        JSONArray res = fetchPageRes(VM_RESOURCE_URL, VM_MODULE_ID, reqStr);

        if (CollectionUtils.isEmpty(res)) {
            return null;
        }
        return res.getObject(0, VmCloudServerDTO.class);
    }


    public List<F2CDisk> getVmDiskById(String id) {

        if (!isActiveService(VM_MODULE_ID)) {
            return null;
        }

        User adminUser = userCommonService.getSessionUserOrAdmin();
        ResultHolder resultHolder = microService.runAsUser(adminUser)
                .getForResultHolder(VM_MODULE_ID, VM_DISK_URL + id);
        return JSONObject.parseArray(JSON.toJSONString(resultHolder.getData()), F2CDisk.class);
    }

    private JSONArray fetchPageRes(String baseUrl, String moduleId, String reqStr) {
        User adminUser = userCommonService.getSessionUserOrAdmin();
        int pageSize = 1000; // 每页大小
        int goPage = 1; // 起始页码
        int pageCount = -1; // 初始化为-1，表示尚未获取总页数
        JSONArray res = new JSONArray();
        try {
            do {
                String url = String.format(baseUrl, goPage, pageSize);
                ResultHolder resultHolder = microService.runAsUser(adminUser)
                        .postForResultHolder(moduleId, url, reqStr);
                JSONObject pageData = JSONObject.parseObject(JSON.toJSONString(resultHolder.getData()));
                if (pageCount == -1) {
                    pageCount = pageData.getInteger("pageCount");
                }
                JSONArray jsonArray = pageData.getJSONArray("listObject");
                res.addAll(jsonArray);
                goPage++;
            } while (goPage <= pageCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public boolean isActiveService(String moduleId) {
        return microService.isActiveService(moduleId);
    }
}
