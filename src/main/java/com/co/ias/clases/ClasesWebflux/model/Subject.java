package com.co.ias.clases.ClasesWebflux.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Builder
@Data
public class Subject {

    @Id
    private Integer id;
    private String name;

}
