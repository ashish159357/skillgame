package com.techhitter.app.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@Data
public class ClientConfiguration {

    @Value("${client.ip}")
    private String clientIp;

}
