package com.dragon.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dragon.modules.common.enums.DataScopeEnum;
import com.dragon.modules.common.utils.QueryHelper;
import com.dragon.modules.common.utils.SecurityUtils;
import com.dragon.modules.system.domain.Dept;
import com.dragon.modules.system.dto.param.DeptQueryCriteria;
import com.dragon.modules.system.dto.result.DeptDto;
import com.dragon.modules.system.mapstruct.DeptMapper;
import com.dragon.modules.system.repository.DeptRepository;
import com.dragon.modules.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CacheConfig(cacheNames = "dept")
@Service("deptService")
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private DeptMapper deptMapper;

    /**
     * 查询所有数据
     *
     * @param criteria 条件
     * @param isQuery
     * @return
     * @throws Exception
     */
    @Override
    public List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception {
        Sort sort = Sort.by(Sort.Direction.ASC, "deptSort");
        String dataScopeType = SecurityUtils.getDataScopeType();
        if (isQuery) {
            if (dataScopeType.equals(DataScopeEnum.ALL.getValue())) {
                criteria.setPidIsNull(true);
            }
            List<Field> fields = QueryHelper.getAllFields(criteria.getClass(), new ArrayList<>());
            List<String> fieldNames = new ArrayList<String>() {{
                add("pidIsNull");
                add("enabled");
            }};
            for (Field field : fields) {
                //设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                Object val = field.get(criteria);
                if (fieldNames.contains(field.getName())) {
                    continue;
                }
                if (ObjectUtil.isNotNull(val)) {
                    criteria.setPidIsNull(null);
                    break;
                }
            }
        }
        List<DeptDto> list = deptMapper.toDto(deptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelper.getPredicate(root, criteria, criteriaBuilder), sort));
        // 如果为空，就代表为自定义权限或者本级权限，就需要去重，不理解可以注释掉，看查询结果
//        if (StringUtils.isBlank(dataScopeType)) {
//            return deduplication(list);
//        }
        return list;
    }

    /**
     * 根据PID查询
     *
     * @param pid
     * @return
     */
    @Override
    public List<Dept> findByPid(long pid) {
        return deptRepository.findByPid(pid);
    }

    /**
     * 根据角色ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Set<Dept> findByRoleId(Long id) {
        return deptRepository.findByRoleId(id);
    }

    /**
     * 获取
     *
     * @param deptList
     * @return
     */
    @Override
    public List<Long> getDeptChildren(List<Dept> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
                    if (dept != null && dept.getEnabled()) {
                        List<Dept> depts = deptRepository.findByPid(dept.getId());
                        if (deptList.size() != 0) {
                            list.addAll(getDeptChildren(depts));
                        }
                        list.add(dept.getId());
                    }
                }
        );
        return list;
    }
}
