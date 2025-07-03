package com.lunchchat.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI LUNCH_CHAT_API() {
    Info info = new Info()
        .title("런치챗_API")
        .version("1.0")
        .description("LunchChat API입니다");

    return new OpenAPI()
        .addServersItem(new Server().url("/"))
        .components(new Components())
        .info(info);
  }

}
