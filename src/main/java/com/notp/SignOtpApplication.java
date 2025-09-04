package com.notp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sign OTP 启动类
 *
 * @author sign
 */
@Slf4j
@SpringBootApplication
public class SignOtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignOtpApplication.class, args);
        printStartupBanner();
    }
    private static void printStartupBanner() {
        String banner = "\n" +
                "////////////////////////////////////////////////////////////////////\n" +
                "//                          _ooOoo_                               //\n" +
                "//                         o8888888o                              //\n" +
                "//                         88\" . \"88                              //\n" +
                "//                         (| ^_^ |)                              //\n" +
                "//                         O\\  =  /O                              //\n" +
                "//                      ____/`---'\\____                           //\n" +
                "//                    .'  \\\\|     |//  `.                         //\n" +
                "//                   /  \\\\|||  :  |||//  \\                        //\n" +
                "//                  /  _||||| -:- |||||-  \\                       //\n" +
                "//                  |   | \\\\\\  -  /// |   |                       //\n" +
                "//                  | \\_|  ''\\---/''  |   |                       //\n" +
                "//                  \\  .-\\__  `-`  ___/-. /                       //\n" +
                "//                ___`. .'  /--.--\\  `. . ___                     //\n" +
                "//              .\"\" '<  `.___\\_<|>_/___.'  >'\"\".                  //\n" +
                "//            | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |                 //\n" +
                "//            \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /                 //\n" +
                "//      ========`-.____`-.___\\_____/___.-`____.-'========         //\n" +
                "//                           `=---='                              //\n" +
                "//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //\n" +
                "//            佛祖保佑       永无BUG       永无宕机               //\n" +
                "////////////////////////////////////////////////////////////////////\n" +
                "//  Sign OTP 服务启动成功  |  若依安全认证系统  |  v3.9.0        //\n" +
                "////////////////////////////////////////////////////////////////////";

        log.info(banner);
        log.info(":: Sign OTP :: \t\t(v{})", "1.0.0");
    }
}
