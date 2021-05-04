package com.dragon.modules.system.rest;

import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.system.dto.param.DeptQueryCriteria;
import com.dragon.modules.system.dto.result.DeptDto;
import com.dragon.modules.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dept")
@Api(tags = "系统: 部门管理")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @ApiOperation("查询部门")
    @GetMapping
    @PreAuthorize("@dragon.check('user:list', 'dept:list')")
    public ResponseEntity<Object> query(DeptQueryCriteria criteria) throws Exception {
        List<DeptDto> deptDtos = deptService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(deptDtos, deptDtos.size()), HttpStatus.OK);
    }

}
