package vn.hstore.jobhunter.util;

import org.springframework.data.jpa.domain.Specification;

import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.util.constant.LevelEnum;

public class JobSpecification {

    public static Specification<Job> hasLevel(LevelEnum level) {
        return (root, query, criteriaBuilder)
                -> level == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<Job> hasSalaryBetween(Double minSalary, Double maxSalary) {
        return (root, query, criteriaBuilder) -> {
            if (minSalary == null && maxSalary == null) {
                return criteriaBuilder.conjunction();
            }

            if (minSalary != null && maxSalary != null) {
                return criteriaBuilder.between(root.get("salary"), minSalary, maxSalary);
            }

            return minSalary != null
                    ? criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), minSalary)
                    : criteriaBuilder.lessThanOrEqualTo(root.get("salary"), maxSalary);
        };
    }

}
