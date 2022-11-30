package com.zht.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zht.springboot.common.Result;
import com.zht.springboot.entity.Files;
import com.zht.springboot.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;


    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename =file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();


        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        File parentFile = uploadFile.getParentFile();
        if(!parentFile .exists()){
            parentFile.mkdirs();
        }

        String md5;
        String url;
        if(uploadFile.exists()) {
            md5 = SecureUtil.md5(uploadFile);
            Files files = getFileByMd5(md5);


            if (files != null) {
                url = files.getUrl();

            } else {
                file.transferTo(uploadFile);
                url = "http://localhost:9090/file/" + fileUUID;
            }
        }else {
            file.transferTo(uploadFile);
            md5 = SecureUtil.md5(uploadFile);
            url = "http://localhost:9090/file/" + fileUUID;
        }





        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        saveFile.setIsDelete(false);
        fileMapper.insert(saveFile);

        return url;
    }


    @GetMapping("{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        File uploadFile = new File(fileUploadPath + fileUUID);
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileUUID,"UTF-8"));
        response.setContentType("application/octet-stream");

        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    private Files getFileByMd5(String md5){
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        return fileMapper.selectOne(queryWrapper);

    }

    

    @PostMapping("/update")
    public Result update (@RequestBody Files files) {
        return Result.success(fileMapper.updateById(files));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileMapper.selectById(id);
        files.setIsDelete(true);
        fileMapper.updateById(files);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBody (@RequestBody List<Integer> ids) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        List<Files> files = fileMapper.selectList(queryWrapper);
        for (Files file :files) {
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return Result.success();
    }


    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name){
        QueryWrapper<Files> queryWrapper= new QueryWrapper<>();

//        System.out.println("queryWrapper start1");
//        System.out.println(fileMapper);
//
//        System.out.println("queryWrapper end");
        queryWrapper.eq("is_delete",false);
//        queryWrapper.orderByDesc("id");
//        if (!"".equals(name)){
//            queryWrapper.like("name",name);
//        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize),queryWrapper));

//        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize),queryWrapper));
    }

}
