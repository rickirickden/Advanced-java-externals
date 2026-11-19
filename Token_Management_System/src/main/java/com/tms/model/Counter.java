package com.tms.model;

/** one row of the counters table, plus joined display fields */
public class Counter {

    private int counterId;
    private String name;
    private String operatorName;
    private int serviceId;
    private String status;          // OPEN or CLOSED

    // filled by the JOINs in the DAO, not stored in the table
    private String serviceName;
    private String currentTokenNo;  // token being served right now (or null)
    private int waitingCount;       // waiting tokens for this counter's service

    public int getCounterId() { return counterId; }
    public void setCounterId(int counterId) { this.counterId = counterId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getCurrentTokenNo() { return currentTokenNo; }
    public void setCurrentTokenNo(String currentTokenNo) { this.currentTokenNo = currentTokenNo; }

    public int getWaitingCount() { return waitingCount; }
    public void setWaitingCount(int waitingCount) { this.waitingCount = waitingCount; }
}
