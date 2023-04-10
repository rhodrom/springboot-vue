package com.hsy.springboot.mapper;

import com.hsy.springboot.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hsy
 * @since 2023-04-05
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("select id from sys_role where flag=#{flag}")
    Integer selectByFlag(@Param("flag") String flag);
}
