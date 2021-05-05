package com.dragon.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dragon.modules.common.enums.DataScopeEnum;
import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.utils.*;
import com.dragon.modules.system.domain.Dept;
import com.dragon.modules.system.domain.User;
import com.dragon.modules.system.dto.param.DeptQueryCriteria;
import com.dragon.modules.system.dto.result.DeptDto;
import com.dragon.modules.system.mapstruct.DeptMapper;
import com.dragon.modules.system.repository.DeptRepository;
import com.dragon.modules.system.repository.RoleRepository;
import com.dragon.modules.system.repository.UserRepository;
import com.dragon.modules.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = "dept")
@Service("deptService")
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DeptMapper deptMapper;

    /**
     * 根据部门ID查询
     *
     * @param id
     * @return
     */
    @Cacheable(key = "'id:' + #p0")
    @Override
    public DeptDto findById(Long id) {
        Dept dept = deptRepository.findById(id).orElseGet(Dept::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", id);
        return deptMapper.toDto(dept);
    }

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
     * 新增部门信息
     *
     * @param resources /
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(Dept resources) {
        deptRepository.save(resources);
        // 更新子部门数目
        updateSubCnt(resources.getPid());
    }

    /**
     * 更新当前部门的子部门数目
     *
     * @param deptId
     */
    private void updateSubCnt(Long deptId) {
        if (deptId != null) {
            int count = deptRepository.countByPid(deptId);
            deptRepository.updateSubCntById(count, deptId);
        }
    }

    /**
     * 修改部门信息
     *
     * @param resources /
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Dept resources) {
        // 清理缓存
        delCaches(resources.getId());

        // 旧的部门
        Long oldPid = findById(resources.getId()).getPid();
        Long newPid = resources.getPid();
        if (resources.getPid() != null && resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        Dept dept = deptRepository.findById(resources.getId()).orElseGet(Dept::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", resources.getId());
        resources.setId(dept.getId());
        deptRepository.save(resources);
        // 更新父节点中子节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
    }

    /**
     * 删除部门信息
     *
     * @param deptDtos /
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Set<DeptDto> deptDtos) {
        for (DeptDto deptDto : deptDtos) {
            // 清理缓存
            delCaches(deptDto.getId());
            deptRepository.deleteById(deptDto.getId());
            updateSubCnt(deptDto.getPid());
        }
    }

    /**
     * 获取待删除的部门
     *
     * @param deptList
     * @param deptDtos
     * @return
     */
    @Override
    public Set<DeptDto> getDeleteDepts(List<Dept> deptList, Set<DeptDto> deptDtos) {
        for (Dept dept : deptList) {
            deptDtos.add(deptMapper.toDto(dept));
            List<Dept> depts = deptRepository.findByPid(dept.getId());
            if (depts != null && depts.size() != 0) {
                getDeleteDepts(depts, deptDtos);
            }
        }
        return deptDtos;
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

    /**
     * 验证是否被角色或用户关联
     *
     * @param deptDtos
     */
    @Override
    public void verification(Set<DeptDto> deptDtos) {
        Set<Long> deptIds = deptDtos.stream().map(DeptDto::getId).collect(Collectors.toSet());
        if (userRepository.countByDepts(deptIds) > 0) {
            throw new BadRequestException("所选部门存在用户关联，请解除后再试！");
        }
        if (roleRepository.countByDepts(deptIds) > 0) {
            throw new BadRequestException("所选部门存在角色关联，请解除后再试！");
        }
    }

    /**
     * 根据ID获取同级与上级数据
     *
     * @param deptDto
     * @param depts
     * @return
     */
    @Override
    public List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> depts) {
        if (deptDto.getPid() == null) {
            depts.addAll(deptRepository.findByPidIsNull());
            return deptMapper.toDto(depts);
        }
        depts.addAll(deptRepository.findByPid(deptDto.getPid()));
        return getSuperior(findById(deptDto.getPid()), depts);
    }

    /**
     * 构建树形数据
     *
     * @param deptDtos
     * @return
     */
    @Override
    public Object buildTree(List<DeptDto> deptDtos) {
        Set<DeptDto> trees = new LinkedHashSet<>();
        Set<DeptDto> depts = new LinkedHashSet<>();
        List<String> deptNames = deptDtos.stream().map(DeptDto::getName).collect(Collectors.toList());
        boolean isChild;
        for (DeptDto deptDTO : deptDtos) {
            isChild = false;
            if (deptDTO.getPid() == null) {
                trees.add(deptDTO);
            }
            for (DeptDto it : deptDtos) {
                if (it.getPid() != null && deptDTO.getId().equals(it.getPid())) {
                    isChild = true;
                    if (deptDTO.getChildren() == null) {
                        deptDTO.setChildren(new ArrayList<>());
                    }
                    deptDTO.getChildren().add(it);
                }
            }
            if (isChild) {
                depts.add(deptDTO);
            } else if (deptDTO.getPid() != null && !deptNames.contains(findById(deptDTO.getPid()).getName())) {
                depts.add(deptDTO);
            }
        }

        if (CollectionUtil.isEmpty(trees)) {
            trees = depts;
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("totalElements", deptDtos.size());
        map.put("content", CollectionUtil.isEmpty(trees) ? deptDtos : trees);
        return map;
    }

    /**
     * 导出数据
     *
     * @param deptDtos 待导出的数据
     * @param response
     * @throws IOException
     */
    @Override
    public void download(List<DeptDto> deptDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeptDto deptDTO : deptDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", deptDTO.getName());
            map.put("部门状态", deptDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 清理缓存
     *
     * @param id
     */
    public void delCaches(Long id) {
        List<User> users = userRepository.findByDeptRoleId(id);
        // 删除数据权限
        redisUtils.delByKeys("data::user:", users.stream().map(User::getId).collect(Collectors.toSet()));
        redisUtils.del("dept::id:" + id);
    }
}
