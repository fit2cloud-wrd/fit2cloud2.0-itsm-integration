package com.fit2cloud.itsm.dao;

import com.fit2cloud.itsm.model.request.ExternalSystemSyncDetailRequest;
import com.fit2cloud.itsm.model.request.PciApiLogRequest;
import com.fit2cloud.itsm.model.response.ExternalSystemSyncDetailLogRep;
import com.fit2cloud.itsm.model.response.PciApiLogRep;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtPciApiLogMapper {
    List<PciApiLogRep> getApiLogList(@Param("record") PciApiLogRequest apiLogRequest);

    int countInPushLogs(@Param("params") Map<String, String> params);

    List<ExternalSystemSyncDetailLogRep> getSyncDetailLogs(@Param("record") ExternalSystemSyncDetailRequest apiLogRequest);

    String getLastSyncApiLog(@Param("type") String type);
}