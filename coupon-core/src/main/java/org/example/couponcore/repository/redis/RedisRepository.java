package org.example.couponcore.repository.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.couponcore.exception.CouponIssueException;
import org.example.couponcore.exception.ErrorCode;
import org.example.couponcore.repository.redis.dto.CouponIssueRequest;
import org.example.couponcore.repository.redis.dto.CouponIssueRequestCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Repository
public class RedisRepository {
    private final RedisTemplate<String,String> redisTemplate;
    private final RedisScript<String> issueScript = issueRequestScript();
    private final String issueRequestQueueKey = getIssueRequestQueueKey();

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public Long lSize(String key){
        return redisTemplate.opsForList().size(key);
    }
    public String lIndex(String key,long index){
        return redisTemplate.opsForList().index(key,index);
    }
    public String lPop(String key){
        return redisTemplate.opsForList().leftPop(key);
    }

    //Redis Script
    //싱글 스레드인 Redis 메소드를 각각 나누어서 처리해서 롹을 걸지 않고 한번에 스크립트로 처리
    public void issueRequest(long couponId, long userId, int totalIssueQuantity){
        String issueRequestKey = getIssueRequestKey(couponId);
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(couponId,userId);
        try{
            String code = redisTemplate.execute(
                    issueScript,
                    List.of(issueRequestKey,issueRequestQueueKey),
                    String.valueOf(userId),
                    String.valueOf(totalIssueQuantity),
                    objectMapper.writeValueAsString(couponIssueRequest)
            );
            CouponIssueRequestCode.checkRequestResult(CouponIssueRequestCode.find(code));
        } catch (JsonProcessingException e){
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST,"input:%s".formatted(couponIssueRequest));
        }
    }

    private RedisScript<String> issueRequestScript(){
        String script = """
                if redis.call('SISMEMBER',KEYS[1],ARGV[1]) == 1 then
                    return '2'
                end
             
                if tonumber(ARGV[2]) > redis.call('SCARD',KEYS[1]) then
                    redis.call('SADD',KEYS[1], ARGV[1])
                    redis.call('RPUSH',KEYS[2], ARGV[3])
                    return '1'
                end
                
                return '3'
                """;
        return RedisScript.of(script,String.class);
    }
}
