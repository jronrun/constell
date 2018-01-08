package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LIKE;
import static com.benayn.constell.service.server.respond.HtmlTag.SELECT;
import static com.benayn.constell.services.capricorn.config.Authorities.CONTENT_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.CONTENT_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.CONTENT_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.CONTENT_UPDATE;

import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.services.capricorn.enums.ContentStatus;
import com.benayn.constell.services.capricorn.enums.ContentType;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable
@Accessable(
    create = CONTENT_CREATE,
    retrieve = CONTENT_RETRIEVE,
    update = CONTENT_UPDATE,
    delete = CONTENT_DELETE)
public class ContentVO extends Renderable {

    @DefineElement("render.common.id")
    @Searchable
    @Listable
    @Editable(hidden = true)
    private Long id;

    @NotNull
    @DefineElement("render.content.title")
    @Searchable(condition = LIKE)
    @Listable
    @Editable
    private String title;

    @NotNull
    @DefineElement("render.content.summary")
    @Searchable(condition = LIKE)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    private String summary;

    @NotNull
    @DefineElement("render.content.note")
    @Searchable(condition = LIKE)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    private String note;

    @DefineElement("render.content.language")
    @Editable
    private LanguageVO language;

    @DefineElement("render.content.tags")
    @Listable(fragment = "content_list_tags")
    private List<TagVO> tags;

    @DefineElement(value = "render.content.status", options = ContentStatus.class, tag = SELECT)
    @Searchable
    @Editable
    @Listable
    private Short status;

    @DefineElement(value = "render.content.type", options = ContentType.class, tag = SELECT)
    @Searchable
    @Editable
    @Listable
    private Short type;

    @DefineElement("render.common.createTime")
    @Listable
    private String createTime;

}
