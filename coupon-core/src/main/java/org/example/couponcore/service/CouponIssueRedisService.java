package org.example.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.example.couponcore.exception.CouponIssueException;
import org.example.couponcore.exception.ErrorCode;
import org.example.couponcore.repository.redis.RedisRepository;
import org.example.couponcore.repository.redis.dto.CouponRedisEntity;
import org.springframework.stereotype.Service;
import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {
    private final RedisRepository redisRepository;

    public void checkCouponIssueQuantity(CouponRedisEntity couponRedisEntity, long userId){
        if(!availableTotalIssueQuantity(couponRedisEntity.totalQuantity(),couponRedisEntity.id())){
                throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                        "발급 가능한 수량을 초과합니다. couponId=%s, userId=%s".formatted(couponRedisEntity.id(),userId));

            }
        if(!availableUserIssueQuantity(couponRedisEntity.id(),userId)){
                throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE,
                        "이미 발급 요청이 처리 되었습니다.. couponId=%s, userId=%s".formatted(couponRedisEntity.id(),userId));
            }
    }

    public boolean availableTotalIssueQuantity(Integer totalQuantity,long couponId){
        if(totalQuantity == null){
            return true;
        }
        String key = getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key); //쿠폰 발급 수량 제어
    }

    //중복 발급
    public boolean availableUserIssueQuantity(long couponId, long userId){
        String key = getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key,String.valueOf(userId)); //중복 발급 요청 제어
    }
}
