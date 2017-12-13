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

    private boolean hasReadyFragment;
    private String readyFragmentValue;

    private boolean hasAppendFragment;
    private String appendFragment;

    //default touch, show in action column
    private boolean hasTouch;
    private List<DefinedTouch> touches;
    //filed touch, attach to given filed
    private boolean hasFieldTouch;
    private Map<String, DefinedTouch> fieldTouches;

}
