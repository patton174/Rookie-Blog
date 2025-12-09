package com.lx.blog.common.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lx.blog.common.model.IpDomain;
import com.lx.blog.common.utils.HttpUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author LX
 * @date 2025/12/03
 * @description IP地址解析接口
 */
@Slf4j
@Component
public class IpParseApi {

    public IpDomain parseIpAddress(String ipAddress) throws IOException {
        if (isPrivateOrLoopback(ipAddress)) {
            return new IpDomain(null, null, null, "局域网/本机");
        }
        String reqUrl = "https://ip9.com.cn/get?ip=" + ipAddress;
        String body = HttpUtils.doGet(reqUrl);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(body);
        String status = text(node, "ret");
        if (!"200".equalsIgnoreCase(status)) {
            return new IpDomain(null, null, null, "未知");
        }
        JsonNode data = node.get("data");
        String country = text(data, "country");
        String regionName = text(data, "prov");
        String city = text(data, "city");
        String isp = text(data, "isp");
        String region = joinRegion(country, regionName, city);
        return new IpDomain(null, null, isp, region);
    }

    private boolean isPrivateOrLoopback(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }
        if ("::1".equals(ip)) {
            return true;
        }
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            try {
                int p0 = Integer.parseInt(parts[0]);
                int p1 = Integer.parseInt(parts[1]);
                if (p0 == 10) {
                    return true;
                }
                if (p0 == 127) {
                    return true;
                }
                if (p0 == 192 && p1 == 168) {
                    return true;
                }
                if (p0 == 172 && p1 >= 16 && p1 <= 31) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return false;
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? null : v.asText();
    }

    private String joinRegion(String country, String region, String city) {
        StringBuilder sb = new StringBuilder();
        if (country != null && !country.isEmpty()) {
            sb.append(country);
        }
        if (region != null && !region.isEmpty()) {
            sb.append(" ").append(region);
        }
        if (city != null && !city.isEmpty()) {
            sb.append(" ").append(city);
        }
        String r = sb.toString();
        return r.isEmpty() ? "未知" : r;
    }
}
