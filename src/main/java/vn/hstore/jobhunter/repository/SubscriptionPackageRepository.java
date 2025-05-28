package vn.hstore.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.SubscriptionPackage;

@Repository
public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, Long>,
        JpaSpecificationExecutor<SubscriptionPackage> {
    
    List<SubscriptionPackage> findByIsActive(Boolean isActive);
    
} 