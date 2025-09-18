package com.fit2cloud.itsm.model.dto;


public class BKCredential {
    /**
     * 全局地址 pass.xx.com
     */
    private String globalPath;
    /**
     * 模拟登录地址 http://paas.xx.com/login/?c_url=/
     */
    private String authPath;
    /**
     * 登录用户名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * token
     */
    private String bkToken;
    /**
     * 过期时间
     */
    private long expiredTime;
    /**
     * token有效时间 分钟
     */
    private int timeoutLimit;

    public String getGlobalPath() {
        return globalPath;
    }

    public void setGlobalPath(String globalPath) {
        this.globalPath = globalPath;
    }

    public String getAuthPath() {
        return authPath;
    }

    public void setAuthPath(String authPath) {
        this.authPath = authPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBkToken() {
        return bkToken;
    }

    public void setBkToken(String bkToken) {
        this.bkToken = bkToken;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public int getTimeoutLimit() {
        return timeoutLimit;
    }

    public void setTimeoutLimit(int timeoutLimit) {
        this.timeoutLimit = timeoutLimit;
    }
}
