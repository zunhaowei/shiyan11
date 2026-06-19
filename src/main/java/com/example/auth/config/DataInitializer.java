package com.example.auth.config;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 —— 应用启动时创建测试用户
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 创建用户1: wzh2023214772
        if (!userRepository.findByUsername("wzh2023214772").isPresent()) {
            User user1 = User.builder()
                    .username("wzh2023214772")
                    .password(passwordEncoder.encode("123456"))  // 默认密码
                    .email("wzh2023214772@example.com")
                    .build();
            userRepository.save(user1);
            System.out.println("✓ 用户 wzh2023214772 创建成功（密码：123456）");
        }

        // 创建用户2: xjj2023214520
        if (!userRepository.findByUsername("xjj2023214520").isPresent()) {
            User user2 = User.builder()
                    .username("xjj2023214520")
                    .password(passwordEncoder.encode("123456"))  // 默认密码
                    .email("xjj2023214520@example.com")
                    .build();
            userRepository.save(user2);
            System.out.println("✓ 用户 xjj2023214520 创建成功（密码：123456）");
        }
    }
}