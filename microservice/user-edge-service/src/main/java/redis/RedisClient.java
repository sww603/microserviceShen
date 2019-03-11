package redis;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by sww_6 on 2019/3/7.
 */
@Component
public class RedisClient {

  @Autowired
  private RedisTemplate redisTemplate;

  public <T> T get(String key) {
    return (T) redisTemplate.opsForValue().get(key);
  }

  public void set(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public void set(String key, Object value, int timeout) {
    redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
  }

  public void expire(String key, int timeout) {
    redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
  }
}
