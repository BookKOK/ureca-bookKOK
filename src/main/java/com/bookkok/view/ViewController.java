package com.bookkok.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // 해당 경로들로 접근을 하게 되면
    @GetMapping({
            "/",
            "/ui",
            "/login",
            "/signup",
            "/home",
            "/reservations",
            "/reservations/info",
            "/reservations/done",
            "/clubs",
            "/clubs/new",
            "/clubs/{clubId}",
            "/board",
            "/board/new",
            "/board/{postId}",
            "/board/{postId}/edit",
            "/mypage",
            "/mypage/password",
            "/admin"
    })

    // 아래 html 파일로 반환
    public String index(Model model) {
        model.addAttribute("appName", "BookKOK");
        return "forward:/index.html";
    }
}
