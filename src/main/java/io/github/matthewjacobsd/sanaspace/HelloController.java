package io.github.matthewjacobsd.sanaspace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/")
public class HelloController {

    @GetMapping
    public String showIndex() {
        return "index.html";
    }

    @GetMapping("/api/hello")
    @ResponseBody 
    public String getHello() {
        return "Hello from SpringBoot API";
    }
}