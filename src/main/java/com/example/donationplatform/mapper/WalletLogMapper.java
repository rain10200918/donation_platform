package com.example.donationplatform.mapper;

import com.example.donationplatform.entity.WalletLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WalletLogMapper {
    void insert(WalletLog log);
}
