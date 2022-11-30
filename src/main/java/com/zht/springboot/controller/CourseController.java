package com.zht.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zht.springboot.common.Result;
import com.zht.springboot.entity.Course;
import com.zht.springboot.entity.User;
import com.zht.springboot.service.ICourseService;
import com.zht.springboot.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tao
 * @since 2022-11-22
 */
@RestController
@RequestMapping("/course")
public class CourseController {


    @Resource
    private ICourseService courseService;

    @Resource
    private IUserService userService;

    @PostMapping
    public Boolean save(@RequestBody Course course) {
        return courseService.saveOrUpdate(course);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return courseService.removeById(id);
    }

    @PostMapping("/del/batch")
    public boolean deleteBody (@RequestBody List<Integer> ids){
        return courseService.removeByIds(ids);
        }

    @GetMapping
    public List<Course> findAll() {
        return courseService.list();
    }

    @GetMapping("/{id}")
    public Course findOne(@PathVariable Integer id) {
        return courseService.getById(id);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        QueryWrapper<Course> queryWrapper= new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<Course> page = courseService.page(new Page<>(pageNum, pageSize),queryWrapper);
        List<Course> records = page.getRecords();
        for (Course record : records){
            User user = userService.getById(record.getTeacherId());
            if(user != null){
                record.setTeacher(user.getNickname());
            }

        }
        return Result.success(records);
    }

}

