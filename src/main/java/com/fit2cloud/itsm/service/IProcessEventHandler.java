package com.fit2cloud.itsm.service;

import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;

public interface IProcessEventHandler {

    void onProcessEvent(BusinessEventContextDTO processEventContext);

}
