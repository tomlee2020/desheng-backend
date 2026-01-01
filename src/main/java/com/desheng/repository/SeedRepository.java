package com.desheng.repository;

import com.desheng.model.Seed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeedRepository extends JpaRepository<Seed, Long> {

    /**
     * Search seeds by variety name, approval number, or company name
     */
    @Query("SELECT s FROM Seed s WHERE " +
           "LOWER(s.varietyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.approvalNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.company) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Seed> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Filter seeds by crop type
     */
    Page<Seed> findByCropType(String cropType, Pageable pageable);

    /**
     * Filter seeds by approval region
     */
    Page<Seed> findByApprovalRegion(String approvalRegion, Pageable pageable);

    /**
     * Filter seeds by approval year range
     */
    @Query("SELECT s FROM Seed s WHERE s.approvalYear BETWEEN :startYear AND :endYear")
    Page<Seed> findByApprovalYearRange(@Param("startYear") Integer startYear, 
                                       @Param("endYear") Integer endYear, 
                                       Pageable pageable);

    /**
     * Filter seeds by company
     */
    Page<Seed> findByCompanyContaining(String company, Pageable pageable);

    /**
     * Combined filter query with multiple conditions
     */
    @Query("SELECT s FROM Seed s WHERE " +
           "(:cropType IS NULL OR s.cropType = :cropType) AND " +
           "(:approvalRegion IS NULL OR s.approvalRegion LIKE CONCAT('%', :approvalRegion, '%')) AND " +
           "(:startYear IS NULL OR s.approvalYear >= :startYear) AND " +
           "(:endYear IS NULL OR s.approvalYear <= :endYear) AND " +
           "(:company IS NULL OR s.company LIKE CONCAT('%', :company, '%'))")
    Page<Seed> findByMultipleFilters(
            @Param("cropType") String cropType,
            @Param("approvalRegion") String approvalRegion,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear,
            @Param("company") String company,
            Pageable pageable);

    /**
     * Find all seeds sorted by approval year (descending)
     */
    Page<Seed> findAll(Pageable pageable);
}
