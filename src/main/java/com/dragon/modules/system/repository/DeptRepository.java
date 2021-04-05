package com.dragon.modules.system.repository;

import com.dragon.modules.system.domain.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeptRepository extends JpaRepository<Dept, Long>, JpaSpecificationExecutor<Dept> {
}
