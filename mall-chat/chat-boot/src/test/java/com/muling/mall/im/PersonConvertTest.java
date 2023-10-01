package com.muling.mall.im;

import com.muling.mall.chat.converter.PersonConverter;
import com.muling.mall.chat.converter.PersonDO;
import com.muling.mall.chat.converter.PersonDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Slf4j
public class PersonConvertTest {
    @Resource
    private PersonConverter personConverter;

    @Test
    public void test(){
        PersonDO personDO = new PersonDO();
        personDO.setName("Hollis");
        personDO.setAge(26);
        personDO.setBirthday(new Date());
        personDO.setId(1);
        PersonDTO personDTO = personConverter.do2dto(personDO);
        System.out.println(personDTO);
    }
}
