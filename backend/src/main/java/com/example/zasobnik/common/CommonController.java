package com.example.zasobnik.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/test")
@Tag(name = "Common Controller", description = "General purpose endpoints.")
public class CommonController {

    @GetMapping("/me")
    public String currentUser() {

        return ("principal").toString();
    }
}
