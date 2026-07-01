package com.bookkok.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

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
            "/admin"
    })
    public String index(Model model) {
        model.addAttribute("appName", "BookKOK");
        return "index";
    }
}
