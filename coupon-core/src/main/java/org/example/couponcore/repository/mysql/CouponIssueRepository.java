package org.example.couponcore.repository.mysql;

import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.couponcore.model.CouponIssue;
import org.example.couponcore.model.QCouponIssue;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {
    private final JPQLQueryFactory queryFactory;

    public CouponIssue findFirstCouponIssue(long couponId, long userId){
        return queryFactory.selectFrom(QCouponIssue.couponIssue)
                .where(QCouponIssue.couponIssue.couponId.eq(couponId))
                .where(QCouponIssue.couponIssue.userId.eq(userId))
                .fetchFirst();
    }
}
