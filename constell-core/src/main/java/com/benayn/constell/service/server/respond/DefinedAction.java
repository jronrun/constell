package com.benayn.constell.service.server.respond;

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

}
