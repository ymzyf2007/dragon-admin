package com.dragon.modules.system.rest;

import com.dragon.modules.system.dto.param.DictQueryCriteria;
import com.dragon.modules.system.service.DictService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dict")
@Api(tags = "系统: 字典管理")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping(value = "/all")
    @PreAuthorize("@dragon.check('dict:list')")
    public ResponseEntity<Object> queryAll() {
        return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()), HttpStatus.OK);
    }

//    @ApiOperation("查询字典")
//    @GetMapping
//    @PreAuthorize("@dragon.check('dict:list')")
//    public ResponseEntity<Object> query(DictQueryCriteria resources, Pageable pageable){
//        return new ResponseEntity<>(dictService.queryAll(resources,pageable),HttpStatus.OK);
//    }

}
