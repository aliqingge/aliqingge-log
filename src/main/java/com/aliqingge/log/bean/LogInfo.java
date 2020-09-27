package com.aliqingge.log.bean;

import java.util.Date;

/**
 * 日志信息
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:40
 */
public class LogInfo {

    /**
     * 请务必注意该对象 使用->释放 原则
     */
    private static final ThreadLocal<LogInfo> LOG_INFO = new ThreadLocal<>();

    /**
     * 应用名
     */
    private String appName;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 请求Ip
     */
    private String clientIp;

    /**
     * 请求地址
     */
    private String reqUrl;

    /**
     * http请求method
     */
    private String httpMethod;

    /**
     * 请求头部信息
     */
    private Object headers;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 方法内容
     */
    private String content;

    /**
     * 操作方法
     */
    private String method;

    /**
     * 参数
     */
    private Object args;

    /**
     * 响应体
     */
    private Object respBody;

    /**
     * 操作日期
     */
    private Date logDate;

    /**
     * 业务处理耗时
     */
    private long costTime;

    /**
     * 线程名
     */
    private String threadName = Thread.currentThread().getName();

    /**
     * 线程Id
     */
    private long threadId = Thread.currentThread().getId();

    /**
     * 执行状态 成功(true)/异常(false)  默认失败false
     */
    private boolean success = false;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

    public Object getRespBody() {
        return respBody;
    }

    public void setRespBody(Object respBody) {
        this.respBody = respBody;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 耗时计算
     */
    public void toCostTime() {
        LogInfo data = LogInfo.getCurrent();
        data.setCostTime((System.currentTimeMillis() - logDate.getTime()));
        LogInfo.setCurrent(data);
    }

    /**
     * 获取当前线程中的操作日志对象
     */
    public static LogInfo getCurrent() {
        LogInfo data = LOG_INFO.get();
        if (data == null) {
            data = new LogInfo();
            data.setLogDate(new Date());
            LOG_INFO.set(data);
        }
        return LOG_INFO.get();
    }

    public static void setCurrent(LogInfo data) {
        LOG_INFO.set(data);
    }

    /**
     * 移除当前线程操作日志对象
     */
    public static void removeCurrent() {
        LOG_INFO.remove();
    }

    /**
     * 内容记录记录 正常会在aop中结束释放
     *
     * @param step 这里可以使用 该方法记录每一个步骤 : 注意 调用该方法时 请注意释放 ; 不用此对象时，请 调用 移除当前线程操作日志对象
     */
    public static void step(String step) {
        LogInfo data = getCurrent();
        if (data.getContent() == null) {
            data.setContent("");
        }
        data.setContent(data.getContent() + step + "\n");
        setCurrent(data);
    }
}
