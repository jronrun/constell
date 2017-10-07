package com.benayn.constell.service.server.component;

import static com.benayn.constell.service.server.respond.DefineType.EDITABLE;
import static com.benayn.constell.service.server.respond.DefineType.SEARCHABLE;
import static com.benayn.constell.service.server.respond.TagName.INPUT;
import static com.benayn.constell.service.server.respond.TagName.UNDEFINED;
import static com.benayn.constell.service.util.LZString.encodes;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.DefineType;
import com.benayn.constell.service.server.respond.DefinedAction;
import com.benayn.constell.service.server.respond.DefinedElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.InputType;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.PageInfo;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.TagName;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
    private static final String HIDDEN_STYLE = "display:none;";

    public ViewObjectResolverBean(MessageSource messageSource, TemplateEngine fragmentTemplateEngine) {
        this.messageSource = messageSource;
        this.fragmentTemplateEngine = fragmentTemplateEngine;
    }

    @SuppressWarnings("WeakerAccess")
    @Cacheable(value = "defined_pages", sync = true, key = "#viewObjectType.simpleName")
    public List<Field> getFields(Class<?> viewObjectType) {
        return Lists.newArrayList(viewObjectType.getDeclaredFields());
    }

    @Override
    public List<DefinedElement> getDefinedSearch(Class<? extends Renderable> viewObjectType, Object value) {
        return getDefinedElements(SEARCHABLE, viewObjectType, value);
    }

    @Override
    public List<DefinedElement> getDefinedEdit(Class<? extends Renderable> viewObjectType, Object value) {
        return getDefinedElements(EDITABLE, viewObjectType, value);
    }

    @Override
    public Page<Renderable> getDefinedPage(Class<? extends Renderable> viewObjectType, Page<?> page) {
        List<?> items = page.getResource();
        checkNotNull(items, "page resource cannot be null");

        List<Renderable> renders = Lists.newArrayList();

        final boolean[] isDefinedUniqueField = {false};
        List<Field> defineFields = getFields(viewObjectType);
        Page<Renderable> newPage = page.cloneButResource();

        DefinedAction definedAction = getDefinedAction(viewObjectType);
        newPage.addExtra("definedAction", definedAction);
        defineFields.forEach(field -> {
            DefineElement defineElement = field.getAnnotation(DefineElement.class);
            boolean hasDefineElement = null != defineElement;

            Listable listable = field.getAnnotation(Listable.class);
            if (null != listable) {
                String title = listable.value();
                if (isNullOrEmpty(title) && hasDefineElement) {
                    title = defineElement.value();
                }

                if (!isNullOrEmpty(definedAction.getUniqueField())
                    && field.getName().equals(definedAction.getUniqueField())) {
                    isDefinedUniqueField[0] = true;
                }

                checkArgument(!isNullOrEmpty(title), "undefined listable column title %s.%s",
                    viewObjectType.getSimpleName(), field.getName());
                newPage.addColumn(field.getName());
                newPage.addTitle(field.getName(), getMessage(title, title));
            }
        });

        checkArgument(isDefinedUniqueField[0],
            "undefined actionable unique field on type %s, unique field: %s",
            viewObjectType.getSimpleName(), definedAction.getUniqueField());

        List<Field> itemFields = null;
        if (items.size() > 0) {
            itemFields = getFields(items.get(0).getClass());
        }

        List<Field> finalItemFields = itemFields;
        items.forEach(item -> {
            Renderable render = asRenderable(viewObjectType, defineFields, item, finalItemFields, definedAction);
            if (null != render) {
                renders.add(render);
            }
        });

        newPage.setResource(renders);
        return newPage;
    }

    private DefinedAction getDefinedAction(Class<? extends Renderable> viewObjectType) {
        DefinedAction definedAction = new DefinedAction();
        Actionable actionable = viewObjectType.getAnnotation(Actionable.class);
        if (null != actionable) {
            boolean hasEdit = actionable.edit();
            boolean hasEditField = !isNullOrEmpty(actionable.editField());
            String editField = actionable.editField();
            boolean hasDelete = actionable.delete();
            boolean hasActionFragment = !isNullOrEmpty(actionable.fragment());
            boolean hasAction = hasEdit || hasDelete || hasActionFragment;
            String uniqueField = actionable.uniqueField();
            boolean hasCreate = actionable.create();
            boolean hasCreateFragment = !isNullOrEmpty(actionable.createFragment());

            definedAction.setHasEdit(hasEdit);
            definedAction.setHasEditField(hasEditField);
            definedAction.setEditField(editField);
            definedAction.setHasDelete(hasDelete);
            definedAction.setHasActionFragment(hasActionFragment);
            definedAction.setActionFragment(actionable.fragment());
            definedAction.setHasAction(hasAction);
            definedAction.setUniqueField(uniqueField);
            definedAction.setHasCreate(hasCreate);
            definedAction.setHasCreateFragment(hasCreateFragment);
            if (hasCreateFragment) {
                String createFragmentValue = getFragmentValue(viewObjectType, actionable.createFragment());
                definedAction.setCreateFragmentValue(createFragmentValue);
            }
        }

        return definedAction;
    }

    private Renderable asRenderable(Class<? extends Renderable> viewObjectType,
        List<Field> defineFields, Object value, List<Field> valueFields,
        DefinedAction definedAction) {
        try {
            Renderable render = viewObjectType.newInstance();
            defineFields.forEach(field -> {
                DefineElement defineElement = field.getAnnotation(DefineElement.class);
                boolean hasDefineElement = null != defineElement;

                Listable listable = field.getAnnotation(Listable.class);
                if (null != listable) {
                    String fieldName = field.getName();
                    Object aValue;

                    //value fragment
                    String fragment;
                    fragment = listable.fragment();
                    if (isNullOrEmpty(fragment) && hasDefineElement) {
                        fragment = defineElement.fragment();
                    }

                    //value from fragment
                    if (!isNullOrEmpty(fragment)) {
                        aValue = getFragmentValue(value, fragment);
                        render.addFieldFragmentValue(fieldName, (String) aValue);
                    } else {
                        aValue = getFieldValueByName(value, valueFields, fieldName);

                        String dateStyle = listable.dateStyle();
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
                            render.setAction(getFragmentValue(value, actionFragment));
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
        Class<? extends Renderable> viewObjectType, Object value) {
        List<DefinedElement> elements = Lists.newArrayList();
        List<Field> valueFields = null;
        if (null != value) {
            valueFields = getFields(value.getClass());
        }

        List<Field> defineFields = getFields(viewObjectType);
        List<Field> finalValueFields = valueFields;
        defineFields.forEach(field -> {
            DefinedElement element = asDefinedElement(defineType, field, viewObjectType, value, finalValueFields);
            if (null != element) {
                elements.add(element);
            }
        });

        return elements;
    }

    private DefinedElement asDefinedElement(DefineType defineType, Field field,
        Class<? extends Renderable> viewObjectType, Object value, List<Field> valueFields) {
        String  voName = viewObjectType.getSimpleName();
        String fieldName = field.getName();

        DefineElement defineElement = field.getAnnotation(DefineElement.class);
        boolean hasDefineElement = null != defineElement;

        Editable editable = field.getAnnotation(Editable.class);
        boolean isEditable = EDITABLE == defineType && null != editable;

        Searchable searchable = field.getAnnotation(Searchable.class);
        boolean isSearchable = SEARCHABLE == defineType && null != searchable;

        if (isEditable || isSearchable) {
            DefinedElement element = new DefinedElement();

            //fragment
            String fragment = null;
            if (isEditable) {
                fragment = editable.fragment();
            }

            if (isSearchable) {
                fragment = searchable.fragment();
            }

            if (isNullOrEmpty(fragment) && hasDefineElement) {
                fragment = defineElement.fragment();
            }

            if (!isNullOrEmpty(fragment)) {
                String fragmentValue = getFragmentValue(value, fragment);
                element.setValue(fragmentValue);
                element.setFragmentValue(true);
                return element;
            }

            //tag
            TagName tag = UNDEFINED;
            if (isEditable) {
                tag = editable.tag();
            }

            if (isSearchable) {
                tag = searchable.tag();
            }

            if (UNDEFINED == tag && hasDefineElement) {
                tag = defineElement.tag();
            }

            checkArgument(tag != UNDEFINED,
                "undefined field tag %s.%s", voName, fieldName);
            element.setTag(tag);

            //id
            String id = null;
            if (isEditable) {
                id = editable.id();
            }

            if (isSearchable) {
                id = searchable.id();
            }

            if (isNullOrEmpty(id) && hasDefineElement) {
                id = defineElement.id();
            }

            if (isNullOrEmpty(id)) {
                //auto generator "el_{fieldName}"
                id = format(ELEMENT_ID_FORMAT, fieldName);
            }
            element.setId(id);

            //name
            String name = null;
            if (isEditable) {
                name = editable.name();
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
            element.setName(name);

            //type
            InputType inputType = InputType.UNDEFINED;
            if (isEditable) {
                inputType = editable.type();
            }

            if (isSearchable) {
                inputType = searchable.type();
            }

            if (InputType.UNDEFINED == inputType && hasDefineElement) {
                inputType = defineElement.type();
            }

            checkArgument(tag == INPUT && InputType.UNDEFINED != inputType,
                "undefined field input type %s.%s", voName, fieldName);
            element.setType(inputType);


            //title
            String title = null;
            if (isEditable) {
                title = editable.title();
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

            if (isSearchable) {
                style = searchable.style();
            }

            if (isNullOrEmpty(style) && hasDefineElement) {
                style = defineElement.style();
            }
            element.setStyle(style);

            //label
            String label = null;
            if (isEditable) {
                label = editable.value();
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

            if (isSearchable) {
                attributes = searchable.attributes();
            }

            if (attributes.length < 1 && hasDefineElement) {
                attributes = defineElement.attributes();
            }

            Map<String, String> attrMap = Maps.newHashMap();
            if (attributes.length > 0) {
                for (String attribute : attributes) {
                    attrMap.putAll(Splitter.on(",").withKeyValueSeparator("=").split(attribute));
                }

            }
            element.setAttributes(encodes(attrMap));

            //editable hidden behave
            if (isEditable && editable.hidden()) {
                //noinspection ConstantConditions
                if (tag == INPUT) {
                    element.setType(InputType.HIDDEN);
                } else {
                    String aStyle = isNullOrEmpty(element.getStyle())
                        ? HIDDEN_STYLE : (element.getStyle() + HIDDEN_STYLE);
                    element.setStyle(aStyle);
                }
            }

            //value
            if (null != valueFields && valueFields.size() > 0) {
                element.setValue(getFieldValueByName(value, valueFields, fieldName));
            }

            return element;
        }

        return null;
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

    private Object getFieldValueByName(Object valueObj, List<Field> fields, String fieldName) {
        Optional<Field> valueField = getFieldByName(fields, fieldName);
        if (valueField.isPresent()) {
            try {
                valueField.get().setAccessible(true);
                return valueField.get().get(valueObj);
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
        Context context = new Context();
        context.setVariable("item", value);

        return fragmentTemplateEngine.process(fragment, context);
    }

    private static final String TITLE_FORMAT = "render.%s.module.title";
    private static final String INDEX_FORMAT = "%s%s/index";
    private static final String LIST_FORMAT = "%s%ss";
    private static final String RETRIEVE_FORMAT = "%s%s/{0}";
    private static final String CREATE_FORMAT = "%s%s";
    private static final String UPDATE_FORMAT = "%s%s";
    private static final String DELETE_FORMAT = "%s%s/{0}";
    private static final String PAGE_ID_FORMAT = "%s_%s";

    @Override
    public <T extends Renderable> PageInfo getPageInfo(Class<T> viewObjectType, String manageBaseUrl) {
        PageInfo pageInfo = new PageInfo();
        String moduleName = viewObjectType.getSimpleName().toLowerCase();
        moduleName = moduleName.substring(0, moduleName.length() - 2);

        pageInfo.setTitle(getMessage(format(TITLE_FORMAT, moduleName), null));
        pageInfo.setPageId(format(PAGE_ID_FORMAT, moduleName, System.currentTimeMillis()));

        pageInfo.setIndex(format(INDEX_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setList(format(LIST_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setRetrieve(format(RETRIEVE_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setCreate(format(CREATE_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setUpdate(format(UPDATE_FORMAT, manageBaseUrl, moduleName));
        pageInfo.setDelete(format(DELETE_FORMAT, manageBaseUrl, moduleName));

        return pageInfo;
    }

    @Override
    public String getMessage(String code, String defaultMessage, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(code, defaultMessage, locale, args);
    }

    @Override
    public String getMessage(String code, String defaultMessage, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
}
