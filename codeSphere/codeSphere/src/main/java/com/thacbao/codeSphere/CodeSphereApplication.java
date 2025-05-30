package com.thacbao.codeSphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CodeSphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeSphereApplication.class, args);
	}

}
