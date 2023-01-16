package com.ayush.ravan;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class RavanApplication implements ApplicationRunner, CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RavanApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("This will run just after application started : CommandLineRunner");
		System.out.println("CommandLine Runner called with " + Arrays.toString(args));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("This will run just after application started : ApplicationRunner");
		System.out.println("Application Runner called with " + args.getNonOptionArgs());
		System.out.println("Application Runner called with " + args.getOptionNames());
	}
}
