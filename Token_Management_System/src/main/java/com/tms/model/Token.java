package com.tms.model;

import java.sql.Timestamp;

/** one row of the tokens table, plus joined display fields */
public class Token {

    private int tokenId;
    private String tokenNo;     // e.g. A-001
    private int userId;
    private int serviceId;
    private int counterId;      // 0 while still waiting
    private String status;      // WAITING / CALLED / SERVED / CANCELLED / NOSHOW
    private Timestamp issuedAt;
    private Timestamp calledAt;
    private Timestamp doneAt;

    // joined for display
    private String userName;
    private String serviceName;
    private String counterName;
    private int avgMinutes;     // handling time of the service
    private int position;       // how many tokens are still ahead (WAITING only)

    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }

    public String getTokenNo() { return tokenNo; }
    public void setTokenNo(String tokenNo) { this.tokenNo = tokenNo; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getCounterId() { return counterId; }
    public void setCounterId(int counterId) { this.counterId = counterId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Timestamp issuedAt) { this.issuedAt = issuedAt; }

    public Timestamp getCalledAt() { return calledAt; }
    public void setCalledAt(Timestamp calledAt) { this.calledAt = calledAt; }

    public Timestamp getDoneAt() { return doneAt; }
    public void setDoneAt(Timestamp doneAt) { this.doneAt = doneAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getCounterName() { return counterName; }
    public void setCounterName(String counterName) { this.counterName = counterName; }

    public int getAvgMinutes() { return avgMinutes; }
    public void setAvgMinutes(int avgMinutes) { this.avgMinutes = avgMinutes; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    /** minutes the customer waited before being called */
    public long getWaitMinutes() {
        if (calledAt == null) return 0;
        return (calledAt.getTime() - issuedAt.getTime()) / 60000L;
    }

    /** total minutes from issue to finish */
    public long getTotalMinutes() {
        if (doneAt == null) return 0;
        return (doneAt.getTime() - issuedAt.getTime()) / 60000L;
    }

    /** rough waiting estimate = people ahead x handling time */
    public int getEstimatedWait() {
        return (position + 1) * avgMinutes;
    }
}
