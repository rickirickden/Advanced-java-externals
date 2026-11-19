package com.tms.model;

/** one row of the services table */
public class Service {

    private int serviceId;
    private String name;
    private String code;       // letter used in token numbers, e.g. A-001
    private int avgMinutes;    // rough handling time per customer
    private boolean active;

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getAvgMinutes() { return avgMinutes; }
    public void setAvgMinutes(int avgMinutes) { this.avgMinutes = avgMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
