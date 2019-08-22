package com.wf.app.wfapp.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RedisLockHelper {


    private static final String DELIMITER = "|";

    /**
     * 如果要求比较高可以通过注入的方式分配
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(20);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取锁（存在死锁风险）
     *
     * @param lockKey lockKey
     * @param value   value
     * @param time    超时时间
     * @param unit    过期单位
     * @return true or false
     */
    public boolean tryLock(final String lockKey, final String value, final long time, final TimeUnit unit) {
        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(lockKey.getBytes(), value.getBytes(), Expiration.from(time, unit), RedisStringCommands.SetOption.SET_IF_ABSENT));
    }

    /**
     * 获取锁
     *
     * @param lockKey lockKey
     * @param uuid    UUID
     * @param timeout 超时时间
     * @param unit    过期单位
     * @return true or false
     */
    public boolean lock(String lockKey, final String uuid, long timeout, final TimeUnit unit) {
        final long milliseconds = Expiration.from(timeout, unit).getExpirationTimeInMilliseconds();
        boolean success = setNX(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
        if (success) {
            stringRedisTemplate.expire(lockKey, timeout, unit);
        } else {
            String value = get(lockKey);
            if (!StringUtils.isEmpty(value)) {
                String[] array = value.split(Pattern.quote(DELIMITER));
                long currentValue = Long.parseLong(array[0]) + 1;
                // 判断锁的值是否已经过期，过期则重新设置并获取
                if (currentValue <= System.currentTimeMillis()) {
                    // 设置锁并返回旧值
                    String oldValue = getSet(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
                    log.info("lock key:[{}],uuid:[{}],currentValue:[{}]", lockKey, uuid, currentValue);
                    if (!StringUtils.isEmpty(oldValue)) {
                        array = oldValue.split(Pattern.quote(DELIMITER));
                        long hisValue = Long.parseLong(array[0]) + 1;
                        // 比较锁的时间，如果不一致则可能是其他线程已经修改了锁值
                        if (hisValue == currentValue) {
                            log.info("lock key:[{}],uuid:[{}],currentValue:[{}],oldValue:[{}]", lockKey, uuid, currentValue, oldValue);
                            success = true;
                        }
                    }
                }
            }
        }
        return success;
    }


    /**
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     */
    public void unlock(String lockKey, String value) {
        unlock(lockKey, value, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟unlock
     *
     * @param lockKey   key
     * @param uuid      client(最好是唯一键的)
     * @param delayTime 延迟时间
     * @param unit      时间单位
     */
    public void unlock(final String lockKey, final String uuid, long delayTime, TimeUnit unit) {
        if (StringUtils.isEmpty(lockKey)) {
            return;
        }
        if (delayTime <= 0) {
            doUnlock(lockKey, uuid);
        } else {
            EXECUTOR_SERVICE.schedule(() -> doUnlock(lockKey, uuid), delayTime, unit);
        }
    }

    /**
     * @param lockKey key
     * @param uuid    client(最好是唯一键的)
     */
    public void doUnlock(final String lockKey, final String uuid) {
        String val = get(lockKey);
        if (StringUtils.isEmpty(val)) {
            return;
        }
        final String[] values = val.split(Pattern.quote(DELIMITER));
        if (values.length <= 0) {
            return;
        }
        if (uuid.equals(values[1])) {
            boolean result = stringRedisTemplate.delete(lockKey);
            log.info("lock key:[{}],uuid:[{}],values:[{}],doUnlock result:[{}]", lockKey, uuid, values[1], result);
        }
    }

    /**
     * 设置redis锁
     *
     * @param key
     * @param value
     * @return
     */
    private boolean setNX(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 设置redis锁
     *
     * @param key
     * @param value
     * @return
     */
    private String getSet(String key, String value) {
        Object object = stringRedisTemplate.opsForValue().getAndSet(key, value);
        return object != null ? (String) object : null;
    }

    /**
     * 获取redis值
     *
     * @param key
     * @return
     */
    private String get(String key) {
        Object object = stringRedisTemplate.opsForValue().get(key);
        return object != null ? (String) object : null;
    }
}
