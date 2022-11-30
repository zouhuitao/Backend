package com.zht.springboot.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zht.springboot.common.Constants;
import com.zht.springboot.common.Result;
import com.zht.springboot.controller.dto.UserDTO;
import com.zht.springboot.entity.User;
import com.zht.springboot.service.IUserService;
import com.zht.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tao
 * @since 2022-11-02
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("{files.upload.path}")
    private String filesUploadPath;


    @Resource
    private IUserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        UserDTO dot = userService.login(userDTO);
        return Result.success(dot);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        return Result.success(userService.register(userDTO));
    }

    @PostMapping
    public boolean save(@RequestBody User user) {
        return userService.saveOrUpdate(user);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }

    @PostMapping("/del/batch")
    public boolean deleteBody (@RequestBody List<Integer> ids){
        return userService.removeByIds(ids);
        }

    @GetMapping
    public List<User> findAll() {
        return userService.list();
    }

    @GetMapping("/role/{role}")
    public Result findUsersByRole(@PathVariable String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role",role);
        List<User> list =userService.list(queryWrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String username,
                               @RequestParam(defaultValue = "") String email,
                               @RequestParam(defaultValue = "") String address){
        QueryWrapper<User> queryWrapper= new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(username)){
            queryWrapper.like("username",username);
        }
        if (!"".equals(email)){
            queryWrapper.like("email",email);
        }
        if (!"".equals(address)){
            queryWrapper.like("address",address);
        }

        User currentUser = TokenUtils.getCurrentUser();
        System.out.println(currentUser.getNickname());

        return Result.success(userService.page(new Page<>(pageNum, pageSize),queryWrapper));

    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        List<User> list =userService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);
     //   writer.addHeaderAlias("username","用户名");
     //   writer.addHeaderAlias("password","密码");
     //   writer.addHeaderAlias("nickname","昵称");
     //   writer.addHeaderAlias("email","邮箱");
     //   writer.addHeaderAlias("phone","电话");
     //   writer.addHeaderAlias("address","地址");
     //   writer.addHeaderAlias("createTime","创建时间");

        writer.write(list,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+ fileName+".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out,true);
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list =reader.readAll(User.class);
        userService.saveBatch(list);
        return  true;
    }

}

