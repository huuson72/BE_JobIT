package vn.hstore.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hstore.jobhunter.domain.SubscriptionPackage;
import vn.hstore.jobhunter.domain.response.ResultPaginationDTO;
import vn.hstore.jobhunter.repository.SubscriptionPackageRepository;

@Service
public class SubscriptionPackageService {

    private final SubscriptionPackageRepository subscriptionPackageRepository;

    public SubscriptionPackageService(SubscriptionPackageRepository subscriptionPackageRepository) {
        this.subscriptionPackageRepository = subscriptionPackageRepository;
    }
    
    public SubscriptionPackage createPackage(SubscriptionPackage subscriptionPackage) {
        return subscriptionPackageRepository.save(subscriptionPackage);
    }
    
    public SubscriptionPackage updatePackage(SubscriptionPackage subscriptionPackage) {
        Optional<SubscriptionPackage> existingPackage = 
            subscriptionPackageRepository.findById(subscriptionPackage.getId());
            
        if (existingPackage.isPresent()) {
            SubscriptionPackage updatedPackage = existingPackage.get();
            updatedPackage.setName(subscriptionPackage.getName());
            updatedPackage.setDescription(subscriptionPackage.getDescription());
            updatedPackage.setPrice(subscriptionPackage.getPrice());
            updatedPackage.setDurationDays(subscriptionPackage.getDurationDays());
            updatedPackage.setJobPostLimit(subscriptionPackage.getJobPostLimit());
            updatedPackage.setIsHighlighted(subscriptionPackage.getIsHighlighted());
            updatedPackage.setIsPrioritized(subscriptionPackage.getIsPrioritized());
            updatedPackage.setIsActive(subscriptionPackage.getIsActive());
            
            return subscriptionPackageRepository.save(updatedPackage);
        }
        
        return null;
    }
    
    public void deletePackage(Long id) {
        subscriptionPackageRepository.deleteById(id);
    }
    
    public Optional<SubscriptionPackage> findById(Long id) {
        return subscriptionPackageRepository.findById(id);
    }
    
    public List<SubscriptionPackage> findAllActivePackages() {
        return subscriptionPackageRepository.findByIsActive(true);
    }
    
    public ResultPaginationDTO findAllPackages(Specification<SubscriptionPackage> spec, Pageable pageable) {
        Page<SubscriptionPackage> packagePage = subscriptionPackageRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(packagePage.getTotalPages());
        meta.setTotal(packagePage.getTotalElements());
        
        result.setMeta(meta);
        result.setResult(packagePage.getContent());
        
        return result;
    }
} 