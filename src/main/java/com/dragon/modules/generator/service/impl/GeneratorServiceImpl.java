package com.dragon.modules.generator.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.utils.FileUtil;
import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.generator.domain.ColumnInfo;
import com.dragon.modules.generator.domain.GenConfig;
import com.dragon.modules.generator.domain.vo.TableInfo;
import com.dragon.modules.generator.repository.ColumnInfoRepository;
import com.dragon.modules.generator.service.GeneratorService;
import com.dragon.modules.generator.utils.GenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("generatorService")
public class GeneratorServiceImpl implements GeneratorService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ColumnInfoRepository columnInfoRepository;

    /**
     * 查询数据库元数据
     *
     * @param name     表名
     * @param startEnd 分页参数
     * @return
     */
    @Override
    public Object getTables(String name, int[] startEnd) {
        // 使用预编译防止sql注入
        String sql = "select table_name, create_time, engine, table_collation, table_comment from information_schema.tables " +
                "where table_schema = (select database()) " +
                "and table_name like ? order by create_time desc";
        Query query = em.createNativeQuery(sql);
        query.setFirstResult(startEnd[0]);
        query.setMaxResults(startEnd[1] - startEnd[0]);
        query.setParameter(1, StringUtils.isNotBlank(name) ? ("%" + name + "%") : "%%");
        List result = query.getResultList();
        List<TableInfo> tableInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            tableInfos.add(new TableInfo(String.valueOf(arr[0]), arr[1], String.valueOf(arr[2]), String.valueOf(arr[3]), ObjectUtil.isNotEmpty(arr[4]) ? String.valueOf(arr[4]) : "-"));
        }
        Query query1 = em.createNativeQuery("SELECT COUNT(*) from information_schema.tables where table_schema = (select database())");
        Object totalElements = query1.getSingleResult();
        return PageUtil.toPage(tableInfos, totalElements);
    }

    /**
     * 查询当前数据库所有表信息
     *
     * @return
     */
    @Override
    public Object getTables() {
        // 使用预编译防止sql注入
        String sql = "select table_name,create_time, engine, table_collation, table_comment from information_schema.tables " +
                "where table_schema = (select database()) " +
                "order by create_time desc";
        Query query = em.createNativeQuery(sql);
        return query.getResultList();
    }

    /**
     * 根据表名查询该表的字段数据
     *
     * @param tableName 表名
     * @return
     */
    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        List<ColumnInfo> columnInfos = columnInfoRepository.findByTableNameOrderByIdAsc(tableName);
        if (CollectionUtil.isNotEmpty(columnInfos)) {
            return columnInfos;
        } else {
            columnInfos = query(tableName);
            return columnInfoRepository.saveAll(columnInfos);
        }
    }

    /**
     * 根据表名查询数据库的表字段数据数据
     *
     * @param tableName
     * @return
     */
    @Override
    public List<ColumnInfo> query(String tableName) {
        // 使用预编译防止sql注入
        String sql = "select column_name, is_nullable, data_type, column_comment, column_key, extra from information_schema.columns " +
                "where table_name = ? and table_schema = (select database()) order by ordinal_position";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, tableName);
        List result = query.getResultList();
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            columnInfos.add(
                    new ColumnInfo(
                            tableName,
                            arr[0].toString(),
                            "NO".equals(arr[1]),
                            arr[2].toString(),
                            ObjectUtil.isNotNull(arr[3]) ? arr[3].toString() : null,
                            ObjectUtil.isNotNull(arr[4]) ? arr[4].toString() : null,
                            ObjectUtil.isNotNull(arr[5]) ? arr[5].toString() : null)
            );
        }
        return columnInfos;
    }

    /**
     * 代码生成
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     */
    @Override
    public void generator(GenConfig genConfig, List<ColumnInfo> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        try {
            GenUtil.generatorCode(columns, genConfig);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("生成失败，请手动处理已生成的文件");
        }
    }

    /**
     * 预览
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     * @return
     */
    @Override
    public ResponseEntity<Object> preview(GenConfig genConfig, List<ColumnInfo> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        List<Map<String, Object>> genList = GenUtil.preview(columns, genConfig);
        return new ResponseEntity<>(genList, HttpStatus.OK);
    }

    /**
     * 打包下载
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     * @param request
     * @param response
     */
    @Override
    public void download(GenConfig genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        try {
            File file = new File(GenUtil.download(columns, genConfig));
            String zipPath = file.getPath() + ".zip";
            ZipUtil.zip(file.getPath(), zipPath);
            FileUtil.downloadFile(request, response, new File(zipPath), true);
        } catch (IOException e) {
            throw new BadRequestException("打包失败");
        }
    }
}
