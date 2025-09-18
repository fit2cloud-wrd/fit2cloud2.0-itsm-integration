package com.fit2cloud.itsm.controller;

import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.itsm.service.impl.WorkspaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/workspace")
@RestController
public class WorkspaceController {
    @Resource
    private WorkspaceService workspaceService;

    @GetMapping(value = "listAll")
    public List<Workspace> workspaces() {
        return workspaceService.listOfUser();
    }
}
