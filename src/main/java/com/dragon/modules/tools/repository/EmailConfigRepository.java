package com.dragon.modules.tools.repository;

import com.dragon.modules.tools.domain.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailConfigRepository extends JpaRepository<EmailConfig, Long> {
}
