# MotionTelegramBot
  This is a thin wrapper around [`motion`](https://motion-project.github.io) that connects it to a [Telegram Bot](https://core.telegram.org/bots)
  
  It works by configuring `motion` to call web endpoints when certain events are [triggered](https://motion-project.github.io/motion_config.html#OptDetail_Scripts),
  and also allows the bot to control `motion` by calling its [web control endpoints](https://motion-project.github.io/motion_config.html#OptDetail_Webcontrol)

## How to run on a RaspberryPi _using docker_

1. run `mvn package` to generate a `jar`
2. Create a dir `MotionTelegramBot` under `/home/pi` (or elsewhere but make sure to update the path in the `docker run` command)
3. Run the following after replacing placeholders
```
docker run -d \
-v /home/pi/MotionTelegramBot/:/jar \
-w /jar \
--network="host" \
-e BOT_CHAT_ID=<chat id> \
-e BOT_USERNAME=<username> \
-e BOT_TOKEN=<token> \
-e MOTION_PORT=<port> \
-e MOTION_CAM_ID=<cam id> \
--rm arm32v7/eclipse-temurin:17 java \
-jar MotionTelegramBot.jar
```
4. Optionally inspect logs to confirm no errors
5. The bot sends a "Hello there!" message when it boots up

## Todo
- [ ] Explain how to get values for the placeholders
- [ ] Explain changes needed to `motion.conf`
- [ ] Explain why `--network="host"` is needed
- [ ] Explain how to run directly with `java` _without docker_
