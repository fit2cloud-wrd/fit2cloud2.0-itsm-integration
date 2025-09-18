package com.fit2cloud.itsm.dao;

import com.fit2cloud.itsm.model.request.PciExternalSystemOrgReq;
import com.fit2cloud.itsm.model.request.PciExternalSystemUserReq;
import com.fit2cloud.itsm.model.request.SyncOrgRequest;
import com.fit2cloud.itsm.model.request.SyncUserRequest;
import com.fit2cloud.itsm.model.response.PciExternalSystemOrgRep;
import com.fit2cloud.itsm.model.response.PciExternalSystemUserRep;
import com.fit2cloud.itsm.model.response.PciSyncOrgSettingRep;
import com.fit2cloud.itsm.model.response.PciSyncUserSettingRep;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtPciSyncSettingMapper {
    List<PciSyncOrgSettingRep> getSyncOrgList(@Param("record") SyncOrgRequest syncOrgRequest);

    List<PciSyncUserSettingRep> getSyncUserList(@Param("record") SyncUserRequest syncOrgRequest);

    List<PciExternalSystemOrgRep> getExternalSystemOrgList(@Param("record") PciExternalSystemOrgReq request);

    List<PciExternalSystemUserRep> getExternalSystemUserList(@Param("record") PciExternalSystemUserReq request);
}