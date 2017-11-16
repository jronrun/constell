package com.benayn.constell.service.server.respond;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class TouchRelation {

    @NotNull
    private String touchId;
    @NotEmpty
    private String module;
    @NotNull
    private List<String> touchToIds;
    private boolean build;

}
