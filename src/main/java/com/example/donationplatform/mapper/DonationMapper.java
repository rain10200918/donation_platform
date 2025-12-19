package com.example.donationplatform.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DonationMapper {
    Integer countSuccessDonation(Long userId, Long projectId);
}
