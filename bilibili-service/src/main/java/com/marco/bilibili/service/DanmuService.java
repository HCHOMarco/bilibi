package com.marco.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.marco.bilibili.dao.DanmuDao;
import com.marco.bilibili.domain.Danmu;
import com.mysql.cj.util.StringUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DanmuService {

    private static final String DANMU_KEY = "dm-video-";

    @Autowired
    private DanmuDao danmuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void addDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }

    @Async
    public void asyncAddDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }

    /**
     *先查redis，若没有就查数据库然后将查到的数据放入redis中
     */
    public List<Danmu> getDanmus(Long videoId, String startTime, String endTime) throws Exception {
        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list;
        //如果可以在redis中查到
        if(!StringUtils.isNullOrEmpty(value)){
            //获取所有弹幕
            list = JSONArray.parseArray(value, Danmu.class);
            if(!StringUtils.isNullOrEmpty(startTime) &&
            !StringUtils.isNullOrEmpty(endTime)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<Danmu> childList = new ArrayList<>();
                //获得符合时间的弹幕
                for(Danmu danmu : list){
                    if(danmu.getCreateTime().after(startDate) && danmu.getCreateTime().before(endDate)){
                        childList.add(danmu);
                    }
                }
                list = childList;
            }
        }else{
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            list = danmuDao.getDanmus(params);
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }

    public void addDanmuToRedis(Danmu danmu) {
        String key = "danmu-video-" + danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();
        if(!StringUtil.isNullOrEmpty(value)){
            list = JSONArray.parseArray(value, Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }
}
