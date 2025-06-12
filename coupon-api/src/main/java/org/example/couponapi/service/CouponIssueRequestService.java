package org.example.couponapi.service;

import lombok.RequiredArgsConstructor;
import org.example.couponapi.controller.dto.CouponIssueRequestDto;
import org.example.couponcore.component.DistributeLockExecutor;
import org.example.couponcore.service.AsyncCouponIssueServiceV1;
import org.example.couponcore.service.AsyncCouponIssueServiceV2;
import org.example.couponcore.service.CouponIssueService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    private final AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public void issueRequestV1(CouponIssueRequestDto requestDto){
//        distributeLockExecutor.execute("lock_" +requestDto.couponId(),10000,100000,
//                ()->{
//            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
//        });

        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId:%s, userId:%s".formatted(requestDto.couponId(),requestDto.userId()));

    }

    public void asyncIssueRequestV1(CouponIssueRequestDto requestDto){
        asyncCouponIssueServiceV1.issue(requestDto.couponId(),requestDto.userId());
    }

    public void asyncIssueRequestV2(CouponIssueRequestDto requestDto){
        asyncCouponIssueServiceV2.issue(requestDto.couponId(),requestDto.userId());
    }
}
