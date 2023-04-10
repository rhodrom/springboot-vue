package com.hsy.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsy.springboot.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hsy
 * @since 2023-04-07
 */
public interface ICourseService extends IService<Course> {

    Page<Course> findPage(Page<Course> page, String name);

    void studentCourse(Integer studentId, Integer courseId);
}
