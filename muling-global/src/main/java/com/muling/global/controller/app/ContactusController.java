package com.muling.global.controller.app;

import com.muling.common.result.Result;
import com.muling.common.web.util.MemberUtils;
import com.muling.global.converter.ContactusConverter;
import com.muling.global.pojo.entity.Contactus;
import com.muling.global.pojo.form.ContactusForm;
import com.muling.global.service.IContactusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "app-联系我们")
@RestController
@RequestMapping("/app-api/v1/contactus")
@RequiredArgsConstructor
public class ContactusController {

    private final IContactusService contactusService;

    @ApiOperation(value = "联系我们")
    @PostMapping
    public Result add(@Valid @RequestBody ContactusForm contactusForm) {
        Long memberId = MemberUtils.getMemberId();
        Contactus contactus = ContactusConverter.INSTANCE.form2po(contactusForm);
        contactus.setMemberId(memberId);
        boolean status = contactusService.save(contactus);
        return Result.success(status);
    }

}
