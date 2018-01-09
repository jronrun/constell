package com.benayn.constell.service.server.component;

import static com.benayn.constell.service.server.respond.DefineType.CREATABLE;
import static com.benayn.constell.service.server.respond.DefineType.SEARCHABLE;
import static com.benayn.constell.service.server.respond.DefineType.UPDATABLE;
import static com.benayn.constell.service.server.respond.HtmlTag.INPUT;
import static com.benayn.constell.service.server.respond.HtmlTag.TEXTAREA;
import static com.benayn.constell.service.server.respond.HtmlTag.UNDEFINED;
import static com.benayn.constell.service.util.LZString.encodes;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.common.Pair;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.Creatable;
import com.benayn.constell.service.server.respond.DataExchange;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.DefineTouch;
import com.benayn.constell.service.server.respond.DefineType;
import com.benayn.constell.service.server.respond.DefinedAccess;
import com.benayn.constell.service.server.respond.DefinedAction;
import com.benayn.constell.service.server.respond.DefinedEditElement;
import com.benayn.constell.service.server.respond.DefinedElement;
import com.benayn.constell.service.server.respond.DefinedOption;
import com.benayn.constell.service.server.respond.DefinedTouch;
import com.benayn.constell.service.server.respond.DefinedTouchAccess;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.InputType;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.OptionValue;
import com.benayn.constell.service.server.respond.PageInfo;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Saveable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.TouchAccessable;
import com.benayn.constell.service.server.respond.Touchable;
import com.benayn.constell.service.server.respond.Updatable;
import com.benayn.constell.service.server.service.SaveEntity;
import com.benayn.constell.service.server.service.SearchEntity;
import com.benayn.constell.service.server.service.SearchField;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
public class ViewObjectResolverBean implements ViewObjectResolver {

    private MessageSource messageSource;
    private TemplateEngine fragmentTemplateEngine;
    private static final String ELEMENT_ID_FORMAT = "el_%s";
    private static final String ELEMENT_ID_WITH_PREFIX_FORMAT = "el_%s_%s";
    private static final String ELEMENT_NAME_WITH_PREFIX_FORMAT = "%s.%s";
    private static final String SEARCH_ELEMENT_ID_FORMAT = "qry_%s";
    private static final String HIDDEN_STYLE = "display:none;";
    private static final String UNCHANGEABLE_MARK = "%s_unchangeable";

    private static final String FIELD_CREATE_TIME = "createTime";
    private static final String FIELD_LAST_MODIFY_TIME = "lastModifyTime";

    public ViewObjectResolverBean(MessageSource messageSource, TemplateEngine fragmentTemplateEngine) {
        this.messageSource = messageSource;
        this.fragmentTemplateEngine = fragmentTemplateEngine;
    }

    @SuppressWarnings("WeakerAccess")
    @Cacheable(value = "defined_pages", sync = true, key = "#viewObjectType.simpleName")
    public List<Field> getFields(Class<?> viewObjectType) {
        return newArrayList(viewObjectType.getDeclaredFields());
    }

    @Override
    public List<DefinedElement> getDefinedSearch(Class<? extends Renderable> viewObjectType, Object value, DataExchange dataExchange) {
        return getDefinedElements(SEARCHABLE, viewObjectType, value, dataExchange);
    }

