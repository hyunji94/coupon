package org.example.couponconsumer.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.couponconsumer.TestConfig;
import org.example.couponcore.repository.redis.RedisRepository;
import org.example.couponcore.service.CouponIssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

    @Autowired
    CouponIssueListener sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisRepository redisRepository;

    @MockBean
    CouponIssueService couponIssueService;

    @BeforeEach
    void check(){
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리대상이 없다면 발급을 하지 않는다.")
    void issue_1() throws JsonProcessingException {
        sut.issue();
        verify(couponIssueService,never()).issue(anyLong(),anyLong());

    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리대상이 있다면 발급한다.")
    void issue_2() throws JsonProcessingException {
        long couponId = 1;
        long userId =1;
        int totalQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId,userId,totalQuantity);
        sut.issue();
        verify(couponIssueService,times(1)).issue(anyLong(),anyLong());

    }

    @Test
    @DisplayName("쿠폰 발급 요청 순서에 맞게 처리 한다.")
    void issue_3() throws JsonProcessingException {
        long couponId = 1;
        long userId1 =1;
        long userId2 =2;
        long userId3 =3;
        int totalQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId,userId1,totalQuantity);
        redisRepository.issueRequest(couponId,userId2,totalQuantity);
        redisRepository.issueRequest(couponId,userId3,totalQuantity);
        sut.issue();
        InOrder inOrder = inOrder(couponIssueService);
        inOrder.verify(couponIssueService,times(1)).issue(couponId,userId1);
        inOrder.verify(couponIssueService,times(1)).issue(couponId,userId2);
        inOrder.verify(couponIssueService,times(1)).issue(couponId,userId3);

    }
}