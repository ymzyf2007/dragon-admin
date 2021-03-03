package com.dragon.modules.common.base;

import java.util.List;

public interface BaseMapper<D, E> {

    /**
     * DTO转Entity
     * @param dto
     * @return
     */
    E toEntity(D dto);

    /**
     * DTO集合转Entity集合
     * @param dtoList
     * @return
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Entity转DTO
     * @param entity
     * @return
     */
    D toDto(E entity);

    /**
     * Entity集合转DTO集合
     * @param entityList
     * @return
     */
    List<D> toDto(List<E> entityList);

}
