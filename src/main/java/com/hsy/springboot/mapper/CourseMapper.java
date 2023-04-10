package com.hsy.springboot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsy.springboot.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hsy
 * @since 2023-04-07
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    Page<Course> findPage(Page<Course> page, @Param("name") String name);

    void studentCourse( @Param("studentId")Integer studentId, @Param("courseId") Integer courseId);

    void delete( @Param("studentId")Integer studentId, @Param("courseId") Integer courseId);
}
