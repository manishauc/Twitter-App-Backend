package com.springboot.blog.service;

import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.LoginResponseDto;
import com.springboot.blog.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    RegisterDto register(RegisterDto registerDto);
    
    LoginResponseDto userLogin(LoginDto loginDto);
}
