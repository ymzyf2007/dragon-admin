package com.dragon.modules.generator.rest;

import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.generator.domain.ColumnInfo;
import com.dragon.modules.generator.service.GenConfigService;
import com.dragon.modules.generator.service.GeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "系统: 代码生成管理")
@RestController
@RequestMapping("/api/generator")
public class GeneratorController {

    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private GenConfigService genConfigService;

    @Value("${generator.enabled}")
    private Boolean generatorEnabled;

    @ApiOperation("分页查询当前数据库所有表信息")
    @GetMapping(value = "/tables")
    public ResponseEntity<Object> queryTables(@RequestParam(defaultValue = "") String name,
                                              @RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        int[] startEnd = PageUtil.transToStartEnd(page, size);
        return new ResponseEntity<>(generatorService.getTables(name, startEnd), HttpStatus.OK);
    }

    @ApiOperation("查询当前数据库所有表信息")
    @GetMapping(value = "/tables/all")
    public ResponseEntity<Object> queryTables() {
        return new ResponseEntity<>(generatorService.getTables(), HttpStatus.OK);
    }

    @ApiOperation("根据表名查询该表的字段数据")
    @GetMapping(value = "/columns")
    public ResponseEntity<Object> queryColumns(@RequestParam String tableName) {
        List<ColumnInfo> columnInfos = generatorService.getColumns(tableName);
        return new ResponseEntity<>(PageUtil.toPage(columnInfos, columnInfos.size()), HttpStatus.OK);
    }

    @ApiOperation("生成代码")
    @PostMapping(value = "/{tableName}/{type}")
    public ResponseEntity<Object> generator(@PathVariable String tableName, @PathVariable Integer type, HttpServletRequest request, HttpServletResponse response) {
        if (!generatorEnabled && type == 0) {
            throw new BadRequestException("此环境不允许生成代码，请选择预览或者下载查看！");
        }
        switch (type) {
            // 生成代码
            case 0:
                generatorService.generator(genConfigService.find(tableName), generatorService.getColumns(tableName));
                break;
            // 预览
            case 1:
                return generatorService.preview(genConfigService.find(tableName), generatorService.getColumns(tableName));
            // 打包
            case 2:
                generatorService.download(genConfigService.find(tableName), generatorService.getColumns(tableName), request, response);
                break;
            default:
                throw new BadRequestException("没有这个选项");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
