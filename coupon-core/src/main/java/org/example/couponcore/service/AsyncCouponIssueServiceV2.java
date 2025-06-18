package org.example.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.couponcore.component.DistributeLockExecutor;
import org.example.couponcore.exception.CouponIssueException;
import org.example.couponcore.exception.ErrorCode;
import org.example.couponcore.repository.redis.RedisRepository;
import org.example.couponcore.repository.redis.dto.CouponIssueRequest;
import org.example.couponcore.repository.redis.dto.CouponRedisEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {
    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;

    //RPS 결정하는 중요한 요인
    @Transactional
    public void issue(long couponId,long userId){

        //CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        CouponRedisEntity coupon = couponCacheService.getCouponLocalCache(couponId);
        coupon.checkIssuableCoupon();
        issueRequest(couponId, userId,coupon.totalQuantity());
    }
    private void issueRequest(long couponId, long userId,Integer totalIssueQuantity){
        if(totalIssueQuantity ==null){
            redisRepository.issueRequest(couponId,userId,Integer.MAX_VALUE);
            return;
        }
        redisRepository.issueRequest(couponId,userId,totalIssueQuantity);
    }

}
