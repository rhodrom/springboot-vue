package com.hsy.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hsy.springboot.common.Constants;
import com.hsy.springboot.common.Result;
import com.hsy.springboot.config.AuthAccess;
import com.hsy.springboot.entity.Files;
import com.hsy.springboot.entity.User;
import com.hsy.springboot.mapper.FileMapper;
import com.hsy.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Resource
    private IUserService userService;
    @Resource
    private FileMapper fileMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/example")
    public Result get(){
        Map<String,Object> map= new HashMap<>();
        map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y", CollUtil.newArrayList(150, 230, 224, 218, 135, 147, 260));
        return Result.success(map);
    }

    @GetMapping("/members")
    public Result members(){
        List<User> list = userService.list();
        int quarter1=0; // 第一季度
        int quarter2=0; // 第二季度
        int quarter3=0; // 第三季度
        int quarter4=0; // 第四季度
        for (User user: list) {
            Date createTime = user.getCreateTime();
            Quarter quarter = DateUtil.quarterEnum(createTime);  //获取时间所属季度
            switch (quarter){
                case Q1: quarter1+=1;break;
                case Q2: quarter2+=1;break;
                case Q3: quarter3+=1;break;
                case Q4: quarter4+=1;break;
                default:break;
            }
        }
        return Result.success(CollUtil.newArrayList(quarter1,quarter2,quarter3,quarter4));
    }

    @AuthAccess  //自定义注解，放行该接口
    @GetMapping("/file/front/all")  //查询所有文件返回给前端
    public Result findAll(){
        //1.从缓存获取数据
        String jsonStr = stringRedisTemplate.opsForValue().get(Constants.FILES_KEY);
        List<Files> files;
        if(StrUtil.isBlank(jsonStr)){  //2.取出来的json是空的
            files=fileMapper.selectList(null); //3. 从数据库中取出数据
            //4. 再去缓存到redis
            stringRedisTemplate.opsForValue().set(Constants.FILES_KEY,JSONUtil.toJsonStr(files));
        }else {
            //5. 如果缓存中有，从redis缓存中获得数据
            files= JSONUtil.toBean(jsonStr, new TypeReference<List<Files>>() {
            },true);  //将jsonStr转为Files对象
        }
        return Result.success(files);
    }
}
