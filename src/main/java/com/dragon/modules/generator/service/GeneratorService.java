package com.dragon.modules.generator.service;

public interface GeneratorService {

    /**
     * 查询数据库元数据
     *
     * @param name     表名
     * @param startEnd 分页参数
     * @return
     */
    Object getTables(String name, int[] startEnd);

}
