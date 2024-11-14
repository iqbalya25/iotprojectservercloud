package org.example.iotproject.config;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @PostConstruct
    public void init() {
        log.info("MQTT Configuration initialized with URL: {}, Port: {}", brokerUrl);
        log.info("Client ID: {}", clientId);
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { brokerUrl });
        options.setCleanSession(true);
        options.setAutomaticReconnect(true); // Add this
        return options;
    }

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String serverURI = brokerUrl ;
        log.info("Connecting to MQTT broker at: {}", serverURI);
        log.info("Using client ID: {}", clientId);

        MqttClient mqttClient = new MqttClient(serverURI, clientId);
        mqttClient.connect(mqttConnectOptions());

        if (mqttClient.isConnected()) {
            log.info("Successfully connected to MQTT broker");
        } else {
            log.error("Failed to connect to MQTT broker");
        }

        return mqttClient;
    }
}
