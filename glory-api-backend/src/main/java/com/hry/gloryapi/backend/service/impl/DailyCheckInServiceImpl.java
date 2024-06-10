package com.hry.gloryapi.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hry.gloryapi.backend.model.entity.DailyCheckInEntity;
import com.hry.gloryapi.backend.service.DailyCheckInService;
import com.hry.gloryapi.backend.mapper.DailyCheckInMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author lenovo
* @description 针对表【daily_check_in(每日签到表)】的数据库操作Service实现
* @createDate 2024-06-03 16:28:36
*/
@Service
public class DailyCheckInServiceImpl extends ServiceImpl<DailyCheckInMapper, DailyCheckInEntity>
    implements DailyCheckInService {

    @Override
    public List<DailyCheckInEntity> listByUserIdAndTime(String userId, LocalDateTime beginTime, LocalDateTime endTime) {
        LambdaQueryWrapper<DailyCheckInEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyCheckInEntity::getUserId,userId)
            .ge(DailyCheckInEntity::getCreateTime,beginTime)
            .lt(DailyCheckInEntity::getCreateTime,endTime);

        return list(wrapper);
    }
}




