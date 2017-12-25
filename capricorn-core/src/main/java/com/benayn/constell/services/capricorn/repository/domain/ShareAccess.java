package com.benayn.constell.services.capricorn.repository.domain;

import com.benayn.constell.services.capricorn.type.AccessBy;
import com.benayn.constell.services.capricorn.type.AccessResult;
import com.benayn.constell.services.capricorn.type.ShareSnapshoot;
import java.util.Date;

public class ShareAccess {
    private Long id;

    private Long shareId;

    private ShareSnapshoot shareSnapshoot;

    private String contentSnapshoot;

    private AccessBy accessBy;

    private AccessResult accessResult;

    private Date createTime;

    private Date lastModifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public ShareSnapshoot getShareSnapshoot() {
        return shareSnapshoot;
    }

    public void setShareSnapshoot(ShareSnapshoot shareSnapshoot) {
        this.shareSnapshoot = shareSnapshoot;
    }

    public String getContentSnapshoot() {
        return contentSnapshoot;
    }

    public void setContentSnapshoot(String contentSnapshoot) {
        this.contentSnapshoot = contentSnapshoot == null ? null : contentSnapshoot.trim();
    }

    public AccessBy getAccessBy() {
        return accessBy;
    }

    public void setAccessBy(AccessBy accessBy) {
        this.accessBy = accessBy;
    }

    public AccessResult getAccessResult() {
        return accessResult;
    }

    public void setAccessResult(AccessResult accessResult) {
        this.accessResult = accessResult;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}