package com.xpand.xface.dao.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.xpand.xface.bean.SearchPersonCondition;
import com.xpand.xface.entity.PersonInfo;

public class PersonInfoSpecification implements Specification<PersonInfo>{
	
	SearchPersonCondition searchCondition;
	public PersonInfoSpecification(SearchPersonCondition searchCondition) {
		this.searchCondition = searchCondition;
	}

	@Override
	public Predicate toPredicate(Root<PersonInfo> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		switch (this.searchCondition.getSearchOperation()) {
			case SearchPersonCondition.OPERATION_EQUAL:
				return builder.equal(
						root.<String> get(this.searchCondition.getSearchField()), this.searchCondition.getSearchValue());
			case SearchPersonCondition.OPERATION_GREATHER_THAN:
				return builder.greaterThan(
						root.<String> get(this.searchCondition.getSearchField()), this.searchCondition.getSearchValue());
			case SearchPersonCondition.OPERATION_GREATHER_THAN_OR_EQUAL:
				return builder.greaterThanOrEqualTo(
						root.<String> get(this.searchCondition.getSearchField()), this.searchCondition.getSearchValue());
			case SearchPersonCondition.OPERATION_LESS_THAN:
				return builder.lessThan(
						root.<String> get(this.searchCondition.getSearchField()), this.searchCondition.getSearchValue());
			case SearchPersonCondition.OPERATION_LESS_THAN_OR_EQUAL:
				return builder.lessThanOrEqualTo(
						root.<String> get(this.searchCondition.getSearchField()), this.searchCondition.getSearchValue());				
			case SearchPersonCondition.OPERATION_LIKE:
				return builder.like(
	                  root.<String>get(this.searchCondition.getSearchField()), "%" + this.searchCondition.getSearchValue() + "%");
			default:
		}
//		if (this.criteria.getOperation().equalsIgnoreCase("qt")) {
//            return builder.greaterThanOrEqualTo(
//              root.<String> get(criteria.getKey()), this.criteria.getValue().toString());
//        } else if (this.criteria.getOperation().equalsIgnoreCase("<")) {
//            return builder.lessThanOrEqualTo(
//              root.<String> get(criteria.getKey()), this.criteria.getValue().toString());
//        } else if (this.criteria.getOperation().equalsIgnoreCase(":")) {
//            if (root.get(this.criteria.getKey()).getJavaType() == String.class) {
//                return builder.like(
//                  root.<String>get(this.criteria.getKey()), "%" + criteria.getValue() + "%");
//            } else {
//                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
//            }
//        }
		return null;
	}

}
