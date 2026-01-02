package com.desheng.controller;

import com.desheng.model.dto.*;
import com.desheng.service.SeedApprovalDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seeds")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SeedApprovalController {

    private final SeedApprovalDetailsService seedApprovalDetailsService;

    /**
     * 获取种子审定详情
     * GET /api/seeds/{id}/approval-details
     */
    @GetMapping("/{id}/approval-details")
    public ResponseEntity<ApiResponse<SeedApprovalDetailsDto>> getApprovalDetails(@PathVariable String id) {
        log.info("GET /api/seeds/{}/approval-details", id);
        
        try {
            SeedApprovalDetailsDto details = seedApprovalDetailsService.getApprovalDetails(id);
            if (details == null) {
                return ResponseEntity.ok(ApiResponse.error(404, "Seed not found", "No seed found with ID: " + id));
            }
            return ResponseEntity.ok(ApiResponse.success(details));
        } catch (Exception e) {
            log.error("Error fetching approval details for ID: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 高级搜索（支持多条件筛选）
     * POST /api/seeds/search/advanced
     */
    @PostMapping("/search/advanced")
    public ResponseEntity<ApiResponse<PagedResponse<SeedSearchResultDto>>> advancedSearch(
            @RequestBody AdvancedSearchRequest request) {
        log.info("POST /api/seeds/search/advanced - request: {}", request);
        
        try {
            PagedResponse<SeedSearchResultDto> results = seedApprovalDetailsService.advancedSearch(request);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            log.error("Error in advanced search", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 按申请者搜索
     * GET /api/seeds/search/by-applicant
     */
    @GetMapping("/search/by-applicant")
    public ResponseEntity<ApiResponse<PagedResponse<SeedSearchResultDto>>> searchByApplicant(
            @RequestParam String applicant,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("GET /api/seeds/search/by-applicant - applicant: {}, page: {}, pageSize: {}", applicant, page, pageSize);
        
        try {
            if (applicant == null || applicant.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(400, "Invalid request parameters", "applicant parameter is required"));
            }
            
            PagedResponse<SeedSearchResultDto> results = seedApprovalDetailsService.searchByApplicant(applicant, page, pageSize);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            log.error("Error searching by applicant", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 按育种者搜索
     * GET /api/seeds/search/by-breeder
     */
    @GetMapping("/search/by-breeder")
    public ResponseEntity<ApiResponse<PagedResponse<SeedSearchResultDto>>> searchByBreeder(
            @RequestParam String breeder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("GET /api/seeds/search/by-breeder - breeder: {}, page: {}, pageSize: {}", breeder, page, pageSize);
        
        try {
            if (breeder == null || breeder.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(400, "Invalid request parameters", "breeder parameter is required"));
            }
            
            PagedResponse<SeedSearchResultDto> results = seedApprovalDetailsService.searchByBreeder(breeder, page, pageSize);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            log.error("Error searching by breeder", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 按审定编号精确搜索
     * GET /api/seeds/search/by-approval-number
     */
    @GetMapping("/search/by-approval-number")
    public ResponseEntity<ApiResponse<SeedSearchResultDto>> searchByApprovalNumber(
            @RequestParam String approvalNumber) {
        log.info("GET /api/seeds/search/by-approval-number - approvalNumber: {}", approvalNumber);
        
        try {
            if (approvalNumber == null || approvalNumber.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(400, "Invalid request parameters", "approvalNumber parameter is required"));
            }
            
            SeedSearchResultDto result = seedApprovalDetailsService.searchByApprovalNumber(approvalNumber);
            if (result == null) {
                return ResponseEntity.ok(ApiResponse.error(404, "Seed not found", "No seed found with approval number: " + approvalNumber));
            }
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Error searching by approval number", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 转基因品种筛选
     * GET /api/seeds/search/gmo
     */
    @GetMapping("/search/gmo")
    public ResponseEntity<ApiResponse<PagedResponse<SeedSearchResultDto>>> searchByGMO(
            @RequestParam Boolean isGMO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("GET /api/seeds/search/gmo - isGMO: {}, page: {}, pageSize: {}", isGMO, page, pageSize);
        
        try {
            if (isGMO == null) {
                return ResponseEntity.ok(ApiResponse.error(400, "Invalid request parameters", "isGMO parameter is required"));
            }
            
            PagedResponse<SeedSearchResultDto> results = seedApprovalDetailsService.searchByGMO(isGMO, page, pageSize);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            log.error("Error searching by GMO status", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 获取申请单位列表
     * GET /api/seeds/applicants
     */
    @GetMapping("/applicants")
    public ResponseEntity<ApiResponse<List<String>>> getAllApplicants() {
        log.info("GET /api/seeds/applicants");
        
        try {
            List<String> applicants = seedApprovalDetailsService.getAllApplicants();
            return ResponseEntity.ok(ApiResponse.success(applicants));
        } catch (Exception e) {
            log.error("Error fetching applicants", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 获取育种者列表
     * GET /api/seeds/breeders
     */
    @GetMapping("/breeders")
    public ResponseEntity<ApiResponse<List<String>>> getAllBreeders() {
        log.info("GET /api/seeds/breeders");
        
        try {
            List<String> breeders = seedApprovalDetailsService.getAllBreeders();
            return ResponseEntity.ok(ApiResponse.success(breeders));
        } catch (Exception e) {
            log.error("Error fetching breeders", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 获取审定单位列表
     * GET /api/seeds/approval-authorities
     */
    @GetMapping("/approval-authorities")
    public ResponseEntity<ApiResponse<List<String>>> getAllApprovalAuthorities() {
        log.info("GET /api/seeds/approval-authorities");
        
        try {
            List<String> authorities = seedApprovalDetailsService.getAllApprovalAuthorities();
            return ResponseEntity.ok(ApiResponse.success(authorities));
        } catch (Exception e) {
            log.error("Error fetching approval authorities", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }

    /**
     * 获取审定编号建议
     * GET /api/seeds/approval-number-suggestions
     */
    @GetMapping("/approval-number-suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getApprovalNumberSuggestions(
            @RequestParam String query) {
        log.info("GET /api/seeds/approval-number-suggestions - query: {}", query);
        
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(400, "Invalid request parameters", "query parameter is required"));
            }
            
            List<String> suggestions = seedApprovalDetailsService.getApprovalNumberSuggestions(query);
            return ResponseEntity.ok(ApiResponse.success(suggestions));
        } catch (Exception e) {
            log.error("Error fetching approval number suggestions", e);
            return ResponseEntity.ok(ApiResponse.error(500, "Internal server error", e.getMessage()));
        }
    }
}