package com.personneltrackingsystem.handler;

import java.util.Date;

public class CustomException<E> {
    private String hostName;
    private String path;
    private Date createTime;
    private E message;

    // Getter ve setter metodları
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public E getMessage() {
        return message;
    }

    public void setMessage(E message) {
        this.message = message;
    }
}