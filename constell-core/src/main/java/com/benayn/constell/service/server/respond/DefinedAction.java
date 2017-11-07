package com.benayn.constell.service.server.respond;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class DefinedAction {

    private boolean hasEdit;
    private boolean hasEditField;
    private String editField;
    private boolean hasDelete;
    private boolean hasActionFragment;
    private String actionFragment;
    private boolean hasAction;
    private String uniqueField;

    private boolean hasCreate;
    private boolean hasCreateFragment;
    private String createFragmentValue;

    private boolean hasTouch;
    private List<DefinedTouch> touches;
    private boolean hasTouchField;
    private Map<String, DefinedTouch> touchFields;

}
