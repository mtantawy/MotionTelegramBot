package com.mtantawy.motiontelegrambot;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {
    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MotionConfig motionConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage()
            && update.getMessage().hasText()
            && update.getMessage().getChatId().toString().equals(botConfig.getChatId())
        ) {
            if (handleIfCommand(update.getMessage().getText())) {
                return;
            }

            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(update.getMessage().getText());

            log.info(
                "Chat ID: {}, Message: {}",
                update.getMessage().getChatId().toString(),
                update.getMessage().getText()
            );

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleIfCommand(String command) {
        try {
            switch (command) {
                case "/get_status" -> {
                    sendMessage(getDetectionStatus());
                    sendMessage(getConnectionStatus());
                    return true;
                }
                case "/start_detection" -> {
                    startDetection();
                    sendMessage("Motion detection started!");
                    return true;
                }
                case "/pause_detection" -> {
                    pauseDetection();
                    sendMessage("Motion detection paused!");
                    return true;
                }
                case "/snapshot" -> {
                    createSnapshot();
                    sendMessage("Snapshot created!");
                    return true;
                }
                case "/restart_motion" -> {
                    restartMotionApplication();
                    sendMessage("Restarting Motion application!");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("an error occurred while handling command", e);
        }

        return false;
    }

    public void sendBootMessage() {
        SendAnimation msg = new SendAnimation();
        msg.setChatId(botConfig.getChatId());
        msg.setAnimation(new InputFile("CgACAgQAAxkBAAOqYYVnRpOICNen8Hf48UOc-KGzvdMAAogCAAIbdc1S6JMAAWXNtq2yIgQ"));
        msg.setCaption("Hello there!");
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("an error occurred while sending boot message", e);
        }
    }

    public void sendMessage(String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(botConfig.getChatId());
        message.setText(messageText);
        log.info("Sending message: {}", messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("an error occurred while sending a text message", e);
        }
    }

    public void sendImage(InputStream stream, String name) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(botConfig.getChatId());
        sendPhotoRequest.setPhoto(new InputFile(stream, name));
        log.info("Sending image");
        try {
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("an error occurred while sending a photo message", e);
        }
    }

    public void sendVideo(InputStream stream, String name) {
        SendVideo sendVideoRequest = new SendVideo();
        sendVideoRequest.setChatId(botConfig.getChatId());
        sendVideoRequest.setVideo(new InputFile(stream, name));
        log.info("Sending video");
        try {
            execute(sendVideoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("an error occurred while sending a video message", e);
        }
    }

    public String getDetectionStatus() throws IOException {
        String url = String.format(
            "http://localhost:%s/%s/detection/status",
            motionConfig.getPort(),
            motionConfig.getCamId()
        );

        return makeHttpRequest(new HttpGet(url));
    }

    public String getConnectionStatus() throws IOException {
        String url = String.format(
            "http://localhost:%s/%s/detection/connection",
            motionConfig.getPort(),
            motionConfig.getCamId()
        );

        return makeHttpRequest(new HttpGet(url));
    }

    public void startDetection() throws IOException {
        String url = String.format(
            "http://localhost:%s/%s/detection/start",
            motionConfig.getPort(),
            motionConfig.getCamId()
        );

        makeHttpRequest(new HttpGet(url));
    }

    public void pauseDetection() throws IOException {
        String url = String.format(
            "http://localhost:%s/%s/detection/pause",
            motionConfig.getPort(),
            motionConfig.getCamId()
        );

        makeHttpRequest(new HttpGet(url));
    }

    public void createSnapshot() throws IOException {
        String url = String.format(
            "http://localhost:%s/%s/action/snapshot",
            motionConfig.getPort(),
            motionConfig.getCamId()
        );

        makeHttpRequest(new HttpGet(url));
    }

    public void restartMotionApplication() throws IOException {
        String url = String.format(
            "http://localhost:%s/%s/action/restart",
            motionConfig.getPort(),
            motionConfig.getCamId()
        );

        makeHttpRequest(new HttpGet(url));
    }

    private String makeHttpRequest(HttpUriRequest request) throws IOException {
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            String responseText =  EntityUtils.toString(response.getEntity());
            log.info("Motion Web Control responded with: {}", responseText);

            return responseText;
        }
    }
}
