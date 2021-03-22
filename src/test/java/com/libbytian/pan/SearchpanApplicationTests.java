package com.libbytian.pan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class SearchpanApplicationTests {


	public static void main(String[] args) {
		String appId = "wx61a17a682672e1b1";
		//正则校验appid


 		Pattern p = Pattern.compile("^wx(?=.*\\d)(?=.*[a-z])[\\da-z]{16}$");
		boolean matches = p.matcher(appId).matches();
		System.out.println(matches);
	}

	@Test
	void contextLoads() {

	}

}
