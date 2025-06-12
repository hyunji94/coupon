package org.example.couponapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.couponapi.controller.dto.CouponIssueRequestDto;
import org.example.couponapi.controller.dto.CouponIssueResponseDto;
import org.example.couponapi.service.CouponIssueRequestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {
    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto body){
        couponIssueRequestService.issueRequestV1(body);
        return new CouponIssueResponseDto(true,null);
    }
    @PostMapping("/issue-async")
    public CouponIssueResponseDto issueAsync(@RequestBody CouponIssueRequestDto requestDto){
        couponIssueRequestService.asyncIssueRequestV1(requestDto);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/issue-async2")
    public CouponIssueResponseDto issueAsync2(@RequestBody CouponIssueRequestDto requestDto){
        couponIssueRequestService.asyncIssueRequestV2(requestDto);
        return new CouponIssueResponseDto(true, null);
    }

}
