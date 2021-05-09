package com.dragon.modules.generator.service;

import com.dragon.modules.generator.domain.ColumnInfo;
import com.dragon.modules.generator.domain.GenConfig;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface GeneratorService {

    /**
     * 查询数据库元数据
     *
     * @param name     表名
     * @param startEnd 分页参数
     * @return
     */
    Object getTables(String name, int[] startEnd);

    /**
     * 查询当前数据库所有表信息
     *
     * @return
     */
    Object getTables();

    /**
     * 根据表名查询该表的字段数据
     *
     * @param tableName 表名
     * @return
     */
    List<ColumnInfo> getColumns(String tableName);

    /**
     * 根据表名查询数据库的表字段数据数据
     *
     * @param tableName
     * @return
     */
    List<ColumnInfo> query(String tableName);

    /**
     * 代码生成
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     */
    void generator(GenConfig genConfig, List<ColumnInfo> columns);

    /**
     * 预览
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     * @return
     */
    ResponseEntity<Object> preview(GenConfig genConfig, List<ColumnInfo> columns);

    /**
     * 打包下载
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     * @param request
     * @param response
     */
    void download(GenConfig genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response);

}
