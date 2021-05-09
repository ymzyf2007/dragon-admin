package com.dragon.modules.generator.service.impl;

import com.dragon.modules.generator.domain.GenConfig;
import com.dragon.modules.generator.repository.GenConfigRepository;
import com.dragon.modules.generator.service.GenConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service("genConfigService")
public class GenConfigServiceImpl implements GenConfigService {

    @Autowired
    private GenConfigRepository genConfigRepository;

    /**
     * 查询表配置
     *
     * @param tableName 表名
     * @return 表配置
     */
    @Override
    public GenConfig find(String tableName) {
        GenConfig genConfig = genConfigRepository.findByTableName(tableName);
        if (genConfig == null) {
            return new GenConfig(tableName);
        }
        return genConfig;
    }

    /**
     * 更新表配置
     *
     * @param tableName 表名
     * @param genConfig 表配置
     * @return 表配置
     */
    @Override
    public GenConfig update(String tableName, GenConfig genConfig) {
        String separator = File.separator;
        String[] paths;
        String symbol = "\\";
        if (symbol.equals(separator)) {
            paths = genConfig.getPath().split("\\\\");
        } else {
            paths = genConfig.getPath().split(File.separator);
        }
        StringBuilder api = new StringBuilder();
        for (String path : paths) {
            api.append(path);
            api.append(separator);
            if ("src".equals(path)) {
                api.append("api");
                break;
            }
        }
        genConfig.setApiPath(api.toString());
        return genConfigRepository.save(genConfig);
    }
}
