package com.aliqingge.log.extractor;

import com.aliqingge.log.bean.LogInfo;
import com.aliqingge.log.util.IpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 提取器
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:44
 */
public class LogExtractor {

    private static final Log log = LogFactory.getLog(LogExtractor.class);

    private static final String AND_REG = "&";
    private static final String EQUALS_REG = "=";


    /**
     * 获取HttpServletRequest对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取HttpServletResponse对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }


    /**
     * 获取请求参数内容
     *
     * @param parameterNames 参数名称列表
     * @param args           参数列表
     */
    public static Object getArgs(String[] parameterNames, Object[] args) {
        Object target;
        if (args.length == 1) {
            target = args[0];
        } else {
            target = args;
        }
        if (target == null) {
            return null;
        }
        HttpServletRequest request = getRequest();
        if (request != null && request.getContentType() != null
                && request.getContentType().length() > 0) {
            String contentType = request.getContentType();
            if (MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
                return xmlArgs(target);
            }
            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType) || MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
                return target;
            }
        }
        return appletArgs(parameterNames, args);
    }

    /**
     * 获取程序执行结果内容
     */
    public static Object getResult(Object resp) {
        HttpServletResponse response = getResponse();
        if (response != null && MediaType.APPLICATION_XML_VALUE.equals(response.getContentType())) {
            return xmlArgs(resp);
        } else {
            return resp;
        }
    }

    /**
     * 获取程序参数
     *
     * @param parameterNames 参数名
     * @param args           参数值
     */
    public static Object appletArgs(String[] parameterNames, Object[] args) {
        if (parameterNames == null || parameterNames.length == 0 || args == null || args.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameterNames.length; i++) {
            sb.append(parameterNames[i]).append(EQUALS_REG).append(args[i].toString()).append(AND_REG);
        }
        if (sb.lastIndexOf(AND_REG) != -1) {
            sb.deleteCharAt(sb.lastIndexOf(AND_REG));
        }
        return sb.toString();
    }

    /**
     * 解析XML 数据
     */
    public static Object xmlArgs(Object pointArgs) {
        try {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = JAXBContext.newInstance(pointArgs.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(pointArgs, writer);
            return writer.toString().replace("standalone=\"yes\"", "");
        } catch (JAXBException e) {
            log.warn("parse xml data exception : {}", e.getLinkedException());
        }
        return pointArgs;
    }


    /**
     * 获取 HttpServletRequest 对象信息
     */
    public static void logHttpRequest(LogInfo data, String[] headers) {
        HttpServletRequest request = LogExtractor.getRequest();
        if (request != null) {
            data.setHost(request.getLocalAddr());
            data.setPort(request.getLocalPort());
            data.setClientIp(IpUtil.getIpAddress(request));
            data.setReqUrl(request.getRequestURL().toString());
            data.setHttpMethod(request.getMethod());
            Map<String, String> headersMap = new HashMap<>();
            for (String header : headers) {
                String value = request.getHeader(header);
                if (value != null && value.length() > 0) {
                    headersMap.put(header, request.getHeader(header));
                }
            }
            data.setHeaders(headersMap);
        }
    }


}
