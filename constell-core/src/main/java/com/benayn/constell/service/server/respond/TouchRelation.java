package com.benayn.constell.service.server.respond;

import static java.util.Optional.ofNullable;

import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Usually relation define by {@link DefineTouch#master()}
 */
@Data
public class TouchRelation {

    @NotNull
    private List<String> masterIds;
    @NotEmpty
    private String module;
    @NotNull
    private List<String> slaveIds;

    public List<Long> getMasterNumberIds() {
        return getNumberIds(this.masterIds);
    }

    public List<Long> getSlaveNumberIds() {
        return getNumberIds(this.slaveIds);
    }

    private List<Long> getNumberIds(List<String> targetIds) {
        return ofNullable(targetIds).orElse(Lists.newArrayList())
            .stream()
            .map(Longs::tryParse)
            .collect(Collectors.toList())
            ;
    }

}
