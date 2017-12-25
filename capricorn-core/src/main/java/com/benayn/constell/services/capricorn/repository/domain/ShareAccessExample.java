package com.benayn.constell.services.capricorn.repository.domain;

import com.benayn.constell.services.capricorn.type.AccessBy;
import com.benayn.constell.services.capricorn.type.AccessResult;
import com.benayn.constell.services.capricorn.type.ShareSnapshoot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareAccessExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ShareAccessExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andShareIdIsNull() {
            addCriterion("share_id is null");
            return (Criteria) this;
        }

        public Criteria andShareIdIsNotNull() {
            addCriterion("share_id is not null");
            return (Criteria) this;
        }

        public Criteria andShareIdEqualTo(Long value) {
            addCriterion("share_id =", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotEqualTo(Long value) {
            addCriterion("share_id <>", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdGreaterThan(Long value) {
            addCriterion("share_id >", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdGreaterThanOrEqualTo(Long value) {
            addCriterion("share_id >=", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdLessThan(Long value) {
            addCriterion("share_id <", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdLessThanOrEqualTo(Long value) {
            addCriterion("share_id <=", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdIn(List<Long> values) {
            addCriterion("share_id in", values, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotIn(List<Long> values) {
            addCriterion("share_id not in", values, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdBetween(Long value1, Long value2) {
            addCriterion("share_id between", value1, value2, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotBetween(Long value1, Long value2) {
            addCriterion("share_id not between", value1, value2, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootIsNull() {
            addCriterion("share_snapshoot is null");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootIsNotNull() {
            addCriterion("share_snapshoot is not null");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootEqualTo(ShareSnapshoot value) {
            addCriterion("share_snapshoot =", value, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootNotEqualTo(ShareSnapshoot value) {
            addCriterion("share_snapshoot <>", value, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootGreaterThan(ShareSnapshoot value) {
            addCriterion("share_snapshoot >", value, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootGreaterThanOrEqualTo(ShareSnapshoot value) {
            addCriterion("share_snapshoot >=", value, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootLessThan(ShareSnapshoot value) {
            addCriterion("share_snapshoot <", value, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootLessThanOrEqualTo(ShareSnapshoot value) {
            addCriterion("share_snapshoot <=", value, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootIn(List<ShareSnapshoot> values) {
            addCriterion("share_snapshoot in", values, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootNotIn(List<ShareSnapshoot> values) {
            addCriterion("share_snapshoot not in", values, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootBetween(ShareSnapshoot value1, ShareSnapshoot value2) {
            addCriterion("share_snapshoot between", value1, value2, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andShareSnapshootNotBetween(ShareSnapshoot value1, ShareSnapshoot value2) {
            addCriterion("share_snapshoot not between", value1, value2, "shareSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootIsNull() {
            addCriterion("content_snapshoot is null");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootIsNotNull() {
            addCriterion("content_snapshoot is not null");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootEqualTo(String value) {
            addCriterion("content_snapshoot =", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootNotEqualTo(String value) {
            addCriterion("content_snapshoot <>", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootGreaterThan(String value) {
            addCriterion("content_snapshoot >", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootGreaterThanOrEqualTo(String value) {
            addCriterion("content_snapshoot >=", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootLessThan(String value) {
            addCriterion("content_snapshoot <", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootLessThanOrEqualTo(String value) {
            addCriterion("content_snapshoot <=", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootLike(String value) {
            addCriterion("content_snapshoot like", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootNotLike(String value) {
            addCriterion("content_snapshoot not like", value, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootIn(List<String> values) {
            addCriterion("content_snapshoot in", values, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootNotIn(List<String> values) {
            addCriterion("content_snapshoot not in", values, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootBetween(String value1, String value2) {
            addCriterion("content_snapshoot between", value1, value2, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andContentSnapshootNotBetween(String value1, String value2) {
            addCriterion("content_snapshoot not between", value1, value2, "contentSnapshoot");
            return (Criteria) this;
        }

        public Criteria andAccessByIsNull() {
            addCriterion("access_by is null");
            return (Criteria) this;
        }

        public Criteria andAccessByIsNotNull() {
            addCriterion("access_by is not null");
            return (Criteria) this;
        }

        public Criteria andAccessByEqualTo(AccessBy value) {
            addCriterion("access_by =", value, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByNotEqualTo(AccessBy value) {
            addCriterion("access_by <>", value, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByGreaterThan(AccessBy value) {
            addCriterion("access_by >", value, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByGreaterThanOrEqualTo(AccessBy value) {
            addCriterion("access_by >=", value, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByLessThan(AccessBy value) {
            addCriterion("access_by <", value, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByLessThanOrEqualTo(AccessBy value) {
            addCriterion("access_by <=", value, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByIn(List<AccessBy> values) {
            addCriterion("access_by in", values, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByNotIn(List<AccessBy> values) {
            addCriterion("access_by not in", values, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByBetween(AccessBy value1, AccessBy value2) {
            addCriterion("access_by between", value1, value2, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessByNotBetween(AccessBy value1, AccessBy value2) {
            addCriterion("access_by not between", value1, value2, "accessBy");
            return (Criteria) this;
        }

        public Criteria andAccessResultIsNull() {
            addCriterion("access_result is null");
            return (Criteria) this;
        }

        public Criteria andAccessResultIsNotNull() {
            addCriterion("access_result is not null");
            return (Criteria) this;
        }

        public Criteria andAccessResultEqualTo(AccessResult value) {
            addCriterion("access_result =", value, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultNotEqualTo(AccessResult value) {
            addCriterion("access_result <>", value, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultGreaterThan(AccessResult value) {
            addCriterion("access_result >", value, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultGreaterThanOrEqualTo(AccessResult value) {
            addCriterion("access_result >=", value, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultLessThan(AccessResult value) {
            addCriterion("access_result <", value, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultLessThanOrEqualTo(AccessResult value) {
            addCriterion("access_result <=", value, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultIn(List<AccessResult> values) {
            addCriterion("access_result in", values, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultNotIn(List<AccessResult> values) {
            addCriterion("access_result not in", values, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultBetween(AccessResult value1, AccessResult value2) {
            addCriterion("access_result between", value1, value2, "accessResult");
            return (Criteria) this;
        }

        public Criteria andAccessResultNotBetween(AccessResult value1, AccessResult value2) {
            addCriterion("access_result not between", value1, value2, "accessResult");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeIsNull() {
            addCriterion("last_modify_time is null");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeIsNotNull() {
            addCriterion("last_modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeEqualTo(Date value) {
            addCriterion("last_modify_time =", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeNotEqualTo(Date value) {
            addCriterion("last_modify_time <>", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeGreaterThan(Date value) {
            addCriterion("last_modify_time >", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_modify_time >=", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeLessThan(Date value) {
            addCriterion("last_modify_time <", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_modify_time <=", value, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeIn(List<Date> values) {
            addCriterion("last_modify_time in", values, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeNotIn(List<Date> values) {
            addCriterion("last_modify_time not in", values, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeBetween(Date value1, Date value2) {
            addCriterion("last_modify_time between", value1, value2, "lastModifyTime");
            return (Criteria) this;
        }

        public Criteria andLastModifyTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_modify_time not between", value1, value2, "lastModifyTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}