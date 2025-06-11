package org.example.couponcore.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.couponcore.exception.CouponIssueException;
import org.example.couponcore.exception.ErrorCode;
import org.example.couponcore.model.Coupon;
import org.example.couponcore.model.CouponIssue;
import org.example.couponcore.repository.mysql.CouponIssueJpaRepository;
import org.example.couponcore.repository.mysql.CouponIssueRepository;
import org.example.couponcore.repository.mysql.CouponJpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponIssueRepository couponIssueRepository;
    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;

    @Transactional
    public void issue(long couponId,long userId){
        Coupon coupon = findCouponWithLock(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }


    @Transactional(readOnly=true)
    public Coupon findCoupon(long couponId){
        return couponJpaRepository.findById(couponId).orElseThrow(()->{
            throw new CouponIssueException(ErrorCode.COUPON_NOT_EXIST,
                    "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId));
        });
    }

    @Transactional
    public Coupon findCouponWithLock(long couponId){
        return couponJpaRepository.findCouponWithLock(couponId).orElseThrow(()->{
            throw new CouponIssueException(ErrorCode.COUPON_NOT_EXIST,
                    "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId));
        });
    }
    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssuance(couponId,userId);
        CouponIssue issue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(issue);
    }

    private void checkAlreadyIssuance(long couponId, long userId){
        CouponIssue issue =couponIssueRepository.findFirstCouponIssue(couponId,userId);
        if(issue !=null){
            throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE
                    ,"이미 발급된 쿠폰입니다. user_id : %s, couponId : $s".formatted(userId,couponId));
        }
    }

}
