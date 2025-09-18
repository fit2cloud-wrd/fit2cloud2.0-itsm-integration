package com.fit2cloud.itsm.controller;

import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.itsm.common.constants.PermissionConstants;
import com.fit2cloud.itsm.model.PciProcess;
import com.fit2cloud.itsm.model.request.PciProcessRequest;
import com.fit2cloud.itsm.service.impl.PciProcessService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("pci/process")
public class PciProcessController {

    @Resource
    private PciProcessService pciProcessService;

    @ApiOperation("查询流程对接列表")
    @RequiresPermissions(PermissionConstants.PCI_PROCESS_READ)
    @GetMapping("/list/{goPage}/{pageSize}")
    public Pager<List<PciProcess>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody PciProcessRequest request){
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, pciProcessService.list(request));
    }
}
