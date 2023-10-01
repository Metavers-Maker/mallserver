package com.muling.mall.chat.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonConverter {

    PersonConverter INSTANCE = Mappers.getMapper(PersonConverter.class);

    @Mappings({
            @Mapping(source = "name", target = "userName"),
            @Mapping(source = "age", target = "age"),
            @Mapping(source = "birthday", target = "birthday")
    })
    PersonDTO do2dto(PersonDO person);

    public static void main(String[] args) {
        PersonDO personDO = new PersonDO();
        personDO.setName("Hollis");
        personDO.setAge(26);
        personDO.setBirthday(new Date());
        personDO.setId(1);
        PersonDTO personDTO = PersonConverter.INSTANCE.do2dto(personDO);
        System.out.println(personDTO);
    }
}
