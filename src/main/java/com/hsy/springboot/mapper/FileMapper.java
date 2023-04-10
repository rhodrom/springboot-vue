package com.hsy.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsy.springboot.entity.Files;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<Files> {
}
