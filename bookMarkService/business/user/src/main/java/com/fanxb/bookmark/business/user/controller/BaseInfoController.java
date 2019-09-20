package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.entity.EmailUpdateBody;
import com.fanxb.bookmark.business.user.entity.UpdatePasswordBody;
import com.fanxb.bookmark.business.user.entity.UsernameBody;
import com.fanxb.bookmark.business.user.service.BaseInfoService;
import com.fanxb.bookmark.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * 类功能简述：用户基本信息相关功能
 *
 * @author fanxb
 * @date 2019/9/18 15:53
 */
@RestController
@RequestMapping("/baseInfo")
@Validated
public class BaseInfoController {

    @Autowired
    private BaseInfoService baseInfoService;

    /**
     * Description: 修改密码
     *
     * @param oldPass 旧密码
     * @param newPass 新密码
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/9/18 15:49
     */
    @PostMapping("/password")
    public Result changePassword(@Valid @RequestBody UpdatePasswordBody body) {
        this.baseInfoService.changePassword(body);
        return Result.success(null);
    }


    /**
     * Description: 修改用户名
     *
     * @param body body
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/9/18 15:42
     */
    @PostMapping("/username")
    public Result updateUsername(@Validated @RequestBody UsernameBody body) {
        this.baseInfoService.updateUsername(body.getUsername());
        return null;
    }

    /**
     * Description: 修改邮箱，需要先确认密码,获取checkId，然后还需确认新邮箱
     *
     * @param email   新的邮箱地址
     * @param checkId 确认id
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/9/18 15:41
     */
    @PostMapping("/email")
    public Result updateEmail(@Validated @RequestBody EmailUpdateBody body) {
        return null;
    }
}
