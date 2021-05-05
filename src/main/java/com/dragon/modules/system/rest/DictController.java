package com.dragon.modules.system.rest;

import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.logging.annotation.Log;
import com.dragon.modules.system.domain.Dict;
import com.dragon.modules.system.dto.param.DictQueryCriteria;
import com.dragon.modules.system.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@RestController
@RequestMapping("/api/dict")
@Api(tags = "系统: 字典管理")
public class DictController {

    @Autowired
    private DictService dictService;

    private static final String ENTITY_NAME = "dict";

    @ApiOperation("根据条件分页查询字典数据")
    @GetMapping
    @PreAuthorize("@dragon.check('dict:list')")
    public ResponseEntity<Object> query(DictQueryCriteria resources, Pageable pageable) {
        return new ResponseEntity<>(dictService.queryAll(resources, pageable), HttpStatus.OK);
    }

    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("根据条件查询所有字典数据")
    @GetMapping(value = "/all")
    @PreAuthorize("@dragon.check('dict:list')")
    public ResponseEntity<Object> queryAll() {
        return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()), HttpStatus.OK);
    }

    @Log("新增字典")
    @ApiOperation("新增字典")
    @PostMapping
    @PreAuthorize("@dragon.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dict resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        dictService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改字典")
    @ApiOperation("修改字典")
    @PutMapping
    @PreAuthorize("@dragon.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(Dict.Update.class) @RequestBody Dict resources) {
        dictService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("根据字典ID列表删除字典")
    @ApiOperation("根据字典ID列表删除字典")
    @DeleteMapping
    @PreAuthorize("@dragon.check('dict:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        dictService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dragon.check('dict:list')")
    public void download(HttpServletResponse response, DictQueryCriteria criteria) throws IOException {
        dictService.download(dictService.queryAll(criteria), response);
    }
}
