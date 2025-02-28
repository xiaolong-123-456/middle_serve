package com.ruoyi;

import com.ruoyi.operate.utils.ExecBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    @Autowired
    private ExecBot execBot;
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            //飞机的机器人服务启动

            //梯子在自己电脑上就写127.0.0.1  软路由就写路由器的地址
            String proxyHost = "127.0.0.1";
            //端口根据实际情况填写，说明在上面，自己看
            int proxyPort = 33211;

            DefaultBotOptions botOptions = new DefaultBotOptions();
            botOptions.setProxyHost(proxyHost);
            botOptions.setProxyPort(proxyPort);
            //注意一下这里，ProxyType是个枚举，看源码你就知道有NO_PROXY,HTTP,SOCKS4,SOCKS5;
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            DefaultBotSession defaultBotSession = new DefaultBotSession();
//            defaultBotSession.setOptions(botOptions);
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(defaultBotSession.getClass());
                telegramBotsApi.registerBot(execBot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

}
