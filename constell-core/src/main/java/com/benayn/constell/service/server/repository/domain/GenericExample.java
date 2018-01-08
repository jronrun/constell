package com.benayn.constell.service.server.repository.domain;

import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.BETWEEN;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.EQUAL_TO;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.GREATER_THAN;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.GREATER_THAN_OR_EQUAL_TO;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.IN;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.IS_NOT_NULL;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.IS_NULL;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LESS_THAN;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LESS_THAN_OR_EQUAL_TO;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LIKE;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.NOT_BETWEEN;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.NOT_EQUAL_TO;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.NOT_IN;
import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.NOT_LIKE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GenericExample {

    @Setter
    protected String orderByClause;
    @Setter
    protected boolean distinct;
    protected List<Criteria> oredCriteria;

    public GenericExample() {
        oredCriteria = Lists.newArrayList();
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
        return new Criteria();
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GenericCriteriaBean {
        @Getter
        protected List<Criterion> criteria;

        GenericCriteriaBean() {
            criteria = Lists.newArrayList();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        private Criteria addCriterion(Criterion criterion) {
            criteria.add(criterion);
            return (Criteria) this;
        }

        protected Criteria addCriterion(String condition) {
            checkNotNull(condition, "Value for condition cannot be null");
            return addCriterion(new Criterion(condition));
        }

        protected Criteria addCriterion(String condition, Object value, String property) {
            checkNotNull(value, "Value for %s cannot be null", property);
            return addCriterion(new Criterion(condition, value));
        }

        protected Criteria addCriterion(String condition, Object value1, Object value2, String property) {
            checkArgument(null != value1 && null != value2,
                "Between values for %s cannot be null", property);
            return addCriterion(new Criterion(condition, value1, value2));
        }

        public <T> Criteria and(ConditionTemplate conditionTemplate, String property, T value1, T value2) {
            if (null == value2) {
                if (null == value1) {
                    return addCriterion(conditionTemplate.value(property));
                }

                return addCriterion(conditionTemplate.value(property), value1, property);
            }

            return addCriterion(conditionTemplate.value(property), value1, value2, property);
        }

        public Criteria andIsNull(String property) {
            return addCriterion(IS_NULL.value(property));
        }

        public Criteria andIsNotNull(String property) {
            return addCriterion(IS_NOT_NULL.value(property));
        }

        public <T> Criteria andEqualTo(String property, T value) {
            return addCriterion(EQUAL_TO.value(property), value, property);
        }

        public <T> Criteria andNotEqualTo(String property, T value) {
            return addCriterion(NOT_EQUAL_TO.value(property), value, property);
        }

        public <T> Criteria andGreaterThan(String property, T value) {
            return addCriterion(GREATER_THAN.value(property), value, property);
        }

        public <T> Criteria andGreaterThanOrEqualTo(String property, T value) {
            return addCriterion(GREATER_THAN_OR_EQUAL_TO.value(property), value, property);
        }

        public <T> Criteria andLessThan(String property, T value) {
            return addCriterion(LESS_THAN.value(property), value, property);
        }

        public <T> Criteria andLessThanOrEqualTo(String property, T value) {
            return addCriterion(LESS_THAN_OR_EQUAL_TO.value(property), value, property);
        }

        public <T> Criteria andLike(String property, T value) {
            return addCriterion(LIKE.value(property), value, property);
        }

        public <T> Criteria andNotLike(String property, T value) {
            return addCriterion(NOT_LIKE.value(property), value, property);
        }

        public <T> Criteria andIn(String property, List<?> values) {
            return addCriterion(IN.value(property), values, property);
        }

        public <T> Criteria andNotIn(String property, List<?> values) {
            return addCriterion(NOT_IN.value(property), values, property);
        }

        public <T> Criteria andBetween(String property, T value1, T value2) {
            return addCriterion(BETWEEN.value(property), value1, value2, property);
        }

        public <T> Criteria andNotBetween(String property, T value1, T value2) {
            return addCriterion(NOT_BETWEEN.value(property), value1, value2, property);
        }

    }

    public static class Criteria extends GenericCriteriaBean {
        protected Criteria() {}
    }

    @Getter
    public static class Criterion {
        private String condition;
        private Object value;
        private Object secondValue;
        private boolean noValue;
        private boolean singleValue;
        private boolean betweenValue;
        private boolean listValue;
        private String typeHandler;

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