    @Override
    public DefinedEditElement getDefinedEdit(Class<? extends Renderable> viewObjectType,
        Object value, DataExchange dataExchange) {
        DefinedEditElement defined = new DefinedEditElement();
        DefineType defineType = null == value ? CREATABLE : UPDATABLE;
        List<DefinedElement> elements = getDefinedElements(defineType, viewObjectType, value, dataExchange);

        elements.forEach(element -> {
            // hidden element
            if (element.isHidden()) {
                defined.addHiddenElement(element);
            }
            // row element
            else if (TEXTAREA == element.getTag()) {
                defined.addRowElement(element);
            }
            // well element
            else {
                defined.addWellElement(element);
            }
        });

        return defined;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Renderable> getDefinedPage(Class<? extends Renderable> viewObjectType, Page<?> page,
        String manageBaseUrl, Renderable touchRenderable, DefinedAccess definedAccess) {
        List<?> items = page.getResource();
        checkNotNull(items, "page resource cannot be null");

        List<Renderable> renders = newArrayList();

        final boolean[] isDefinedUniqueField = {false};
        List<Field> defineFields = getFields(viewObjectType);
        Page<Renderable> newPage = page.cloneButResource();

        DefinedAction definedAction = getDefinedAction(viewObjectType, definedAccess, manageBaseUrl);
        newPage.addExtra("definedAction", definedAction);

        // label value <column, <value, label>>
        Map<String, Map<Object, String>> labelValue = Maps.newHashMap();
        List<String> toggleWidget = newArrayList();

        boolean hasTouch = null != touchRenderable && touchRenderable.hasTouch();

        DefinedTouchAccess definedTouchAccess = null;
        if (hasTouch) {
            definedTouchAccess = definedAccess.getTouchAccess().get(touchRenderable.getTouchModuleId());
        }
        DefinedTouchAccess finalDefinedTouchAccess = definedTouchAccess;

        if (hasTouch) {
            String touchColumn = "touchListValue";
            newPage.addColumn(touchColumn);
            String titleFragment = isNullOrEmpty(touchRenderable.getTouchListTitleFragment())
                ? "touch_column_title" : touchRenderable.getTouchListTitleFragment();
            newPage.addTitle(touchColumn, getFragmentValue(touchRenderable, titleFragment,
                (Context context) -> context.setVariable("access", finalDefinedTouchAccess)));
        }

        defineFields.forEach(field -> {
            String column = field.getName();
            DefineElement defineElement = field.getAnnotation(DefineElement.class);
            boolean hasDefineElement = null != defineElement;

            Touchable touchable = field.getAnnotation(Touchable.class);
            boolean isTouchable = hasTouch && null != touchable;

            Listable listable = field.getAnnotation(Listable.class);
            boolean isListable = !hasTouch && null != listable;

            if (isTouchable || isListable) {
                String title = null;
                if (isTouchable) {
                    title = touchable.value();
                }

                if (isListable) {
                    title = listable.value();
                }

                if (isNullOrEmpty(title) && hasDefineElement) {
                    title = defineElement.value();
                }

                if (!isNullOrEmpty(definedAction.getUniqueField())
                    && column.equals(definedAction.getUniqueField())) {
                    isDefinedUniqueField[0] = true;
                }

                checkArgument(!isNullOrEmpty(title), "undefined %s column title %s.%s",
                    hasTouch ? "touchable" : "listable",
                    viewObjectType.getSimpleName(), column);

                //options
                Class<? extends Enum> optionsClass = null;
                if (isTouchable) {
                    optionsClass = touchable.options();
                }

                if (isListable) {
                    optionsClass = listable.options();
                }

                if (!hasOptionsValue(optionsClass) && hasDefineElement) {
                    optionsClass = defineElement.options();
                }

                if (hasOptionsValue(optionsClass)) {
                    Map<Object, String> columnLabelValue = Maps.newHashMap();

                    EnumSet.allOf(optionsClass).forEach(item -> {
                        OptionValue option = (OptionValue) item;
                        columnLabelValue.put(option.getValue(), getMessage(option.getLabel(), option.getLabel()));
                    });

                    labelValue.put(column, columnLabelValue);
                }

                boolean isToggleWidget = false;
                if (isTouchable) {
                    isToggleWidget = touchable.toggleWidget();
                }

                if (isListable) {
                    isToggleWidget = listable.toggleWidget();
                }

                //toggle widget
                if (isToggleWidget) {
                    toggleWidget.add(column);
                }

                newPage.addColumn(column);
                newPage.addTitle(column, getMessage(title, title));
            }
        });

        checkArgument(isDefinedUniqueField[0],
            "undefined actionable unique field on type %s, unique field: %s",
            viewObjectType.getSimpleName(), definedAction.getUniqueField());

        newPage.addExtra("toggleWidget", toggleWidget);
        newPage.addExtra("labelValue", labelValue);

        List<Field> itemFields = null;
        if (items.size() > 0) {
            itemFields = getFields(items.get(0).getClass());
        }

        List<Field> finalItemFields = itemFields;
        items.forEach(item ->
            ofNullable(asRenderable(viewObjectType, defineFields,
                item, finalItemFields, definedAction, hasTouch, page.getExtraResource()))
            .ifPresent(render -> {
                if (hasTouch) {
                    setTouchListValue(render, item, definedAction, newPage, finalDefinedTouchAccess);
                }

                setTouchesFromItemValue(render, item, definedAction, newPage);

                renders.add(render);
            }
        ));

        newPage.setResource(renders);
        return newPage;
    }

    private void setTouchesFromItemValue(Renderable render, Object item,
        DefinedAction definedAction, Page<Renderable> page) {
        Map<String, String> touchesItemValue = Maps.newHashMap();

        if (definedAction.isHasTouch()) {
            definedAction.getTouches().forEach(definedTouch
                -> putTouchItemValue(touchesItemValue, definedTouch, render, page));
        }

        if (definedAction.isHasFieldTouch()) {
            definedAction.getFieldTouches().forEach((key, definedTouch)
                -> putTouchItemValue(touchesItemValue, definedTouch, render, page));
        }

        render.setTouchesItemValue(touchesItemValue);
    }

    private void putTouchItemValue(Map<String, String> touchesItemValue, DefinedTouch definedTouch,
        Object item, Page<Renderable> page) {
        String touchFragment = isNullOrEmpty(definedTouch.getTouchFragment())
            ? "touch_item_generic" : definedTouch.getTouchFragment();

        List<String> columns = newArrayList(page.getColumns());
        columns.remove("touchListValue");
        String value = getFragmentValue(item, touchFragment,
            (Context context) -> {
                context.setVariable("touch", definedTouch);
                context.setVariable("columns", columns);
                context.setVariable("titles", page.getTitles());
                context.setVariable("toggleWidget",
                    ofNullable(page.getExtra().get("toggleWidget")).orElse(newArrayList()));
                context.setVariable("labelValue", page.getExtra().get("labelValue"));
            });

        touchesItemValue.put(definedTouch.getId(), value);
    }

    private void setTouchListValue(Renderable render, Object item, DefinedAction definedAction,
        Page<Renderable> page, DefinedTouchAccess access) {
        String cellFragment = isNullOrEmpty(render.getTouchListCellFragment())
            ? "touch_column_cell" : render.getTouchListCellFragment();
        render.setTouchListValue(getFragmentValue(render, cellFragment, (Context context) -> {
                context.setVariable("defact", definedAction);
                context.setVariable("source", item);
                context.setVariable("access", access);
                context.setVariable("owners", ofNullable(page.getTouchOwnerIds()).orElse(newArrayList()));
            }));
    }

    private DefinedAction getDefinedAction(Class<? extends Renderable> viewObjectType,
        DefinedAccess definedAccess, String manageBaseUrl) {
        DefinedAction definedAction = new DefinedAction();
        Actionable actionable = viewObjectType.getAnnotation(Actionable.class);
        if (null != actionable) {
            boolean hasEdit = definedAccess.isHasRetrieve() && actionable.edit();
            boolean hasEditField = definedAccess.isHasRetrieve() && !isNullOrEmpty(actionable.editField());
            String editField = actionable.editField();
            boolean hasDelete = definedAccess.isHasDelete() && actionable.delete();
            boolean hasActionFragment = !isNullOrEmpty(actionable.fragment());
            boolean hasAppendFragment = !isNullOrEmpty(actionable.appendFragment());
            boolean hasAction = hasEdit || hasDelete || hasActionFragment || hasAppendFragment;
            String uniqueField = actionable.uniqueField();
            boolean hasCreate = definedAccess.isHasCreate() && actionable.create();
            boolean hasCreateFragment = !isNullOrEmpty(actionable.createFragment());
            boolean hasReadyFragment = !isNullOrEmpty(actionable.readyFragment());

            definedAction.setHasEdit(hasEdit);
            definedAction.setHasEditField(hasEditField);
            definedAction.setEditField(editField);
            definedAction.setHasDelete(hasDelete);
            definedAction.setHasActionFragment(hasActionFragment);
            definedAction.setActionFragment(actionable.fragment());
            definedAction.setHasAppendFragment(hasAppendFragment);
            definedAction.setAppendFragment(actionable.appendFragment());
            definedAction.setHasAction(hasAction);
            definedAction.setUniqueField(uniqueField);
            definedAction.setHasCreate(hasCreate);
            definedAction.setHasCreateFragment(hasCreateFragment);
            if (hasCreateFragment) {
                String createFragmentValue = getFragmentValue(viewObjectType, actionable.createFragment(),
                    (Context context) -> context.setVariable("access", definedAccess));
                definedAction.setCreateFragmentValue(createFragmentValue);
            }

            definedAction.setHasReadyFragment(hasReadyFragment);
            if (hasReadyFragment) {
                String readyFragmentValue = getFragmentValue(viewObjectType, actionable.readyFragment(),
                    (Context context) -> context.setVariable("access", definedAccess));
                definedAction.setReadyFragmentValue(readyFragmentValue);
            }

            // DefineTouch
            List<DefinedTouch> definedTouches = newArrayList();
            DefineTouch relation = viewObjectType.getAnnotation(DefineTouch.class);
            if (null != relation) {
                ofNullable(asDefinedTouch(relation, viewObjectType, manageBaseUrl)).ifPresent(definedTouches::add);
            }

            DefineTouch[] relations = actionable.relations();
            if (relations.length > 0) {
                Arrays.stream(relations).forEach(aRelation
                    -> ofNullable(asDefinedTouch(aRelation, viewObjectType, manageBaseUrl)).ifPresent(definedTouches::add));
            }

            List<DefinedTouch> defaultTouches = newArrayList();
            Map<String, DefinedTouch> fieldTouches = Maps.newHashMap();
            definedTouches.forEach(definedTouch -> {
                DefinedTouchAccess definedTouchAccess = definedAccess.getTouchAccess().get(definedTouch.getId());
                ofNullable(definedTouchAccess).ifPresent(access -> {
                    if (isNullOrEmpty(definedTouch.getActionField())) {
                        if (access.isHasIndex()) {
                            defaultTouches.add(definedTouch);
                        }
                    } else {
                        if (access.isHasIndex()) {
                            fieldTouches.put(definedTouch.getActionField(), definedTouch);
                        }
                    }
                });
            });

            /*
            Map<String, DefinedTouch> touchFields = definedTouches.stream()
                .filter(definedTouch -> !isNullOrEmpty(definedTouch.getActionField()))
                .collect(Collectors.toMap(DefinedTouch::getActionField, Function.identity()));
             */

            definedAction.setHasTouch(defaultTouches.size() > 0);
            definedAction.setTouches(defaultTouches);
            definedAction.setHasFieldTouch(fieldTouches.size() > 0);
            definedAction.setFieldTouches(fieldTouches);
        } else {
            definedAction.setUniqueField("id");
        }

        return definedAction;
    }

    private String getTouchId(DefineTouch defineTouch) {
        Class<? extends Renderable> touchViewType = defineTouch.view();
        checkArgument(hasTouchViewObjectValue(touchViewType),
            "DefineTouch view must be a sub class of Renderable");
        return getTouchId(touchViewType);
    }

    private String getTouchId(Class<? extends Renderable> viewObjectType) {
        return encodes(viewObjectType.getSimpleName());
    }

    private DefinedTouch asDefinedTouch(DefineTouch defineTouch,
        Class<? extends Renderable> viewObjectType, String manageBaseUrl) {
        String name = defineTouch.name();
        String actionField = defineTouch.actionField();
        boolean hasActionField = !isNullOrEmpty(actionField);
        checkArgument(!isNullOrEmpty(name) || hasActionField,
            "DefineTouch must define one of name or action on type %s",
            viewObjectType.getSimpleName());

        Class<? extends Renderable> touchViewType = defineTouch.view();

        DefinedTouch definedTouch = new DefinedTouch();
        definedTouch.setId(getTouchId(defineTouch));
        definedTouch.setName(getMessage(name, name));
        definedTouch.setActionField(actionField);
        definedTouch.setHasActionField(hasActionField);
        definedTouch.setTitleFragment(defineTouch.titleFragment());
        definedTouch.setCellFragment(defineTouch.cellFragment());
        definedTouch.setTouchFragment(defineTouch.touchFragment());
        definedTouch.setMaster(defineTouch.master());
        definedTouch.setSwitchable(defineTouch.switchable());

        PageInfo touchViewPageInfo = getPageInfo(touchViewType, manageBaseUrl);
        definedTouch.setTouchHref(touchViewPageInfo.getIndex());

        PageInfo viewObjectTypePageInfo = getPageInfo(viewObjectType, manageBaseUrl);
        definedTouch.setModule(viewObjectTypePageInfo.getModule());
        definedTouch.setModuleId(getTouchId(viewObjectType));

        String relationHref;
        if (definedTouch.isMaster()) {
            relationHref = touchViewPageInfo.getRelation();
        } else {
            relationHref = viewObjectTypePageInfo.getRelation();
        }

        definedTouch.setRelationHref(relationHref);

        return definedTouch;
    }

    private Renderable asRenderable(Class<? extends Renderable> viewObjectType,
        List<Field> defineFields, Object value, List<Field> valueFields,
        DefinedAction definedAction, boolean hasTouch, Object extraResource) {
        try {
            Renderable render = viewObjectType.newInstance();
            defineFields.forEach(field -> {
                DefineElement defineElement = field.getAnnotation(DefineElement.class);
                boolean hasDefineElement = null != defineElement;

                Touchable touchable = field.getAnnotation(Touchable.class);
                boolean isTouchable = hasTouch && null != touchable;

                Listable listable = field.getAnnotation(Listable.class);
                boolean isListable = !hasTouch && null != listable;

                if (isTouchable || isListable) {
                    String fieldName = field.getName();
                    Object aValue;

                    //value fragment
                    String fragment = null;
                    if (isTouchable) {
                        fragment = touchable.fragment();
                    }

                    if (isListable) {
                        fragment = listable.fragment();
                    }

                    if (isNullOrEmpty(fragment) && hasDefineElement) {
                        fragment = defineElement.fragment();
                    }

                    boolean hasFragment = !isNullOrEmpty(fragment);
                    if (!hasFragment && isRenderable(field.getType())) {
                        throw new IllegalArgumentException(
                            format("View object field must define fragment with @Listable or @Touchable %s.%s",
                                viewObjectType.getSimpleName(), fieldName));
                    }

                    //value from fragment
                    if (hasFragment) {
                        aValue = getFragmentValue(value, fragment,
                            (Context context) -> context.setVariable("extraResource", extraResource));
                        render.addFieldFragmentValue(fieldName, (String) aValue);
                    } else {
                        aValue = getFieldValueByName(value, valueFields, fieldName, null, field);

                        String dateStyle = null;
                        if (isTouchable) {
                            dateStyle = touchable.dateStyle();
                        }

                        if (isListable) {
                            dateStyle = listable.dateStyle();
                        }

                        if (isNullOrEmpty(dateStyle) && hasDefineElement) {
                            dateStyle = defineElement.dateStyle();
                        }

                        setFieldValue(field, render, aValue, dateStyle);
                    }

                    //action fragment
                    if (definedAction.isHasActionFragment()) {
                        String actionFragment = definedAction.getActionFragment();
                        if (!isNullOrEmpty(actionFragment)) {
                            render.setFragmentAction(true);
                            render.setAction(getFragmentValue(value, actionFragment,
                                (Context context) -> context.setVariable("defact", definedAction)));
                        }
                    }

                    //append action fragment
                    if (definedAction.isHasAppendFragment()) {
                        String appendActionFragment = definedAction.getAppendFragment();
                        if (!isNullOrEmpty(appendActionFragment)) {
                            render.setFragmentAppendAction(true);
                            render.setAppendAction(getFragmentValue(value, appendActionFragment,
                                (Context context) -> context.setVariable("defact", definedAction)));
                        }
                    }
                }

            });

            return render;
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn("initialize {} render fail: {}", viewObjectType.getSimpleName(), e.getMessage());
            Throwables.throwIfUnchecked(e);
        }

        return null;
    }

    private Object convertDate(Field defineField, Object aValue, String dateStyle) {
        if (isNullOrEmpty(dateStyle)) {
            return aValue;
        }

        if (defineField.getType().isAssignableFrom(String.class)
            && aValue instanceof Date) {
            aValue = DateTimeFormatter
                .ofPattern(dateStyle).format(LocalDateTime
                    .ofInstant(((Date) aValue).toInstant(), ZoneId.systemDefault()));
        }

        return aValue;
    }

    private List<DefinedElement> getDefinedElements(DefineType defineType,
        Class<? extends Renderable> viewObjectType, Object value, DataExchange dataExchange) {
        return getDefinedElements(defineType, viewObjectType, value, dataExchange, null);
    }

    private List<DefinedElement> getDefinedElements(DefineType defineType,
        Class<? extends Renderable> viewObjectType, Object value, DataExchange dataExchange, String namePrefix) {
        List<DefinedElement> elements = newArrayList();
        List<Field> valueFields = null;
        if (null != value) {
            valueFields = getFields(value.getClass());
        }

        List<Field> defineFields = getFields(viewObjectType);
        List<Field> finalValueFields = valueFields;
        defineFields.forEach(field -> {
            DefinedElement element = asDefinedElement(defineType, field,
                viewObjectType, value, finalValueFields, dataExchange, namePrefix);
            if (null != element) {

                // readonly or disabled behave
                if ((null != element.getReadonly() && element.getReadonly())
                    || (null != element.getDisabled() && element.getDisabled())) {

                    DefinedElement hiddenEl = element.clones();
                    hiddenEl.setId(element.getId());
                    hiddenEl.setName(element.getName());
                    hiddenEl.setDisabled(null);
                    hiddenEl.setReadonly(null);
                    addHiddenBehave(hiddenEl);
                    elements.add(hiddenEl);

                    element.setId(format(UNCHANGEABLE_MARK, element.getId()));
                    element.setName(format(UNCHANGEABLE_MARK, element.getName()));
                }

                elements.add(element);
            }
        });

        return elements;
    }

    @SuppressWarnings("unchecked")
    private DefinedElement asDefinedElement(DefineType defineType, Field field,
        Class<? extends Renderable> viewObjectType, Object value, List<Field> valueFields,
        DataExchange dataExchange, String namePrefix) {
        String  voName = viewObjectType.getSimpleName();
        String fieldName = field.getName();

        boolean isRenderable = isRenderable(field.getType());
        boolean hasDataExchange = null != dataExchange && dataExchange.has(fieldName);
        Object extraResource = hasDataExchange ? dataExchange.get(fieldName).apply(value) : null;

        DefineElement defineElement = field.getAnnotation(DefineElement.class);
        boolean hasDefineElement = null != defineElement;

        Editable editable = field.getAnnotation(Editable.class);
        boolean isEditable = (CREATABLE == defineType || UPDATABLE == defineType) && null != editable;

        Creatable creatable = field.getAnnotation(Creatable.class);
        boolean isCreatable = CREATABLE == defineType && null != creatable;

        Updatable updatable = field.getAnnotation(Updatable.class);
        boolean isUpdatable = UPDATABLE == defineType && null != updatable;

        // create || update
        if (CREATABLE == defineType || UPDATABLE == defineType) {
            // has not define both create and update
            if (null == editable) {
                switch (defineType) {
                    case UPDATABLE:
                        if (null == updatable) {
                            return null;
                        }
                        break;
                    case CREATABLE:
                        if (null == creatable) {
                            return null;
                        }
                        break;
                }
            }
        }

        Searchable searchable = field.getAnnotation(Searchable.class);
        boolean isSearchable = SEARCHABLE == defineType && null != searchable;

        if (isEditable || isCreatable || isUpdatable || isSearchable) {

            DefinedElement element = new DefinedElement();

            //fragment
            String fragment = null;
            if (isEditable) {
                fragment = editable.fragment();
            }

            if (isCreatable) {
                fragment = creatable.fragment();
            }

            if (isUpdatable) {
                fragment = updatable.fragment();
            }

            if (isSearchable) {
                fragment = searchable.fragment();
            }

            if (isNullOrEmpty(fragment) && hasDefineElement) {
                fragment = defineElement.fragment();
            }

            if (!isNullOrEmpty(fragment)) {
                String fragmentValue = getFragmentValue(value, fragment,
                    (Context context) -> context.setVariable("extraResource", extraResource));
                element.setValue(fragmentValue);
                element.setFragmentValue(true);
                return element;
            }

            //tag
            HtmlTag tag = UNDEFINED;
            if (isEditable) {
                tag = editable.tag();
            }

            if (isCreatable) {
                tag = creatable.tag();
            }

            if (isUpdatable) {
                tag = updatable.tag();
            }

            if (isSearchable) {
                tag = searchable.tag();
            }

            if (UNDEFINED == tag && hasDefineElement) {
                tag = defineElement.tag();
            }

            //View object filed only supports HtmlTag.VIEW_OBJECT
            if (isRenderable) {
                tag = HtmlTag.VIEW_OBJECT;
            }

            checkArgument(tag != UNDEFINED,
                "undefined field tag %s.%s", voName, fieldName);
            element.setTag(tag);
            element.setTagName(tag.toString());

            boolean hasNamePrefix = !isNullOrEmpty(namePrefix);
            //id
            String id = null;
            if (isEditable) {
                id = editable.id();
            }

            if (isCreatable) {
                id = creatable.id();
            }

            if (isUpdatable) {
                id = updatable.id();
            }

            if (isSearchable) {
                id = searchable.id();
            }

            if (isNullOrEmpty(id) && hasDefineElement) {
                id = defineElement.id();
            }

            if (isNullOrEmpty(id)) {
                //auto generator "el_{fieldName}"
                id = format(hasNamePrefix ? ELEMENT_ID_WITH_PREFIX_FORMAT : ELEMENT_ID_FORMAT, fieldName);
            }

            if (isSearchable) {
                id = format(SEARCH_ELEMENT_ID_FORMAT, id);
            }
            element.setId(id);

            //name
            String name = null;
            if (isEditable) {
                name = editable.name();
            }

            if (isCreatable) {
                name = creatable.name();
            }

            if (isUpdatable) {
                name = updatable.name();
            }

            if (isSearchable) {
                name = searchable.name();
            }

            if (isNullOrEmpty(name) && hasDefineElement) {
                name = defineElement.name();
            }

            //auto generator "{fieldName}"
            if (isNullOrEmpty(name)) {
                name = fieldName;
            }
            name = hasNamePrefix ? format(ELEMENT_NAME_WITH_PREFIX_FORMAT, namePrefix, name) : name;
            element.setName(name);

            //type
            InputType inputType = InputType.UNDEFINED;
            if (isEditable) {
                inputType = editable.type();
            }

            if (isCreatable) {
                inputType = creatable.type();
            }

            if (isUpdatable) {
                inputType = updatable.type();
            }

            if (isSearchable) {
                inputType = searchable.type();
            }

            if (InputType.UNDEFINED == inputType && hasDefineElement) {
                inputType = defineElement.type();
            }

            if (tag == INPUT) {
                checkArgument(InputType.UNDEFINED != inputType,
                    "undefined field input type %s.%s", voName, fieldName);
            }
            element.setType(inputType);
            element.setTypeName(inputType.toString());


            //title
            String title = null;
            if (isEditable) {
                title = editable.title();
            }

            if (isCreatable) {
                title = creatable.title();
            }

            if (isUpdatable) {
                title = updatable.title();
            }

            if (isSearchable) {
                title = searchable.title();
            }

            if (isNullOrEmpty(title) && hasDefineElement) {
                title = defineElement.title();
            }
            element.setTitle(title);

            //clazz
            String clazz = null;
            if (isEditable) {
                clazz = editable.clazz();
            }

            if (isCreatable) {
                clazz = creatable.clazz();
            }

            if (isUpdatable) {
                clazz = updatable.clazz();
            }

            if (isSearchable) {
                clazz = searchable.clazz();
            }

            if (isNullOrEmpty(clazz) && hasDefineElement) {
                clazz = defineElement.clazz();
            }
            element.setClazz(clazz);

            //style
            String style = null;
            if (isEditable) {
                style = editable.style();
            }

            if (isCreatable) {
                style = creatable.style();
            }

            if (isUpdatable) {
                style = updatable.style();
            }

            if (isSearchable) {
                style = searchable.style();
            }

            if (isNullOrEmpty(style) && hasDefineElement) {
                style = defineElement.style();
            }
            element.setStyle(style);

            //readonly
            Boolean readonly = null;
            if (isEditable) {
                readonly = editable.readonly();
            }

            if (isCreatable) {
                readonly = creatable.readonly();
            }

            if (isUpdatable) {
                readonly = updatable.readonly();
            }

            if (null == readonly && hasDefineElement) {
                readonly = defineElement.readonly();
            }
            element.setReadonly(readonly);

            //disabled
            Boolean disabled = null;
            if (isEditable) {
                disabled = editable.disabled();
            }

            if (isCreatable) {
                disabled = creatable.disabled();
            }

            if (isUpdatable) {
                disabled = updatable.disabled();
            }

            if (null == disabled && hasDefineElement) {
                disabled = defineElement.disabled();
            }
            element.setDisabled(disabled);

            //label
            String label = null;
            if (isEditable) {
                label = editable.value();
            }

            if (isCreatable) {
                label = creatable.value();
            }

            if (isUpdatable) {
                label = updatable.value();
            }

            if (isSearchable) {
                label = searchable.value();
            }

            if (isNullOrEmpty(label) && hasDefineElement) {
                label = defineElement.value();
            }
            element.setLabel(getMessage(label, label));

            //placeholder
            String placeholder = null;
            if (isEditable) {
                placeholder = editable.placeholder();
            }

            if (isCreatable) {
                placeholder = creatable.placeholder();
            }

            if (isUpdatable) {
                placeholder = updatable.placeholder();
            }

            if (isSearchable) {
                placeholder = searchable.placeholder();
            }

            if (isNullOrEmpty(placeholder) && hasDefineElement) {
                placeholder = defineElement.placeholder();
            }

            //auto generator if none, same as label
            if (isNullOrEmpty(placeholder)) {
                placeholder = element.getLabel() + " ..";
            }
            element.setPlaceholder(placeholder);

            //attributes
            String[] attributes = null;

            if (isEditable) {
                attributes = editable.attributes();
            }

            if (isCreatable) {
                attributes = creatable.attributes();
            }

            if (isUpdatable) {
                attributes = updatable.attributes();
            }

            if (isSearchable) {
                attributes = searchable.attributes();
            }

            if (attributes.length < 1 && hasDefineElement) {
                attributes = defineElement.attributes();
            }

            List<Pair<String, String>> elementAttributes = newArrayList();
            if (attributes.length > 0) {
                for (String attribute : attributes) {
                    List<String> attr = Splitter.on("=").splitToList(attribute);
                    String attrName = attr.get(0);
                    String attrValue = attr.size() > 1 ? attr.get(1) : null;
                    elementAttributes.add(Pair.of(attrName, attrValue));
                }

            }
            element.setAttributes(elementAttributes);

            //value
            Object fieldValue = null;

            // View Object Field
            if (isRenderable) {
                Class<? extends Renderable> fieldViewObject = (Class<? extends Renderable>) field.getType();
                if (hasDataExchange) {
                    fieldValue = getDefinedElements(defineType, fieldViewObject, extraResource, dataExchange, fieldName);
                } else {
                    final Object[] aFieldValue = {null};

                    getFieldByName(valueFields, fieldName).ifPresent(theValueField -> {
                        Object aValue = getFieldValue(theValueField, value);
                        if (null != aValue) {
                            aFieldValue[0] = getDefinedElements(defineType, fieldViewObject, aValue, dataExchange, fieldName);
                        }
                    });

                    fieldValue = aFieldValue[0];
                }
            }

            if (null == fieldValue && hasDataExchange) {
                fieldValue = extraResource;
            }

            if (null == fieldValue && !hasDataExchange && null != valueFields && valueFields.size() > 0) {

                //date style
                String dateStyle = null;
                if (isEditable) {
                    dateStyle = editable.dateStyle();
                }

                if (isCreatable) {
                    dateStyle = creatable.dateStyle();
                }

                if (isUpdatable) {
                    dateStyle = updatable.dateStyle();
                }

                if (isSearchable) {
                    dateStyle = searchable.dateStyle();
                }

                if (isNullOrEmpty(dateStyle) && hasDefineElement) {
                    dateStyle = defineElement.dateStyle();
                }

                fieldValue = getFieldValueByName(value, valueFields, fieldName, dateStyle, field);
            }
            element.setValue(fieldValue);

            //default option
            String defaultOption = null;
            if (isEditable) {
                defaultOption = editable.defaultOption();
            }

            if (isCreatable) {
                defaultOption = creatable.defaultOption();
            }

            if (isUpdatable) {
                defaultOption = updatable.defaultOption();
            }

            if (isSearchable) {
                defaultOption = searchable.defaultOption();
            }

            if (isNullOrEmpty(defaultOption) && hasDefineElement) {
                defaultOption = defineElement.defaultOption();
            }

            //options
            Class<? extends Enum> optionsClass = null;
            if (isEditable) {
                optionsClass = editable.options();
            }

            if (isCreatable) {
                optionsClass = creatable.options();
            }

            if (isUpdatable) {
                optionsClass = updatable.options();
            }

            if (isSearchable) {
                optionsClass = searchable.options();
            }

            if (!hasOptionsValue(optionsClass) && hasDefineElement) {
                optionsClass = defineElement.options();
            }

            if (hasOptionsValue(optionsClass)) {

                if (null == element.getValue() && !isNullOrEmpty(defaultOption)) {
                    element.setValue(defaultOption);
                }

                List<DefinedOption> definedOptions = (List<DefinedOption>) EnumSet.allOf(optionsClass).stream()
                    .map(item -> {
                        OptionValue option = (OptionValue) item;
                        return DefinedOption.of(getMessage(option.getLabel(), option.getLabel()),
                            option.getValue(), Objects.equals(option.stringValue(), String.valueOf(element.getValue())));
                    })
                    .collect(Collectors.toList());

                element.setOptions(definedOptions);
            } else {
                checkArgument(Enum.class == optionsClass,
                    "options enum class must implements OptionValue interface %s.%s", voName, fieldName);
            }

            /* additional behaves */

            // hidden behave
            if ((isEditable && editable.hidden())
                || (isCreatable && creatable.hidden())
                || (isUpdatable && updatable.hidden())) {
                addHiddenBehave(element);
            }

            return element;
        }

        return null;
    }

    private void addHiddenBehave(DefinedElement element) {
        if (element.getTag() == INPUT) {
            element.setType(InputType.HIDDEN);
        } else {
            String aStyle = isNullOrEmpty(element.getStyle()) ? HIDDEN_STYLE : (element.getStyle() + HIDDEN_STYLE);
            element.setStyle(aStyle);
        }

        element.setHidden(true);
    }

    private boolean isRenderable(Class<?> targetClazz) {
        return Renderable.class.isAssignableFrom(targetClazz);
    }

    private boolean hasOptionsValue(Class<? extends Enum> optionsClass) {
        return newArrayList(optionsClass.getInterfaces()).contains(OptionValue.class);
    }

    private boolean hasTouchViewObjectValue(Class<? extends Renderable> targetClazz) {
        return Renderable.class != targetClazz && isRenderable(targetClazz);
    }

    private <T> void setFieldValue(T entity, List<Field> entityFields,
        String entityFieldName, Object entityFieldValue, String dateStyle) {
        getFieldByName(entityFields, entityFieldName)
            .ifPresent(entityField -> setFieldValue(entityField, entity, entityFieldValue, dateStyle))
        ;
    }

    private void setFieldValue(Field field, Object valueObj, Object aValue, String dateStyle) {
        try {
            field.setAccessible(true);
            field.set(valueObj, convertDate(field, aValue, dateStyle));
        } catch (IllegalAccessException e) {
            log.warn("set field value fail: {}.{} {}",
                valueObj.getClass().getSimpleName(), field.getName(), e.getMessage());
            Throwables.throwIfUnchecked(e);
        }
    }

    private Object getFieldValue(List<Field> fields, Object valueObject, String fieldName) {
        final Object[] value = {null};
        getFieldByName(fields, fieldName)
            .ifPresent(entityField -> value[0] = getFieldValue(entityField, valueObject))
        ;

        return value[0];
    }

    private Object getFieldValue(Field field, Object valueObj) {
        try {
            field.setAccessible(true);
            return field.get(valueObj);
        } catch (IllegalAccessException e) {
            log.warn("value object {}.{} is not present", valueObj.getClass().getSimpleName(), field.getName());
            Throwables.throwIfUnchecked(e);
        }

        return null;
    }

    private Object getFieldValueByName(Object valueObj, List<Field> fields,
        String fieldName, String dateStyle, Field defineField) {
        Optional<Field> valueField = getFieldByName(fields, fieldName);
        if (valueField.isPresent()) {
            try {
                valueField.get().setAccessible(true);
                Object fieldValue = valueField.get().get(valueObj);

                if (!isNullOrEmpty(dateStyle)) {
                    fieldValue = convertDate(defineField, fieldValue, dateStyle);
                }

                return fieldValue;
            } catch (IllegalAccessException e) {
                log.warn("value object {}.{} is not present", valueObj.getClass().getSimpleName(), fieldName);
                Throwables.throwIfUnchecked(e);
            }
        }

        return null;
    }

    private Optional<Field> getFieldByName(List<Field> fields, String fieldName) {
        return fields
            .stream()
            .filter(aField -> fieldName.equals(aField.getName()))
            .findFirst()
            ;
    }

    private String getFragmentValue(Object value, String fragment) {
        return getFragmentValue(value, fragment, null);
    }

    private String getFragmentValue(Object value, String fragment, Consumer<Context> consumer) {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("item", value);

        if (null != consumer) {
            consumer.accept(context);
        }

        return fragmentTemplateEngine.process(fragment, context);
    }

    @Override
    public DefinedAccess getDefinedAccess(Class<? extends Renderable> viewObjectType,
        boolean denyAllIfNoneDefine, Predicate<String> predicate) {
        Actionable actionable = viewObjectType.getAnnotation(Actionable.class);
        Accessable accessable = viewObjectType.getAnnotation(Accessable.class);

        List<DefineTouch> touches = Lists.newArrayList();
        DefineTouch relation = viewObjectType.getAnnotation(DefineTouch.class);
        ofNullable(relation).ifPresent(touches::add);

        if (null != actionable) {
            DefineTouch[] relations = actionable.relations();
            touches.addAll(Arrays.asList(relations));
        }

        boolean hasAccessDefine = null != accessable;
        boolean hasActionableDefine = null != actionable && touches.size() > 0;

        if (!hasAccessDefine && !hasActionableDefine) {
            return denyAllIfNoneDefine ? DefinedAccess.denyAll() : DefinedAccess.allowAll();
        }

        DefinedAccess definedAccess = new DefinedAccess();

        if (hasAccessDefine) {
            definedAccess.setHasCreate(predicate.test(accessable.create()));
            definedAccess.setHasRetrieve(predicate.test(accessable.retrieve()));
            definedAccess.setHasUpdate(predicate.test(accessable.update()));
            definedAccess.setHasDelete(predicate.test(accessable.delete()));
        }

        if (hasActionableDefine) {
            Map<String, DefinedTouchAccess> touchAccess = touches.stream()
                .map(touch -> asDefinedTouchAccess(touch, predicate))
                .collect(Collectors.toMap(DefinedTouchAccess::getId, Function.identity()))
                ;

            definedAccess.setTouchAccess(touchAccess);
        }

        return definedAccess;
    }

    @Override
    public <T> SaveEntity<T> getSaveEntity(Renderable renderable, T entity) {
        List<Field> fields = getFields(renderable.getClass());
        List<Field> entityFields = getFields(entity.getClass());

        Actionable actionable = renderable.getClass().getAnnotation(Actionable.class);
        String primaryKey = null == actionable ? "id" : actionable.uniqueField();

        final boolean isCreate = null == getFieldValue(fields, renderable, primaryKey);
        fields.forEach(field -> setSaveEntity(isCreate, field, renderable, entity, entityFields));

        Date now = new Date();
        if (isCreate) {
            setFieldValue(entity, entityFields, FIELD_CREATE_TIME, now, null);
        }
        setFieldValue(entity, entityFields, FIELD_LAST_MODIFY_TIME, now, null);

        return SaveEntity.of(isCreate, primaryKey, entity);
    }

    @Override
    public SearchEntity getSearchEntity(Renderable condition) {
        List<Field> fields = getFields(condition.getClass());

        Actionable actionable = condition.getClass().getAnnotation(Actionable.class);
        String primaryKey = null == actionable ? "id" : actionable.uniqueField();

        List<SearchField> searchFields = Lists.newArrayList();
        fields.forEach(field -> asSearchField(field, condition, searchFields));
        return SearchEntity.of(primaryKey, searchFields);
    }

    private void asSearchField(Field field, Renderable condition, List<SearchField> searchFields) {
        Searchable searchable = field.getAnnotation(Searchable.class);
        boolean isSearchable = null != searchable;

        if (isSearchable) {
            SearchField searchField = new SearchField();
            searchField.setConditionTemplate(searchable.condition());
            searchField.setSide(searchable.likeSide());
            searchField.setName(field.getName());

            Object aValue = getFieldValue(field, condition);
            searchField.setValue(aValue);

            searchFields.add(searchField);
        }
    }

    private <T> void setSaveEntity(boolean isCreate, Field field, Renderable renderable,
        T entity, List<Field> entityFields) {
        String fieldName = field.getName();

        Editable editable = field.getAnnotation(Editable.class);
        boolean isEditable = null != editable;

        Creatable creatable = field.getAnnotation(Creatable.class);
        boolean isCreateable = isCreate && null != creatable;

        Updatable updatable = field.getAnnotation(Updatable.class);
        boolean isUpdatable = !isCreate && null != updatable;

        DefineElement defineElement = field.getAnnotation(DefineElement.class);
        boolean hasDefineElement = null != defineElement;

        Saveable saveable = field.getAnnotation(Saveable.class);
        boolean isSaveable = null != saveable;

        if (isEditable || isCreateable || isUpdatable || isSaveable) {

            String dateStyle = null;
            if (isEditable) {
                dateStyle = editable.dateStyle();
            }

            if (isCreateable) {
                dateStyle = creatable.dateStyle();
            }

            if (isUpdatable) {
                dateStyle = updatable.dateStyle();
            }

            if (isNullOrEmpty(dateStyle) && hasDefineElement) {
                dateStyle = defineElement.dateStyle();
            }

            Object aValue = getFieldValue(field, renderable);
            setFieldValue(entity, entityFields, fieldName, aValue, dateStyle);
        }
    }

    private DefinedTouchAccess asDefinedTouchAccess(DefineTouch defineTouch, Predicate<String> predicate) {
        TouchAccessable touchAccessable = defineTouch.accessable();
        boolean hasIndex = predicate.test(touchAccessable.index());
        boolean hasCreate = predicate.test(touchAccessable.create());
        boolean hasDelete = predicate.test(touchAccessable.delete());
        return DefinedTouchAccess.of(getTouchId(defineTouch), hasIndex, hasCreate, hasDelete);
    }

    private static final String TITLE_FORMAT = "render.%s.module.title";
    private static final String INDEX_FORMAT = "%s%s/index";            // /manage/role/index
    private static final String RELATION_FORMAT = "%s%s/relation";      // /manage/role/relation
    private static final String LIST_FORMAT = "%s%ss";                  // /manage/roles
    private static final String RETRIEVE_FORMAT = "%s%s/{0}";           // /manage/role/100
    private static final String CREATE_FORMAT = "%s%s";                 // /manage/role
    private static final String UPDATE_FORMAT = "%s%s";                 // /manage/role
    private static final String DELETE_FORMAT = "%s%s/{0}";             // /manage/role/100
    private static final String PAGE_ID_FORMAT = "%s_%s";               // role_1514540850329
    private static final String SEARCH_ID_FORMAT = "%s_search_%s";      // role_search_1514540850329
    private static final String CONTENT_ID_FORMAT = "%s_content_%s";    // role_content_1514540850329
    private static final String EDIT_ID_FORMAT = "%s_edit_item";        // role_edit_item

    @Override
    public <T extends Renderable> PageInfo getPageInfo(Class<T> viewObjectType, String manageBaseUrl) {
        PageInfo pageInfo = new PageInfo();
        String moduleName = viewObjectType.getSimpleName().toLowerCase();
        moduleName = moduleName.substring(0, moduleName.length() - 2);

        pageInfo.setTitle(getMessage(format(TITLE_FORMAT, moduleName), null));

        long suffixId = System.currentTimeMillis();
        pageInfo.setPageId(format(PAGE_ID_FORMAT, moduleName, suffixId));
        pageInfo.setModule(moduleName);
        pageInfo.setSearchId(format(SEARCH_ID_FORMAT, moduleName, suffixId));
        pageInfo.setContentId(format(CONTENT_ID_FORMAT, moduleName, suffixId));
        pageInfo.setEditId(format(EDIT_ID_FORMAT, moduleName));

        pageInfo.setIndex(format(INDEX_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setRelation(format(RELATION_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setList(format(LIST_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setRetrieve(format(RETRIEVE_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setCreate(format(CREATE_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setUpdate(format(UPDATE_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setDelete(format(DELETE_FORMAT, manageBaseUrl, moduleName));

        return pageInfo;
    }

    @Override
    public String getMessage(String code, String defaultMessage, Object... args) {
        return getMessage(code, defaultMessage, null, args);
    }

    @Override
    public String getMessage(String code, String defaultMessage, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage,
            null != locale ? locale : LocaleContextHolder.getLocale());
    }
}
