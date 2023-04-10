package com.hsy.springboot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsy.springboot.entity.Course;
import com.hsy.springboot.mapper.CourseMapper;
import com.hsy.springboot.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hsy
 * @since 2023-04-07
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CourseMapper courseMapper;

    @Override
    public Page<Course> findPage(Page<Course> page, String name) {
        return courseMapper.findPage(page,name);
    }

    @Override
    public void studentCourse(Integer studentId, Integer courseId) {
        courseMapper.delete(studentId,courseId);
        courseMapper.studentCourse(studentId,courseId);
    }
}
