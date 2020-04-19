package com.example.brickdoor.repositories;

import com.example.brickdoor.models.Company;
import com.example.brickdoor.models.Role;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends CrudRepository<Company,Integer> {
  @Query("SELECT company FROM User company WHERE company.id=:companyId")
  Company findCompanyById(@Param("companyId") int companyId);

  @Query("SELECT company FROM User company WHERE company.role=:role")
  Set<Company> getAllCompanies(@Param("role") Role role);
}
