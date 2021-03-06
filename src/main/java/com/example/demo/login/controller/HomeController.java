package com.example.demo.login.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    private Map<String, String> radioMarriage;

    private Map<String, String> initRadioMarriage(){
        Map<String, String> radio = new LinkedHashMap<String, String>();

        radio.put("既婚", "true");
        radio.put("未婚", "false");

        return radio;
    }

    @GetMapping("/home")
    public String getHome(Model model) {

        model.addAttribute("contents", "login/home :: home_contents");

        return "login/homeLayout";
    }

    @PostMapping("/logout")
    public String postLogout(){

        return "/login/login";
    }

    @GetMapping("/userList")
    public String getUserList(Model model) {
        model.addAttribute("contents", "login/userList :: userList_contents");

        List<User> userList = userService.selectMany();

        model.addAttribute("userList", userList);

        int count = userService.count();
        model.addAttribute("userListCount", count);

        return "login/homeLayout";
    }

    @GetMapping("/userDetail/{id:.+}")
    public String getUserDetail(@ModelAttribute SignupForm form, Model model, @PathVariable("id")String userId) {
        System.out.println("userId = " + userId);

        radioMarriage = initRadioMarriage();

        model.addAttribute("contents", "login/userDetail :: userDetail_contents");
        model.addAttribute("radioMarriage", radioMarriage);

        if (userId != null && userId.length() > 0) {
            User user = userService.selectOne(userId);

            form.setUserId(user.getUserId());
            form.setUserName(user.getUserName());
            form.setBirthday(user.getBirthday());
            form.setAge(user.getAge());
            form.setMarriage(user.isMarriage());

            model.addAttribute("signupForm", form);
        }

        return "login/homeLayout";
    }

    @PostMapping(value = "/userDetail", params="update")
    public String postUserDetailUpdate(@ModelAttribute SignupForm form, Model model) {
        System.out.println("更新処理");

        User user = new User();

        user.setUserId(form.getUserId());
        user.setPassword(form.getPassword());
        user.setUserName(form.getUserName());
        user.setBirthday(form.getBirthday());
        user.setAge(form.getAge());
        user.setMarriage(form.isMarriage());

        try {
            boolean result = userService.updateOne(user);

            if (result == true) {
                model.addAttribute("result", "更新成功");
            } else {
                model.addAttribute("result", "更新失敗");
            }
        } catch(DataAccessException e) {
            model.addAttribute("result", "更新失敗(トランザクションテスト)");
        }

        return getUserList(model);
    }

    @PostMapping(value = "/userDetail", params="delete")
    public String postUserDetailDelete(@ModelAttribute SignupForm form, Model model) {
        System.out.println("削除処理");

        boolean result = userService.deleteOne(form.getUserId());

        if (result == true) {
            model.addAttribute("result", "削除成功");
        } else {
            model.addAttribute("result", "削除失敗");
        }

        return getUserList(model);
    }

    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("contents", "login/admin :: admin_contents");

        return "login/homelayout";
    }
}
