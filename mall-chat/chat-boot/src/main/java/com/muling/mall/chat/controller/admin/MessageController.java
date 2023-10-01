package com.muling.mall.chat.controller.admin;

import com.muling.common.web.util.UserUtils;
import com.muling.mall.chat.dto.Message;
import com.muling.mall.chat.entity.User;
import com.muling.mall.chat.service.ChatService;
import com.muling.mall.chat.service.NotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;

@Tag(name = "admin-即时通讯")
@RestController("MessageController")
@RequestMapping("/app-api/v1/ims")
public class MessageController {

    @Resource
    private ChatService service;
    @Resource
    private NotifyService notifyService;

    @Autowired
    private SimpUserRegistry userRegistry;

    @Operation(summary = "群发消息", description = "群发消息", tags = {"admin-即时通讯"})
    @PostMapping("/public-message")
    public void sendPublicMessage(@RequestBody final Message message) {
        String username = UserUtils.getUsername();
        service.sendPublicMessage(username + " 说： " + message.getData());
        notifyService.sendPublicNotify();
    }

    @Operation(summary = "私发消息", description = "私发消息", tags = {"admin-即时通讯"})
    @PostMapping("/private-message/{username}")
    public void sendPrivateMessage(
            @Parameter(description = "用户名") @PathVariable final String username,
            @RequestBody final Message message) {
        String data = HtmlUtils.htmlEscape(
                "Sending private message to user " + username + ": "
                        + message.getData());
        SimpUser simpUser = userRegistry.getUser(username);
        if (simpUser != null) {
            User user = (User) simpUser.getPrincipal();
            service.sendPrivateMessage(username, data, user.getSessionId());
            notifyService.sendPrivateNotify(username);
        }
    }

    @Operation(tags = {"admin-即时通讯"})
    @PostMapping("/public-notify")
    public void sendPublicNotify() {
        notifyService.sendPublicNotify();
    }

    @Operation(tags = {"admin-即时通讯"})
    @PostMapping("/private-notify/{username}")
    public void sendPrivateNotify(@PathVariable final String username) {
        notifyService.sendPrivateNotify(username);
    }

    @Operation(summary = "在线人数", description = "在线人数", tags = {"admin-即时通讯"})
    @PostMapping("/oneliners")
    public int oneliners() {
        return userRegistry.getUserCount();
    }
}
