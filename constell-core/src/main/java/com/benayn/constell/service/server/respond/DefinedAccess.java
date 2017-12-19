package com.benayn.constell.service.server.respond;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Data;

@Data
public class DefinedAccess {

    private boolean hasCreate;
    private boolean hasRetrieve;
    private boolean hasUpdate;
    private boolean hasDelete;

    private Map<String, DefinedTouchAccess> touchAccess = Maps.newHashMap();

    public static DefinedAccess denyAll() {
        return new DefinedAccess();
    }

    public static DefinedAccess allowAll() {
        DefinedAccess access = new DefinedAccess();
        access.setHasCreate(true);
        access.setHasRetrieve(true);
        access.setHasUpdate(true);
        access.setHasDelete(true);
        return access;
    }

}
