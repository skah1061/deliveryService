package com.sprta.deliveryproject.controller;

import com.sprta.deliveryproject.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails == null) {
            return "login";
        }
        model.addAttribute("memberId", userDetails.getMember().getId());
        model.addAttribute("memberProfilename",userDetails.getMember().getProfilename());
        return "index";
    }
    @GetMapping("/shops/manage")
    public String managepage(){
        return "back_office";
    }
}
