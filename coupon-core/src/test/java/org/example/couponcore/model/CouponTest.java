//package org.example.couponcore.model;
//
//import org.example.couponcore.exception.CouponIssueException;
//import org.example.couponcore.exception.ErrorCode;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//
//class CouponTest {
//    @Test
//    @DisplayName("발급수량이 남았다면 true")
//    void availableIssueQuantity_1(){
//        Coupon coupon = Coupon.builder()
//                .totalQuantity(100)
//                .issuedQuantity(99)
//                .build();
//
//        boolean result = coupon.availableIssueQuantity();
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    @DisplayName("발급수량이 소진되면 false")
//    void availableIssueQuantity_2(){
//        Coupon coupon = Coupon.builder()
//                .totalQuantity(100)
//                .issuedQuantity(101)
//                .build();
//
//        boolean result = coupon.availableIssueQuantity();
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("발급기한이 시작되지 않았다면, false 반환")
//    void availableIssueDate_1(){
//        Coupon coupon = Coupon.builder()
//                .dateIssueStart(LocalDateTime.now().plusDays(1))
//                .dateIssueEnd(LocalDateTime.now().plusDays(2))
//                .build();
//
//        boolean result = coupon.availableIssueDate();
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("발급기한에 해당되면, true 반환")
//    void availableIssueDate_2(){
//        Coupon coupon = Coupon.builder()
//                .dateIssueStart(LocalDateTime.now().minusDays(1))
//                .dateIssueEnd(LocalDateTime.now().plusDays(2))
//                .build();
//
//        boolean result = coupon.availableIssueDate();
//        Assertions.assertTrue(result);
//    }
//    @Test
//    @DisplayName("발급조건이 모두 유효하면, true 반환")
//    void Issue_1(){
//        Coupon coupon = Coupon.builder()
//                .totalQuantity(100)
//                .issuedQuantity(99)
//                .dateIssueStart(LocalDateTime.now().minusDays(1))
//                .dateIssueEnd(LocalDateTime.now().plusDays(2))
//                .build();
//
//        coupon.issue();
//        Assertions.assertEquals(coupon.getIssuedQuantity(),100);
//    }
//    @Test
//    @DisplayName("발급 수량을 초과하면, 예외 반환")
//    void Issue_2(){
//        Coupon coupon = Coupon.builder()
//                .totalQuantity(100)
//                .issuedQuantity(120)
//                .build();
//        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class,coupon::issue);
//        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
//    }
//}