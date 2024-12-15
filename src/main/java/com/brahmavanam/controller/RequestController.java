package com.brahmavanam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RequestController {

    @GetMapping("/namana")
    public RedirectView namana(){
        String wordpressUrl = "https://brahmavanam.wordpress.com/%E0%B2%A8%E0%B2%AE%E0%B2%A8/"; // Replace with your WordPress page URL
        return new RedirectView(wordpressUrl);
    }

    @GetMapping("/calendar")
    public String calendar(){
        return "calendarHome";
    }

}
