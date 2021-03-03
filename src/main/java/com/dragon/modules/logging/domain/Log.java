package com.dragon.modules.logging.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sys_log")
public class Log implements Serializable {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 日志类型 */
    private String logType;

    /** 操作用户 */
    private String username;

    /** 方法名 */
    private String method;

    /** 参数 */
    private String params;

    /** 请求ip */
    private String requestIp;

    /** 地址 */
    private String address;

    /** 浏览器  */
    private String browser;

    /** 请求耗时 */
    private Long time;

    /** 描述 */
    private String description;

    /** 异常详细  */
    private byte[] exceptionDetail;

    /** 创建日期 */
    @CreationTimestamp
    private Timestamp createTime;

    public Log(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }

}
