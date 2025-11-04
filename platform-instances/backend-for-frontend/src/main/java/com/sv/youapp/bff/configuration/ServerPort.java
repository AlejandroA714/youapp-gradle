package com.sv.youapp.bff.configuration;

import lombok.Getter;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ServerPort implements ApplicationListener<WebServerInitializedEvent> {

  @Getter private static int port = -1;

  @Override
  public void onApplicationEvent(WebServerInitializedEvent event) {
    port = event.getWebServer().getPort();
  }
}
