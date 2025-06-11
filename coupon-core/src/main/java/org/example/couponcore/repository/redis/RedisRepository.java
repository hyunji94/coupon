package org.example.couponcore.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisRepository {
    private final RedisTemplate<String,String> redisTemplate;

    //sortedSet
    public Boolean zAdd(String key,String value, double score){
        return redisTemplate.opsForZSet().addIfAbsent(key,value,score);
    }

    //set
    public Long sAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }
    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    //특정 값이 Set에 포함되어 있는지 확인
    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    //List의 오른쪽 끝에 값을 추가.
    public Long rPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
}
