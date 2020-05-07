package com.example.demo.domain;

import com.frameworkset.orm.annotation.ESId;
import lombok.Data;

import java.util.Date;
@Data
public class TAgentInfo implements java.io.Serializable{
    private String hostname;
    @ESId   //ip属性作为文档唯一标识，根据ip值对应的索引文档存在与否来决定添加或者修改操作
    private String ip;
    private String ports;
    private String agentId;
    private String location;
    private String applicationName;
    private int serviceType;
    private int pid;
    private String agentVersion;
    private String vmVersion;
    //日期类型
    private Date startTimestampDate;
    private Date endTimestampDate;
    private long startTimestamp;
    private long endTimestamp;
    private int endStatus;
    private String serverMetaData;
    private String jvmInfo;
}
