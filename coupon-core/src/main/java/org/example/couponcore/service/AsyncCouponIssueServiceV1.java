package org.example.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.couponcore.component.DistributeLockExecutor;
import org.example.couponcore.exception.CouponIssueException;
import org.example.couponcore.exception.ErrorCode;
import org.example.couponcore.model.Coupon;
import org.example.couponcore.repository.redis.RedisRepository;
import org.example.couponcore.repository.redis.dto.CouponIssueRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {
    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;

    private final DistributeLockExecutor distributeLockExecutor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void issue(long couponId,long userId){
        Coupon coupon = couponIssueService.findCoupon(couponId);
        if(!coupon.availableIssueDate()){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 일자가 아닙니다.couponId=%s, issueStart=%s issueEnd=%s"
                            .formatted(couponId,coupon.getDateIssueStart(),coupon.getDateIssueEnd()));
        }
        //redis 자체에서는 싱글스레드로 처리해서 동시성 문제 없음
        //다만 redis 메소드를 각 메소드에서 호출(redis를 분리해서 사용) -> 동시성 문제 발생 야기
       //롹 추가
        distributeLockExecutor.execute("lock_%s".formatted(couponId),3000,3000,()->{
            if(!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(),couponId)){
                throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                        "발급 가능한 수량을 초과합니다. couponId=%s, userId=%s".formatted(couponId,userId));

            }
            if(!couponIssueRedisService.availableUserIssueQuantity(couponId,userId)){
                throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE,
                        "이미 발급 요청이 처리 되었습니다.. couponId=%s, userId=%s".formatted(couponId,userId));
            }
            issueRequest(couponId, userId);
        });

    }
    private void issueRequest(long couponId, Long userId){
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId,userId);
        try{
            String value= objectMapper.writeValueAsString(issueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId),String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(),value);
        }catch (JsonProcessingException e){
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST,"input:%s".formatted(issueRequest));
        }



    }

}
