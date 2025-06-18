package org.example.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.example.couponcore.model.Coupon;
import org.example.couponcore.repository.redis.dto.CouponRedisEntity;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CouponCacheService {
    private final CouponIssueService couponIssueService;

    @Cacheable(cacheNames = "coupon")
    public CouponRedisEntity getCouponCache(long couponId){
        Coupon coupon = couponIssueService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

    @Cacheable(cacheNames = "coupon",cacheManager = "localCacheManager")
    public CouponRedisEntity getCouponLocalCache(long couponId){
         return proxy().getCouponCache(couponId);
    }

    private CouponCacheService proxy(){
        return ((CouponCacheService) AopContext.currentProxy());
    }
    //캐시 업데이트
    @CachePut(cacheNames = "coupon")
    public CouponRedisEntity putCouponCache(long couponId){
        return getCouponCache(couponId);
    }
    @CachePut(cacheNames = "coupon",cacheManager = "localCacheManager")
    public CouponRedisEntity putCouponLocalCache(long couponId){
        return getCouponLocalCache(couponId);
    }
}
