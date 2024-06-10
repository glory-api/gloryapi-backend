package com.hry.gloryapi.backend.service;

import com.hry.gloryapi.backend.model.entity.DailyCheckInEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author lenovo
* @description 针对表【daily_check_in(每日签到表)】的数据库操作Service
* @createDate 2024-06-03 16:28:36
*/
public interface DailyCheckInService extends IService<DailyCheckInEntity> {
    List<DailyCheckInEntity> listByUserIdAndTime(String userId, LocalDateTime beginTime, LocalDateTime endTime);
}
