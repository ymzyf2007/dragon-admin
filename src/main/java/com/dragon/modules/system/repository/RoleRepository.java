package com.dragon.modules.system.repository;

import com.dragon.modules.system.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * 根据用户ID查询
     *
     * @param id 用户ID
     * @return
     */
    @Query(value = "SELECT r.* FROM sys_role r, sys_users_roles u WHERE " +
            "r.role_id = u.role_id AND u.user_id = ?1", nativeQuery = true)
    Set<Role> findByUserId(Long id);

    /**
     * 根据部门查询
     *
     * @param deptIds
     * @return
     */
    @Query(value = "select count(1) from sys_role r, sys_roles_depts d where " +
            "r.role_id = d.role_id and d.dept_id in ?1", nativeQuery = true)
    int countByDepts(Set<Long> deptIds);

}
