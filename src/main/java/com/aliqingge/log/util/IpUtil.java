package com.aliqingge.log.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhangzhongqing
 * @date 2020/9/27 14:56
 */
public class IpUtil {

    private static final Log log = LogFactory.getLog(IpUtil.class);

    private static final String COMMA_REG = ",";

    private static final int LENGTH = 15;

    /**
     * 获取用户IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String[] ipHeaders = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        String[] localhostIp = {"127.0.0.1", "0:0:0:0:0:0:0:1"};
        String ip = request.getRemoteAddr();
        for (String header : ipHeaders) {
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
            ip = request.getHeader(header);
        }
        for (String local : localhostIp) {
            if (ip != null && ip.length() > 0 && ip.equals(local)) {
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    log.warn("Get host ip exception , UnknownHostException : {}", e);
                }
                break;
            }
        }
        if (ip != null && ip.length() > LENGTH && ip.contains(COMMA_REG)) {
            ip = ip.substring(0, ip.indexOf(','));
        }

        return ip;
    }
}
