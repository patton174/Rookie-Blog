package com.lx.blog.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 分布式唯一ID生成工具 (基于Snowflake算法优化版)
 * <p>
 * 特性：
 * 1. 支持分布式环境下的唯一ID生成
 * 2. 包含 UUID 格式化与签名工具
 * 3. 线程安全，高性能
 * </p>
 *
 * @author LX
 * @date 2025/12/03
 * @description 分布式唯一ID生成工具
 */
public class UUIDUtils {

    private static final SnowflakeIdGenerator GENERATOR = new SnowflakeIdGenerator();

    /**
     * 获取分布式唯一ID (基于Snowflake算法)
     *
     * @return String类型的数字ID (防止前端精度丢失)
     */
    public static String getId() {
        return String.valueOf(GENERATOR.nextId());
    }

    /**
     * Snowflake 算法核心实现
     * 保持原版位分配策略，确保ID兼容性
     */
    private static class SnowflakeIdGenerator {
        private static final Logger logger = LoggerFactory.getLogger(SnowflakeIdGenerator.class);

        // ============================== Constants ==============================
        /** 起始时间戳 (2010-11-04) */
        private static final long EPOCH = 1288834974657L;

        /** 各部分位数 */
        private static final long WORKER_ID_BITS = 5L;
        private static final long DATACENTER_ID_BITS = 5L;
        private static final long SEQUENCE_BITS = 12L;

        /** 最大值 */
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

        /** 位移量 */
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

        /** 序列号掩码 (4095) */
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

        // ============================== Fields ==============================
        private final long workerId;
        private final long datacenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        public SnowflakeIdGenerator() {
            this.datacenterId = resolveDatacenterId();
            this.workerId = resolveWorkerId(this.datacenterId);
            logger.info("Snowflake initialized. WorkerId: {}, DatacenterId: {}", workerId, datacenterId);
        }

        public synchronized long nextId() {
            long currentTimestamp = System.currentTimeMillis();

            // 时钟回拨检查
            if (currentTimestamp < lastTimestamp) {
                handleClockBackwards(lastTimestamp - currentTimestamp);
                currentTimestamp = System.currentTimeMillis(); // Retry after wait
            }

            // 同一毫秒内序列自增
            if (lastTimestamp == currentTimestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                // 序列溢出，等待下一毫秒
                if (sequence == 0) {
                    currentTimestamp = waitNextMillis(lastTimestamp);
                }
            } else {
                // 不同毫秒，序列号重置 (使用随机起步防止低频请求规律性)
                sequence = ThreadLocalRandom.current().nextLong(0, 10);
            }

            lastTimestamp = currentTimestamp;

            return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                    | (datacenterId << DATACENTER_ID_SHIFT)
                    | (workerId << WORKER_ID_SHIFT)
                    | sequence;
        }

        private long waitNextMillis(long lastTimestamp) {
            long timestamp = System.currentTimeMillis();
            while (timestamp <= lastTimestamp) {
                timestamp = System.currentTimeMillis();
            }
            return timestamp;
        }

        private void handleClockBackwards(long offset) {
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during clock backward wait", e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d ms", offset));
            }
        }

        // ============================== ID Resolution ==============================
        private long resolveDatacenterId() {
            try {
                InetAddress ip = InetAddress.getLocalHost();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if (network == null) return 1L;

                byte[] mac = network.getHardwareAddress();
                if (mac == null) return 1L;

                long id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                return id % (MAX_DATACENTER_ID + 1);
            } catch (Exception e) {
                logger.warn("Failed to determine datacenterId from MAC, using default 1. Cause: {}", e.getMessage());
                return 1L;
            }
        }

        private long resolveWorkerId(long datacenterId) {
            StringBuilder mpId = new StringBuilder();
            mpId.append(datacenterId);
            String name = ManagementFactory.getRuntimeMXBean().getName();
            if (name != null && !name.isEmpty()) {
                mpId.append(name.split("@")[0]); // PID
            }
            return (mpId.toString().hashCode() & 0xffff) % (MAX_WORKER_ID + 1);
        }
    }
}
