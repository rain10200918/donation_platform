package com.example.donationplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.donationplatform.dto.StatusDTO;
import com.example.donationplatform.dto.UsersRegisterDTO;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.entity.WalletLog;
import com.example.donationplatform.exception.ServiceException;
import com.example.donationplatform.mapper.UsersMapper;
import com.example.donationplatform.mapper.WalletLogMapper;
import com.example.donationplatform.service.UsersService;
import com.example.donationplatform.util.JwtUtil;
import com.example.donationplatform.util.MD5Util;
import com.example.donationplatform.util.ValidatorUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.donationplatform.constant.Constant.PERSON_TYPE;
import static com.example.donationplatform.constant.Constant.USER_STATUS_NORMAL;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private WalletLogMapper walletLogMapper;
    @Autowired
    @Lazy
    private UsersServiceImpl self;
    @Override
    public String selectByMobile(String mobile,String  password) {
        Users users = usersMapper.selectByMobile(mobile);
        if (!password.equals(users.getPassword())){
           throw new RuntimeException("密码错误");
        }
        if (!users.getStatus().equals("1")){
            throw new ServiceException("403","用户被禁用");
        }

        return JwtUtil.generateToken(users.getId(), users.getNickName());
    }

    @Override
    public Users selectById(long userId) {
        return usersMapper.selectById(userId);
    }

    @Override
    public void updateUserLoginInfo(String mobile) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        Users user = new Users();
        user.setLastLoginIp(ip);
        user.setLastLoginAt(LocalDateTime.now());
        user.setMobile( mobile);
        user.setUpdateTime(LocalDateTime.now());
        usersMapper.updateUserLoginInfo( user);
    }

    @Override
    public void register(UsersRegisterDTO usersRegisterDTO) {
        if (!ValidatorUtils.isValidMobile(usersRegisterDTO.getMobile())){
            throw new IllegalArgumentException("手机号格式不正确");
        }
        if (!ValidatorUtils.isValidEmail(usersRegisterDTO.getEmail())){
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        Users isExist = usersMapper.selectByMobile(usersRegisterDTO.getMobile());
        if (isExist != null){
            throw new IllegalArgumentException("手机号已存在");
        }
        Users emailExist = usersMapper.selectByEmail(usersRegisterDTO.getEmail());
        if (emailExist != null){
            throw new IllegalArgumentException("邮箱已存在");
        }
        String password = MD5Util.md5(usersRegisterDTO.getPassword());
        Users users = new Users();
        BeanUtil.copyProperties(usersRegisterDTO,users);
        users.setPassword(password);
        users.setStatus(USER_STATUS_NORMAL);
        users.setUserType(PERSON_TYPE);
        users.setCreateTime(LocalDateTime.now());
        usersMapper.registerUser(users);
    }

    @Override
    public Users getUserById(Long id) {
        Users users = usersMapper.getUserById(id);
        return users;
    }

    @Override
    public PageInfo<Users> getUserList(Integer pageNum, Integer pageSize, String nickName, String status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Users> userList = usersMapper.getUserList(pageNum, pageSize, nickName, status);
        return new PageInfo<>(userList);
    }

    @Override
    public void updateUserStatus(StatusDTO dto) {
        usersMapper.updateUserStatus(dto);
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        synchronized (( "user_wallet_" + userId ).intern()) {
            self.updateBalanceWithTransaction(userId, amount);
        }
    }
   @Transactional
    public void updateBalanceWithTransaction(Long userId, BigDecimal amount) {
       // 1. 执行原子更新（数据库自增）
       usersMapper.updateById(amount, userId);

       // 2. 重新查询用户信息，获取数据库计算后的真实余额
       Users user = usersMapper.getUserById(userId);
       if (user == null) throw new RuntimeException("用户不存在");

       // 3. 记录流水（使用查出来的 user.getBalance() 确保账单准确）
       WalletLog log = new WalletLog();
       log.setUserId(userId);
       log.setType(1);
       log.setAmount(amount);
       log.setBalanceAfter(user.getBalance()); // 此时是数据库计算后的准确值
       log.setRemark("账户模拟充值");
       log.setCreateTime(LocalDateTime.now());
       walletLogMapper.insert(log);
    }
}
