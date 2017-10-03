package com.benayn.constell.service.server.component;

import static com.benayn.constell.service.server.respond.DefineType.EDITABLE;
import static com.benayn.constell.service.server.respond.DefineType.SEARCHABLE;
import static com.benayn.constell.service.server.respond.TagName.INPUT;
import static com.benayn.constell.service.server.respond.TagName.UNDEFINED;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.DefineType;
import com.benayn.constell.service.server.respond.DefinedElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.InputType;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.service.server.respond.TagName;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
public class ViewObjectResolverBean implements ViewObjectResolver {

    private MessageSource messageSource;
    private TemplateEngine textTemplateEngine;
    private static final String ELEMENT_ID_FORMAT = "el_%s";
    private static final String HIDDEN_STYLE = "display:none;";

    public ViewObjectResolverBean(MessageSource messageSource, TemplateEngine textTemplateEngine) {
        this.messageSource = messageSource;
        this.textTemplateEngine = textTemplateEngine;
    }

    @SuppressWarnings("WeakerAccess")
    //@Cacheable(value = "defined_pages", sync = true, key = "#viewObjectType.simpleName")
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

        List<Field> defineFields = getFields(viewObjectType);
        List<Field> itemFields = null;
        if (items.size() > 0) {
            itemFields = getFields(items.get(0).getClass());
        }

        Page<Renderable> newPage = page.cloneButResource();
        List<Field> finalItemFields = itemFields;
        items.forEach(item -> {
            Renderable render = asRenderable(viewObjectType, defineFields, item, finalItemFields, newPage);
            if (null != render) {
                renders.add(render);
            }
        });

        newPage.setResource(renders);
        return newPage;
    }

    private Renderable asRenderable(Class<? extends Renderable> viewObjectType,
        List<Field> defineFields, Object value, List<Field> valueFields,
        Page<Renderable> newPage) {
        try {
            Renderable render = viewObjectType.newInstance();
            defineFields.forEach(field -> {
                DefineElement defineElement = field.getAnnotation(DefineElement.class);
                boolean hasDefineElement = null != defineElement;

                Listable listable = field.getAnnotation(Listable.class);
                if (null != listable) {

                    String title = listable.value();
                    if (isNullOrEmpty(title) && hasDefineElement) {
                        title = defineElement.value();
                    }

                    checkArgument(!isNullOrEmpty(title), "undefined listable column title %s.%s",
                        viewObjectType.getSimpleName(), field.getName());
                    newPage.addTitle(field.getName(), getMessage(title, title));

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
                    } else {
                        aValue = getFieldValueByName(value, valueFields, field.getName());
                    }

                    setFieldValue(field, render, aValue);
                }

            });

            return render;
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn("initialize {} render fail: {}", viewObjectType.getSimpleName(), e.getMessage());
            Throwables.throwIfUnchecked(e);
        }

        return null;
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
                id = String.format(ELEMENT_ID_FORMAT, fieldName);
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
            element.setAttributes(Lists.newArrayList(attributes));

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

    private void setFieldValue(Field field, Object valueObj, Object aValue) {
        try {
            field.setAccessible(true);
            field.set(valueObj, aValue);
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

        return textTemplateEngine.process(fragment, context);
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
