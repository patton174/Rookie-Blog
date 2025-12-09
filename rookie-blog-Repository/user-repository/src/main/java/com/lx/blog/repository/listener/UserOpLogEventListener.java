package com.lx.blog.repository.listener;

import com.lx.blog.common.aop.log.OpLogEvent;
import com.lx.blog.common.utils.UUIDUtils;
import com.lx.blog.repository.dao.UserLogDao;
import com.lx.blog.repository.dao.impl.mapper.entity.UserLog;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author LX
 * @date 2025/12/03
 * @description 操作日志事件监听器
 */
@Component
@RequiredArgsConstructor
public class UserOpLogEventListener {

    @NotNull private final UserLogDao userLogDao;

    @EventListener
    public void onOpLog(OpLogEvent event) {
        if (!"user_log".equals(event.getTableName())) return;
        Map<String, Object> p = event.getPayload();
        String _class = (String) p.get("_class");
        UserLog log = UserLog.builder()
                .id(UUIDUtils.getId())
                ._class(_class)
                .action((String) p.get("action"))
                .userId((String) p.get("userId"))
                .ip((String) p.get("ip"))
                .userAgent((String) p.get("user_agent"))
                .loggedAt((LocalDateTime) p.get("logged_at"))
                .build();
        userLogDao.save(log);
    }
}

