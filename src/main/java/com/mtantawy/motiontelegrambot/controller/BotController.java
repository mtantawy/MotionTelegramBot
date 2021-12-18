package com.mtantawy.motiontelegrambot.controller;

import com.mtantawy.motiontelegrambot.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class BotController {

    private final Bot bot;

    public BotController(Bot bot) {
        this.bot = bot;
    }

    @PostMapping("/event_start")
    public String postEventStart() {
        bot.sendMessage("Motion event started!");
        return "event started message sent";
    }

    @PostMapping("/event_end")
    public String postEventEnd() {
        bot.sendMessage("Motion event ended!");
        return "event ended message sent";
    }

    @PostMapping("/picture_save")
    public String postPictureSave(@RequestParam("file") MultipartFile file) throws IOException {
        bot.sendImage(file.getInputStream(), file.getName());
        return "picture message sent";
    }

    @PostMapping("/movie_save")
    public String postMovieSave(@RequestParam("file") MultipartFile file) throws IOException {
        bot.sendVideo(file.getInputStream(), file.getName());
        return "video message sent";
    }

    @GetMapping("/self_test")
    public String getSelfTest() throws IOException {
        bot.sendMessage("Self Test");
        bot.sendMessage(bot.getDetectionStatus());
        bot.sendMessage(bot.getConnectionStatus());
        return "message sent";
    }
}
