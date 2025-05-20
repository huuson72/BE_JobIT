package vn.hstore.jobhunter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.domain.response.ResultPaginationDTO;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            JobRepository jobRepository) { // Thêm JobRepository vào constructor
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Company handleCreateCompany(Company c) {
        return this.companyRepository.save(c);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());
        return rs;
    }

    public Company handleUpdateCompany(Company c) {
        Optional<Company> companyOptional = this.companyRepository.findById(c.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setLogo(c.getLogo());
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company company = comOptional.get();
            
            // Thay vì xóa mọi user thuộc công ty, đánh dấu công ty là đã xóa
            company.setDeleted(true);
            this.companyRepository.save(company);
            
            // Không cần xóa users vì chúng sẽ vẫn giữ nguyên liên kết, nhưng công ty đã bị đánh dấu xóa
        }
    }

    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }

    public Map<String, Object> findCompanyWithJobsById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            List<vn.hstore.jobhunter.domain.Job> jobs = this.jobRepository.findByCompany(company); // Lấy danh sách công việc của công ty

            Map<String, Object> response = new HashMap<>();
            response.put("company", company);
            response.put("jobs", jobs);
            return response;
        }
        return null;
    }

}
