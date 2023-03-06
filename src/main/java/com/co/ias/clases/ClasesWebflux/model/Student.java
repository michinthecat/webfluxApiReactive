package com.co.ias.clases.ClasesWebflux.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Student {

    @Id
    private Integer id;
    private String name;
    private String email;
}
