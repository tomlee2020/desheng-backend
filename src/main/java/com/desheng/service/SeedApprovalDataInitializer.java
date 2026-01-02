package com.desheng.service;

import com.desheng.model.SeedApprovalDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 种子审定详情数据初始化器
 * 在应用启动时初始化一些测试数据到ES
 */
@Component
@Order(2) // 在索引创建之后执行
@Slf4j
@RequiredArgsConstructor
public class SeedApprovalDataInitializer implements CommandLineRunner {

    private final SeedApprovalDetailsService seedApprovalDetailsService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing seed approval details test data...");
        
        try {
            // 创建测试数据
            List<SeedApprovalDocument> testData = createTestData();
            
            // 保存到ES
            seedApprovalDetailsService.saveDocuments(testData);
            
            log.info("Successfully initialized {} seed approval details test records", testData.size());
        } catch (Exception e) {
            log.error("Failed to initialize seed approval details test data", e);
        }
    }

    private List<SeedApprovalDocument> createTestData() {
        LocalDateTime now = LocalDateTime.now();
        
        return Arrays.asList(
            // 水稻品种1
            SeedApprovalDocument.builder()
                    .id("12345")
                    .approvalNumber("国审稻20210001")
                    .varietyName("华优1号")
                    .cropName("水稻")
                    .approvalYear(2021)
                    .applicant("中国农业科学院作物科学研究所")
                    .breeder("张三, 李四")
                    .varietySource("华A × 优1")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2021)农种许字第001号")
                    .varietyRights("品种权号：CNA20210001.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属籼型三系杂交水稻，全生育期135天，比对照品种晚熟3天。株型适中，分蘖力中等，穗大粒多，结实率较高。")
                    .growthPeriod("135天")
                    .plantHeight("110厘米")
                    .resistance("中抗稻瘟病，抗白叶枯病")
                    .qualityTraits("米质优良，整精米率65%，垩白粒率12%，垩白度2.5%")
                    .yieldSummary("两年区域试验平均亩产620.5公斤，比对照增产8.5%")
                    .comparisonData("比对照品种增产8.5%")
                    .cultivationRequirements("适宜在长江中下游稻区种植")
                    .cultivationTechniques("适时播种，培育壮秧；合理密植，每亩1.5-1.8万穴")
                    .cultivationPrecautions("注意防治稻瘟病和纹枯病")
                    .approvalOpinion("该品种符合国家水稻品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("湖南", "湖北", "江西", "安徽"))
                    .plantingRestrictions("不适宜在稻瘟病重发区种植")
                    .yieldData(Arrays.asList(
                            SeedApprovalDocument.YieldData.builder()
                                    .year(2019)
                                    .location("湖南长沙")
                                    .yieldValue(615.2)
                                    .yieldUnit("公斤/亩")
                                    .comparisonVariety("对照品种")
                                    .comparisonYield(570.3)
                                    .build(),
                            SeedApprovalDocument.YieldData.builder()
                                    .year(2020)
                                    .location("湖南长沙")
                                    .yieldValue(625.8)
                                    .yieldUnit("公斤/亩")
                                    .comparisonVariety("对照品种")
                                    .comparisonYield(575.6)
                                    .build()
                    ))
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build(),

            // 水稻品种2
            SeedApprovalDocument.builder()
                    .id("12346")
                    .approvalNumber("国审稻20220002")
                    .varietyName("丰优2号")
                    .cropName("水稻")
                    .approvalYear(2022)
                    .applicant("湖南省农业科学院")
                    .breeder("王五, 赵六")
                    .varietySource("丰A × 优2")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2022)农种许字第002号")
                    .varietyRights("品种权号：CNA20220002.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属籼型三系杂交水稻，全生育期138天，株型紧凑，抗倒性强。")
                    .growthPeriod("138天")
                    .plantHeight("105厘米")
                    .resistance("高抗稻瘟病，中抗白叶枯病")
                    .qualityTraits("米质较优，整精米率68%，垩白粒率10%")
                    .yieldSummary("两年区域试验平均亩产635.2公斤，比对照增产10.2%")
                    .comparisonData("比对照品种增产10.2%")
                    .cultivationRequirements("适宜在华南稻区种植")
                    .cultivationTechniques("适时播种，合理密植，科学施肥")
                    .cultivationPrecautions("注意防治纹枯病和稻飞虱")
                    .approvalOpinion("该品种符合国家水稻品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("广东", "广西", "海南", "福建"))
                    .plantingRestrictions("不适宜在低温地区种植")
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build(),

            // 玉米品种
            SeedApprovalDocument.builder()
                    .id("12347")
                    .approvalNumber("国审玉20210003")
                    .varietyName("先玉335")
                    .cropName("玉米")
                    .approvalYear(2021)
                    .applicant("先正达种业有限公司")
                    .breeder("李明, 张华")
                    .varietySource("PH6WC × PH4CV")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2021)农种许字第003号")
                    .varietyRights("品种权号：CNA20210003.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属普通玉米品种，春播生育期128天，株型紧凑，抗倒性好。")
                    .growthPeriod("128天")
                    .plantHeight("260厘米")
                    .resistance("中抗大斑病，抗小斑病")
                    .qualityTraits("籽粒黄色，半马齿型，容重750g/L")
                    .yieldSummary("两年区域试验平均亩产680.5公斤，比对照增产12.3%")
                    .comparisonData("比对照品种增产12.3%")
                    .cultivationRequirements("适宜在东北春玉米区种植")
                    .cultivationTechniques("适时播种，合理密植，每亩4500-5000株")
                    .cultivationPrecautions("注意防治玉米螟和大斑病")
                    .approvalOpinion("该品种符合国家玉米品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("黑龙江", "吉林", "辽宁", "内蒙古"))
                    .plantingRestrictions("不适宜在南方地区种植")
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build(),

            // 小麦品种
            SeedApprovalDocument.builder()
                    .id("12348")
                    .approvalNumber("国审麦20220004")
                    .varietyName("济麦22")
                    .cropName("小麦")
                    .approvalYear(2022)
                    .applicant("山东省农业科学院作物研究所")
                    .breeder("刘建军, 赵振东")
                    .varietySource("济南17 × 临汾137")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2022)农种许字第004号")
                    .varietyRights("品种权号：CNA20220004.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属半冬性中熟品种，全生育期232天，幼苗半匍匐，分蘖力较强。")
                    .growthPeriod("232天")
                    .plantHeight("75厘米")
                    .resistance("中抗条纹花叶病毒病，高抗白粉病")
                    .qualityTraits("籽粒角质，饱满度好，千粒重42.5g")
                    .yieldSummary("两年区域试验平均亩产520.8公斤，比对照增产8.9%")
                    .comparisonData("比对照品种增产8.9%")
                    .cultivationRequirements("适宜在黄淮冬麦区种植")
                    .cultivationTechniques("适期播种，基本苗18-22万/亩")
                    .cultivationPrecautions("注意防治赤霉病和蚜虫")
                    .approvalOpinion("该品种符合国家小麦品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("山东", "河南", "河北", "山西"))
                    .plantingRestrictions("不适宜在南方稻茬麦区种植")
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build(),

            // 大豆品种
            SeedApprovalDocument.builder()
                    .id("12349")
                    .approvalNumber("国审豆20230005")
                    .varietyName("中黄13")
                    .cropName("大豆")
                    .approvalYear(2023)
                    .applicant("中国农业科学院作物科学研究所")
                    .breeder("韩天富, 李英慧")
                    .varietySource("中作975 × 徐豆18")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2023)农种许字第005号")
                    .varietyRights("品种权号：CNA20230005.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属有限结荚习性夏大豆品种，生育期102天，株型收敛，抗倒性强。")
                    .growthPeriod("102天")
                    .plantHeight("65厘米")
                    .resistance("中抗花叶病毒病，抗大豆胞囊线虫病")
                    .qualityTraits("籽粒椭圆形，种皮黄色，脐褐色，百粒重20.5g")
                    .yieldSummary("两年区域试验平均亩产180.2公斤，比对照增产7.8%")
                    .comparisonData("比对照品种增产7.8%")
                    .cultivationRequirements("适宜在黄淮海夏大豆区种植")
                    .cultivationTechniques("6月上中旬播种，每亩1.2-1.5万株")
                    .cultivationPrecautions("注意防治食心虫和红蜘蛛")
                    .approvalOpinion("该品种符合国家大豆品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("河南", "山东", "安徽", "江苏"))
                    .plantingRestrictions("不适宜在东北春大豆区种植")
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build(),

            // 棉花品种
            SeedApprovalDocument.builder()
                    .id("12350")
                    .approvalNumber("国审棉20230006")
                    .varietyName("中棉所49")
                    .cropName("棉花")
                    .approvalYear(2023)
                    .applicant("中国农业科学院棉花研究所")
                    .breeder("杜雄明, 潘家驹")
                    .varietySource("中棉所35 × 新陆早13号")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2023)农种许字第006号")
                    .varietyRights("品种权号：CNA20230006.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属常规棉花品种，生育期125天，植株塔型，结铃性强。")
                    .growthPeriod("125天")
                    .plantHeight("90厘米")
                    .resistance("抗枯萎病，耐黄萎病")
                    .qualityTraits("纤维长度29.5mm，比强度30.2cN/tex，马克隆值4.8")
                    .yieldSummary("两年区域试验平均亩产籽棉280.5公斤，比对照增产9.2%")
                    .comparisonData("比对照品种增产9.2%")
                    .cultivationRequirements("适宜在长江流域棉区种植")
                    .cultivationTechniques("4月下旬播种，每亩1.8-2.2万株")
                    .cultivationPrecautions("注意防治棉铃虫和红铃虫")
                    .approvalOpinion("该品种符合国家棉花品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("湖北", "湖南", "江西", "安徽"))
                    .plantingRestrictions("不适宜在新疆棉区种植")
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build(),

            // 花生品种
            SeedApprovalDocument.builder()
                    .id("12351")
                    .approvalNumber("国审花20240007")
                    .varietyName("花育25")
                    .cropName("花生")
                    .approvalYear(2024)
                    .applicant("山东省花生研究所")
                    .breeder("万书波, 禹山林")
                    .varietySource("花育17 × 鲁花11")
                    .isGMO(false)
                    .licenseInfo("生产许可证号：(2024)农种许字第007号")
                    .varietyRights("品种权号：CNA20240007.1")
                    .approvalAuthority("国家农作物品种审定委员会")
                    .detailedDescription("该品种属普通型大花生品种，生育期125天，株型直立紧凑，抗倒性强。")
                    .growthPeriod("125天")
                    .plantHeight("45厘米")
                    .resistance("抗青枯病，耐叶斑病")
                    .qualityTraits("籽仁含油量52.8%，蛋白质含量25.2%，百果重220g")
                    .yieldSummary("两年区域试验平均亩产荚果320.5公斤，比对照增产11.5%")
                    .comparisonData("比对照品种增产11.5%")
                    .cultivationRequirements("适宜在北方花生区种植")
                    .cultivationTechniques("5月上旬播种，每亩8000-10000穴")
                    .cultivationPrecautions("注意防治蚜虫和叶斑病")
                    .approvalOpinion("该品种符合国家花生品种审定标准，通过审定")
                    .suitableRegions(Arrays.asList("山东", "河北", "河南", "辽宁"))
                    .plantingRestrictions("不适宜在南方花生区种植")
                    .createdAt(now)
                    .updatedAt(now)
                    .version(1)
                    .build()
        );
    }
}