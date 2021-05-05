package com.dragon.modules.system.rest;

import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.logging.annotation.Log;
import com.dragon.modules.system.domain.Job;
import com.dragon.modules.system.dto.param.JobQueryCriteria;
import com.dragon.modules.system.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Api(tags = "系统: 岗位管理")
@RequestMapping("/api/job")
@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    private static final String ENTITY_NAME = "job";

    @ApiOperation("分页查询岗位信息")
    @GetMapping
    @PreAuthorize("@dragon.check('job:list','user:list')")
    public ResponseEntity<Object> query(JobQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(jobService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("新增岗位信息")
    @ApiOperation("新增岗位信息")
    @PostMapping
    @PreAuthorize("@dragon.check('job:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Job resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        jobService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改岗位信息")
    @ApiOperation("修改岗位信息")
    @PutMapping
    @PreAuthorize("@dragon.check('job:edit')")
    public ResponseEntity<Object> update(@Validated(Job.Update.class) @RequestBody Job resources) {
        jobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除岗位")
    @ApiOperation("删除岗位")
    @DeleteMapping
    @PreAuthorize("@dragon.check('job:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        // 验证是否被用户关联
        jobService.verification(ids);
        jobService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
