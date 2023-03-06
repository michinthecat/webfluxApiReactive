package com.co.ias.clases.ClasesWebflux.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Enrollment {
        @Id
        private Integer id;
        private Student student;
        private Subject subject;

}
