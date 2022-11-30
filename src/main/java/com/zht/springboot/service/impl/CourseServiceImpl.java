package com.zht.springboot.service.impl;

import com.zht.springboot.entity.Course;
import com.zht.springboot.mapper.CourseMapper;
import com.zht.springboot.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tao
 * @since 2022-11-22
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

}
