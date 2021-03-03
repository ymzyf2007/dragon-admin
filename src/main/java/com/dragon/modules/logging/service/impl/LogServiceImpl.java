package com.dragon.modules.logging.service.impl;

import com.dragon.modules.common.utils.PageUtil;
import com.dragon.modules.common.utils.QueryHelper;
import com.dragon.modules.logging.domain.Log;
import com.dragon.modules.logging.dto.param.LogQueryCriteria;
import com.dragon.modules.logging.dto.result.LogErrorDTO;
import com.dragon.modules.logging.mapstruct.LogErrorMapper;
import com.dragon.modules.logging.repository.LogRepository;
import com.dragon.modules.logging.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final LogErrorMapper logErrorMapper;

    /**
     * 查询全部数据
     * @param criteria 查询条件
     * @return
     */
    @Override
    public List<Log> queryAll(LogQueryCriteria criteria) {
        return logRepository.findAll((root, criteriaQuery, cb) -> QueryHelper.getPredicate(root, criteria, cb));
    }

    /**
     * 分页查询
     * @param criteria 查询条件
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelper.getPredicate(root, criteria, cb)), pageable);
        String status = "ERROR";
        if (status.equals(criteria.getLogType())) {
//            return PageUtil.toPage(page.map(logErrorMapper::toDto));
            return PageUtil.toPage(page.map(new Function<Log, LogErrorDTO>() {
                @Override
                public LogErrorDTO apply(Log entity) {
                    return logErrorMapper.toDto(entity);
                }
            }));
        }
        return page;
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        return null;
    }

    @Override
    public Object findByErrDetail(Long id) {
        return null;
    }

    @Override
    public void download(List<Log> logs, HttpServletResponse response) throws IOException {

    }

    @Override
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log) {

    }

    @Override
    public void delAllByError() {

    }

    @Override
    public void delAllByInfo() {

    }
}
