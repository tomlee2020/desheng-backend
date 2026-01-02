package com.desheng.service;

import com.desheng.model.SeedApprovalDocument;
import com.desheng.model.dto.*;
import com.desheng.repository.SeedApprovalElasticsearchRepository;
import com.desheng.util.PinyinUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeedApprovalDetailsService {

    private final SeedApprovalElasticsearchRepository seedApprovalRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 获取种子审定详情
     */
    public SeedApprovalDetailsDto getApprovalDetails(String id) {
        log.info("Fetching approval details for seed ID: {}", id);
        
        Optional<SeedApprovalDocument> documentOpt = seedApprovalRepository.findById(id);
        if (documentOpt.isEmpty()) {
            return null;
        }

        return convertToDto(documentOpt.get());
    }

    /**
     * 高级搜索
     */
    public PagedResponse<SeedSearchResultDto> advancedSearch(AdvancedSearchRequest request) {
        log.info("Advanced search with request: {}", request);
        
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        
        Criteria criteria = buildSearchCriteria(request);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        List<SeedSearchResultDto> items = searchHits.stream()
                .map(hit -> convertToSearchResult(hit.getContent()))
                .collect(Collectors.toList());
        
        return PagedResponse.of(items, searchHits.getTotalHits(), request.getPage(), request.getPageSize());
    }

    /**
     * 按申请单位搜索
     */
    public PagedResponse<SeedSearchResultDto> searchByApplicant(String applicant, Integer page, Integer pageSize) {
        log.info("Searching by applicant: {}", applicant);
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Criteria criteria = new Criteria("applicant").contains(applicant)
                .or(new Criteria("applicant.pinyin").contains(applicant))
                .or(new Criteria("applicantPinyin").contains(applicant));
        
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        List<SeedSearchResultDto> items = searchHits.stream()
                .map(hit -> convertToSearchResult(hit.getContent()))
                .collect(Collectors.toList());
        
        return PagedResponse.of(items, searchHits.getTotalHits(), page, pageSize);
    }

    /**
     * 按育种者搜索
     */
    public PagedResponse<SeedSearchResultDto> searchByBreeder(String breeder, Integer page, Integer pageSize) {
        log.info("Searching by breeder: {}", breeder);
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Criteria criteria = new Criteria("breeder").contains(breeder)
                .or(new Criteria("breeder.pinyin").contains(breeder))
                .or(new Criteria("breederPinyin").contains(breeder));
        
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        List<SeedSearchResultDto> items = searchHits.stream()
                .map(hit -> convertToSearchResult(hit.getContent()))
                .collect(Collectors.toList());
        
        return PagedResponse.of(items, searchHits.getTotalHits(), page, pageSize);
    }

    /**
     * 按审定编号精确搜索
     */
    public SeedSearchResultDto searchByApprovalNumber(String approvalNumber) {
        log.info("Searching by approval number: {}", approvalNumber);
        
        Optional<SeedApprovalDocument> documentOpt = seedApprovalRepository.findByApprovalNumber(approvalNumber);
        return documentOpt.map(this::convertToSearchResult).orElse(null);
    }

    /**
     * 按转基因筛选
     */
    public PagedResponse<SeedSearchResultDto> searchByGMO(Boolean isGMO, Integer page, Integer pageSize) {
        log.info("Searching by GMO status: {}", isGMO);
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Criteria criteria = new Criteria("isGMO").is(isGMO);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        List<SeedSearchResultDto> items = searchHits.stream()
                .map(hit -> convertToSearchResult(hit.getContent()))
                .collect(Collectors.toList());
        
        return PagedResponse.of(items, searchHits.getTotalHits(), page, pageSize);
    }

    /**
     * 获取申请单位列表
     */
    public List<String> getAllApplicants() {
        log.info("Fetching all applicants from ES");
        
        // 使用聚合查询获取所有不重复的申请单位
        Criteria criteria = new Criteria("applicant").exists();
        Query query = new CriteriaQuery(criteria);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent().getApplicant())
                .filter(applicant -> applicant != null && !applicant.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取育种者列表
     */
    public List<String> getAllBreeders() {
        log.info("Fetching all breeders from ES");
        
        Criteria criteria = new Criteria("breeder").exists();
        Query query = new CriteriaQuery(criteria);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent().getBreeder())
                .filter(breeder -> breeder != null && !breeder.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取审定单位列表
     */
    public List<String> getAllApprovalAuthorities() {
        log.info("Fetching all approval authorities from ES");
        
        Criteria criteria = new Criteria("approvalAuthority").exists();
        Query query = new CriteriaQuery(criteria);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(query, SeedApprovalDocument.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent().getApprovalAuthority())
                .filter(authority -> authority != null && !authority.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取审定编号建议
     */
    public List<String> getApprovalNumberSuggestions(String query) {
        log.info("Getting approval number suggestions for: {}", query);
        
        Pageable pageable = PageRequest.of(0, 10);
        
        Criteria criteria = new Criteria("approvalNumber").startsWith(query);
        Query searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedApprovalDocument> searchHits = elasticsearchTemplate.search(searchQuery, SeedApprovalDocument.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent().getApprovalNumber())
                .collect(Collectors.toList());
    }

    /**
     * 构建搜索条件
     */
    private Criteria buildSearchCriteria(AdvancedSearchRequest request) {
        Criteria criteria = new Criteria();
        
        // 关键词搜索 - 多字段匹配（包括拼音）
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().trim();
            Criteria keywordCriteria = new Criteria("varietyName").contains(keyword)
                    .or(new Criteria("varietyName.pinyin").contains(keyword))
                    .or(new Criteria("varietyNamePinyin").contains(keyword))
                    .or(new Criteria("varietyNamePinyinShort").contains(keyword))
                    .or(new Criteria("approvalNumber").contains(keyword))
                    .or(new Criteria("applicant").contains(keyword))
                    .or(new Criteria("applicant.pinyin").contains(keyword))
                    .or(new Criteria("applicantPinyin").contains(keyword));
            criteria = criteria.and(keywordCriteria);
        }
        
        // 审定编号精确匹配
        if (request.getApprovalNumber() != null && !request.getApprovalNumber().trim().isEmpty()) {
            criteria = criteria.and(new Criteria("approvalNumber").is(request.getApprovalNumber()));
        }
        
        // 品种名称模糊匹配（包括拼音）
        if (request.getVarietyName() != null && !request.getVarietyName().trim().isEmpty()) {
            String varietyName = request.getVarietyName().trim();
            Criteria varietyCriteria = new Criteria("varietyName").contains(varietyName)
                    .or(new Criteria("varietyName.pinyin").contains(varietyName))
                    .or(new Criteria("varietyNamePinyin").contains(varietyName))
                    .or(new Criteria("varietyNamePinyinShort").contains(varietyName));
            criteria = criteria.and(varietyCriteria);
        }
        
        // 申请单位模糊匹配（包括拼音）
        if (request.getApplicant() != null && !request.getApplicant().trim().isEmpty()) {
            String applicant = request.getApplicant().trim();
            Criteria applicantCriteria = new Criteria("applicant").contains(applicant)
                    .or(new Criteria("applicant.pinyin").contains(applicant))
                    .or(new Criteria("applicantPinyin").contains(applicant));
            criteria = criteria.and(applicantCriteria);
        }
        
        // 育种者模糊匹配（包括拼音）
        if (request.getBreeder() != null && !request.getBreeder().trim().isEmpty()) {
            String breeder = request.getBreeder().trim();
            Criteria breederCriteria = new Criteria("breeder").contains(breeder)
                    .or(new Criteria("breeder.pinyin").contains(breeder))
                    .or(new Criteria("breederPinyin").contains(breeder));
            criteria = criteria.and(breederCriteria);
        }
        
        // 作物名称精确匹配
        if (request.getCropName() != null && !request.getCropName().trim().isEmpty()) {
            criteria = criteria.and(new Criteria("cropName").is(request.getCropName()));
        }
        
        // 审定年份精确匹配
        if (request.getApprovalYear() != null) {
            criteria = criteria.and(new Criteria("approvalYear").is(request.getApprovalYear()));
        }
        
        // 审定年份范围
        if (request.getApprovalYearRange() != null && request.getApprovalYearRange().size() == 2) {
            Integer startYear = request.getApprovalYearRange().get(0);
            Integer endYear = request.getApprovalYearRange().get(1);
            criteria = criteria.and(new Criteria("approvalYear").between(startYear, endYear));
        }
        
        // 转基因筛选
        if (request.getIsGMO() != null) {
            criteria = criteria.and(new Criteria("isGMO").is(request.getIsGMO()));
        }
        
        // 审定单位包含匹配
        if (request.getApprovalAuthority() != null && !request.getApprovalAuthority().trim().isEmpty()) {
            criteria = criteria.and(new Criteria("approvalAuthority").contains(request.getApprovalAuthority()));
        }
        
        // 适宜地区匹配
        if (request.getSuitableRegion() != null && !request.getSuitableRegion().trim().isEmpty()) {
            criteria = criteria.and(new Criteria("suitableRegions").contains(request.getSuitableRegion()));
        }
        
        return criteria;
    }

    /**
     * 转换为详情DTO
     */
    private SeedApprovalDetailsDto convertToDto(SeedApprovalDocument document) {
        // 构建产量表现数据
        List<SeedApprovalDetailsDto.YearlyData> yearlyDataList = null;
        if (document.getYieldData() != null) {
            yearlyDataList = document.getYieldData().stream()
                    .map(data -> SeedApprovalDetailsDto.YearlyData.builder()
                            .year(data.getYear())
                            .location(data.getLocation())
                            .yield(data.getYieldValue())
                            .unit(data.getYieldUnit())
                            .comparisonVariety(data.getComparisonVariety())
                            .comparisonYield(data.getComparisonYield())
                            .build())
                    .collect(Collectors.toList());
        }

        return SeedApprovalDetailsDto.builder()
                .id(document.getId())
                .approvalNumber(document.getApprovalNumber())
                .varietyName(document.getVarietyName())
                .cropName(document.getCropName())
                .approvalYear(document.getApprovalYear())
                .applicant(document.getApplicant())
                .breeder(document.getBreeder())
                .varietySource(document.getVarietySource())
                .isGMO(document.getIsGMO())
                .licenseInfo(document.getLicenseInfo())
                .varietyRights(document.getVarietyRights())
                .approvalAuthority(document.getApprovalAuthority())
                .characteristics(SeedApprovalDetailsDto.Characteristics.builder()
                        .detailedDescription(document.getDetailedDescription())
                        .growthPeriod(document.getGrowthPeriod())
                        .plantHeight(document.getPlantHeight())
                        .resistance(document.getResistance())
                        .qualityTraits(document.getQualityTraits())
                        .build())
                .yieldPerformance(SeedApprovalDetailsDto.YieldPerformance.builder()
                        .summary(document.getYieldSummary())
                        .yearlyData(yearlyDataList)
                        .comparisonData(document.getComparisonData())
                        .build())
                .cultivationTechnology(SeedApprovalDetailsDto.CultivationTechnology.builder()
                        .requirements(document.getCultivationRequirements())
                        .techniques(document.getCultivationTechniques())
                        .precautions(document.getCultivationPrecautions())
                        .build())
                .approvalOpinion(SeedApprovalDetailsDto.ApprovalOpinion.builder()
                        .opinion(document.getApprovalOpinion())
                        .suitableRegions(document.getSuitableRegions())
                        .restrictions(document.getPlantingRestrictions())
                        .build())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .version(document.getVersion())
                .build();
    }

    /**
     * 转换为搜索结果DTO
     */
    private SeedSearchResultDto convertToSearchResult(SeedApprovalDocument document) {
        return SeedSearchResultDto.builder()
                .id(document.getId())
                .approvalNumber(document.getApprovalNumber())
                .varietyName(document.getVarietyName())
                .cropName(document.getCropName())
                .approvalYear(document.getApprovalYear())
                .applicant(document.getApplicant())
                .breeder(document.getBreeder())
                .isGMO(document.getIsGMO())
                .approvalAuthority(document.getApprovalAuthority())
                .suitableRegions(document.getSuitableRegions())
                .build();
    }

    /**
     * 保存文档到ES
     */
    public void saveDocument(SeedApprovalDocument document) {
        log.info("Saving seed approval document to Elasticsearch: {}", document.getId());
        
        // 生成拼音字段
        if (document.getVarietyName() != null) {
            document.setVarietyNamePinyin(PinyinUtil.getPinyinWithoutTone(document.getVarietyName()));
            document.setVarietyNamePinyinShort(PinyinUtil.getPinyinShort(document.getVarietyName()));
        }
        
        if (document.getApplicant() != null) {
            document.setApplicantPinyin(PinyinUtil.getPinyinWithoutTone(document.getApplicant()));
        }
        
        if (document.getBreeder() != null) {
            document.setBreederPinyin(PinyinUtil.getPinyinWithoutTone(document.getBreeder()));
        }
        
        seedApprovalRepository.save(document);
    }

    /**
     * 批量保存文档到ES
     */
    public void saveDocuments(List<SeedApprovalDocument> documents) {
        log.info("Saving {} seed approval documents to Elasticsearch", documents.size());
        
        // 为每个文档生成拼音字段
        documents.forEach(document -> {
            if (document.getVarietyName() != null) {
                document.setVarietyNamePinyin(PinyinUtil.getPinyinWithoutTone(document.getVarietyName()));
                document.setVarietyNamePinyinShort(PinyinUtil.getPinyinShort(document.getVarietyName()));
            }
            
            if (document.getApplicant() != null) {
                document.setApplicantPinyin(PinyinUtil.getPinyinWithoutTone(document.getApplicant()));
            }
            
            if (document.getBreeder() != null) {
                document.setBreederPinyin(PinyinUtil.getPinyinWithoutTone(document.getBreeder()));
            }
        });
        
        seedApprovalRepository.saveAll(documents);
    }

    /**
     * 删除文档
     */
    public void deleteDocument(String id) {
        log.info("Deleting seed approval document from Elasticsearch: {}", id);
        seedApprovalRepository.deleteById(id);
    }
}