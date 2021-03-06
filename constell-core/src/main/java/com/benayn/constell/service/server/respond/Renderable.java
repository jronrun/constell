package com.benayn.constell.service.server.respond;

import static com.benayn.constell.service.common.BaseConstants.DEFAULT_PAGE_SIZE;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Supports <a href="http://beanvalidation.org/1.0/spec/">JSR 303: Bean Validation</a>
 *
 * @see Actionable
 * @see DefineElement
 * @see Searchable
 * @see Listable
 * @see Editable
 * @see Creatable
 * @see Updatable
 * @see DefineTouch
 * @see Touchable
 * @see Accessable
 */
@Getter
@Setter
@ToString
public abstract class Renderable {

    /**
     * Touch item ID
     */
    private Long touchId;
    private String touchModuleId;
    private String touchModule;
    private boolean switchable;
    private String touchListTitleFragment;
    private String touchListCellFragment;
    private String touchFromItemFragment;

    /**
     * Query only owner to touchId if true, or all the touch target items if false
     */
    private boolean touchOwner;

    // <touchId, touchItemFragmentValue>
    private Map<String, String> touchesItemValue;
    private String touchListValue;

    /**
     * List row field fragment value map
     */
    private Map<String, String> fieldFragmentValue = Maps.newHashMap();

    /**
     * List action fragment value
     */
    private String action;
    /**
     * True if list action use fragment
     */
    private boolean fragmentAction;

    /**
     * List append action fragment value
     */
    private String appendAction;
    /**
     * True if has list append action fragment
     */
    private boolean fragmentAppendAction;

    private int pageNo;
    private int pageSize = DEFAULT_PAGE_SIZE;

    public boolean hasTouch() {
        return null != getTouchId() && !isNullOrEmpty(getTouchModule());
    }

    public boolean hasTouchOwner() {
        return hasTouch() && isTouchOwner();
    }

    public void addFieldFragmentValue(String fieldName, String fragmentValue) {
        fieldFragmentValue.put(fieldName, fragmentValue);
    }

    /*
    public boolean isLike(String fieldName) {
        return checkFields().contains(fieldName);
    }

    private List<String> likeFields;
    private List<String> checkFields() {
        if (null != likeFields) {
            return likeFields;
        }

        likeFields = Lists.newArrayList();
        Lists.newArrayList(getClass().getDeclaredFields()).forEach(field -> {
            Searchable searchable = field.getAnnotation(Searchable.class);
            if (null != searchable && searchable.like()) {
                likeFields.add(field.getName());
            }
        });

        return likeFields;
    }
     */


}
