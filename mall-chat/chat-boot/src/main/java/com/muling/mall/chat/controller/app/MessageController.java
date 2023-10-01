package com.muling.mall.chat.controller.app;

import com.muling.mall.chat.dto.Message;
import com.muling.mall.chat.dto.ResponseMessage;
import com.muling.mall.chat.entity.User;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MessageController {

    @MessageMapping("/public-message")
    @SendTo("/topic/public-messages")
    public ResponseMessage getMessage(final Message message) throws BizException {
        return new ResponseMessage(HtmlUtils.htmlEscape(message.getData()));
    }

    @MessageMapping("/private-message")
    @SendToUser(destinations = "/queue/private-messages")
//    @SendToUser(destinations = "/queue/private-messages", broadcast = false)
    public ResponseMessage getPrivateMessage(final Message message,
                                             final User user) {
        return new ResponseMessage(HtmlUtils.htmlEscape(
                "Sending private message to user " + user.getName() + ": "
                        + message.getData())
        );
    }

    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/errors")
    public Result handleQueueException(BizException exception) {
        // ...
        return Result.failed(exception.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser(destinations = "/topic/errors")
    public Result handleTopicException(BizException exception) {
        // ...
        return Result.failed(exception.getMessage());
    }
}
