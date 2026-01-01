# MyBatis-Plus 重构指南

## 概述

本项目已从 **Spring Data JPA** 重构为 **MyBatis-Plus**。MyBatis-Plus 是一个 MyBatis 的增强工具，在保留 MyBatis 灵活性的同时，提供了强大的 CRUD 操作和条件构造器，极大地提高了开发效率。

## 主要改进

### 1. 依赖变更

**原来（JPA）**：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**现在（MyBatis-Plus）**：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

### 2. 实体类注解变更

**原来（JPA）**：
```java
@Entity
@Table(name = "seeds")
public class Seed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String varietyName;
}
```

**现在（MyBatis-Plus）**：
```java
@TableName("seeds")
public class Seed {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("variety_name")
    private String varietyName;
}
```

### 3. 数据访问层变更

**原来（JPA Repository）**：
```java
@Repository
public interface SeedRepository extends JpaRepository<Seed, Long> {
    Page<Seed> findByCropType(String cropType, Pageable pageable);
}
```

**现在（MyBatis-Plus Mapper）**：
```java
@Mapper
public interface SeedMapper extends BaseMapper<Seed> {
    @Select("SELECT * FROM seeds WHERE crop_type = #{cropType}")
    IPage<Seed> findByCropType(Page<Seed> page, @Param("cropType") String cropType);
}
```

### 4. Service 层变更

**原来（JPA Service）**：
```java
@Service
public class SeedService {
    @Autowired
    private SeedRepository repository;
    
    public Page<Seed> getAllSeeds(int page, int pageSize) {
        return repository.findAll(PageRequest.of(page, pageSize));
    }
}
```

**现在（MyBatis-Plus Service）**：
```java
@Service
public class SeedService extends ServiceImpl<SeedMapper, Seed> {
    private final SeedMapper seedMapper;
    
    public IPage<Seed> getAllSeeds(int page, int pageSize) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Seed::getApprovalYear);
        return this.page(pageRequest, wrapper);
    }
}
```

## MyBatis-Plus 核心特性

### 1. BaseMapper 接口

MyBatis-Plus 提供的 `BaseMapper` 接口包含了基本的 CRUD 操作：

```java
// 插入
mapper.insert(entity);

// 根据 ID 查询
mapper.selectById(id);

// 查询所有
mapper.selectList(null);

// 更新
mapper.updateById(entity);

// 删除
mapper.deleteById(id);
```

### 2. 条件构造器（QueryWrapper 和 LambdaQueryWrapper）

**QueryWrapper 示例**：
```java
QueryWrapper<Seed> wrapper = new QueryWrapper<>();
wrapper.eq("crop_type", "水稻")
       .ge("approval_year", 2020)
       .like("variety_name", "杂交");
List<Seed> seeds = seedMapper.selectList(wrapper);
```

**LambdaQueryWrapper 示例（推荐）**：
```java
LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Seed::getCropType, "水稻")
       .ge(Seed::getApprovalYear, 2020)
       .like(Seed::getVarietyName, "杂交");
List<Seed> seeds = seedMapper.selectList(wrapper);
```

### 3. 分页查询

```java
// 创建分页对象（从 1 开始）
Page<Seed> page = new Page<>(1, 10);

// 执行分页查询
IPage<Seed> result = seedMapper.selectPage(page, wrapper);

// 获取结果
List<Seed> records = result.getRecords();
long total = result.getTotal();
long pages = result.getPages();
```

### 4. 自定义 SQL

```java
@Mapper
public interface SeedMapper extends BaseMapper<Seed> {
    // 使用 @Select 注解
    @Select("SELECT * FROM seeds WHERE variety_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Seed> searchByKeyword(@Param("keyword") String keyword);
    
    // 使用动态 SQL
    @Select("<script>" +
            "SELECT * FROM seeds WHERE 1=1 " +
            "<if test='cropType != null'> AND crop_type = #{cropType}</if> " +
            "<if test='company != null'> AND company LIKE CONCAT('%', #{company}, '%')</if> " +
            "</script>")
    IPage<Seed> findByConditions(Page<Seed> page, 
                                 @Param("cropType") String cropType,
                                 @Param("company") String company);
}
```

### 5. 字段自动填充

通过实现 `MetaObjectHandler` 接口，可以在插入和更新时自动填充字段：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

## 配置说明

### application.properties

```properties
# MyBatis-Plus 配置
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
mybatis-plus.type-aliases-package=com.desheng.model
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.global-config.db-config.id-type=auto
mybatis-plus.global-config.db-config.table-underline=true
```

### MybatisPlusConfig 配置类

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
```

## 常见操作示例

### 1. 简单查询

```java
// 按 ID 查询
Seed seed = seedService.getById(1L);

// 查询所有
List<Seed> seeds = seedService.list();

// 条件查询
LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Seed::getCropType, "水稻");
List<Seed> riceSeeds = seedService.list(wrapper);
```

### 2. 分页查询

```java
Page<Seed> page = new Page<>(1, 10);
LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
wrapper.orderByDesc(Seed::getApprovalYear);
IPage<Seed> result = seedService.page(page, wrapper);
```

### 3. 插入数据

```java
Seed seed = new Seed();
seed.setVarietyName("新品种");
seed.setCropType("水稻");
seedService.save(seed);
```

### 4. 批量插入

```java
List<Seed> seeds = new ArrayList<>();
// 添加多个 seed 对象
seedService.saveBatch(seeds);
```

### 5. 更新数据

```java
Seed seed = new Seed();
seed.setId(1L);
seed.setVarietyName("更新的品种名");
seedService.updateById(seed);
```

### 6. 删除数据

```java
// 按 ID 删除
seedService.removeById(1L);

// 条件删除
LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Seed::getCropType, "棉花");
seedService.remove(wrapper);
```

## 性能优化建议

### 1. 使用 LambdaQueryWrapper

相比 QueryWrapper，LambdaQueryWrapper 提供了类型安全的字段引用，避免了硬编码字段名。

### 2. 合理使用索引

在 `schema.sql` 中为常用查询字段建立索引：

```sql
CREATE TABLE seeds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crop_type VARCHAR(255) NOT NULL,
    approval_year INT NOT NULL,
    company VARCHAR(255) NOT NULL,
    INDEX idx_crop_type (crop_type),
    INDEX idx_approval_year (approval_year),
    INDEX idx_company (company)
);
```

### 3. 分页查询

始终使用分页查询，避免一次性加载大量数据：

```java
Page<Seed> page = new Page<>(pageNum, pageSize);
IPage<Seed> result = seedService.page(page, wrapper);
```

### 4. 选择性字段查询

如果只需要部分字段，使用 `select()` 方法：

```java
LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
wrapper.select(Seed::getId, Seed::getVarietyName, Seed::getCropType);
List<Seed> seeds = seedService.list(wrapper);
```

## 迁移检查清单

- [ ] 更新 `pom.xml` 依赖
- [ ] 修改实体类注解（`@TableName`, `@TableId`, `@TableField`）
- [ ] 创建 Mapper 接口（继承 `BaseMapper`）
- [ ] 重构 Service 类（继承 `ServiceImpl`）
- [ ] 创建 `MybatisPlusConfig` 配置类
- [ ] 创建 `MetaObjectHandler` 实现类
- [ ] 更新 `application.properties` 配置
- [ ] 测试所有 CRUD 操作
- [ ] 测试分页和条件查询
- [ ] 更新前端 API 集成代码

## 常见问题

### Q: MyBatis-Plus 和 JPA 的主要区别是什么？
A: 
- **JPA** 是 ORM 标准，自动生成 SQL，学习曲线平缓但灵活性有限。
- **MyBatis-Plus** 是 MyBatis 的增强，保留了 SQL 的灵活性，同时提供了强大的条件构造器和代码生成工具。

### Q: 如何处理复杂的多表联接查询？
A: 在 Mapper 中使用自定义 SQL：
```java
@Select("SELECT s.*, c.name as company_name FROM seeds s " +
        "LEFT JOIN companies c ON s.company_id = c.id " +
        "WHERE s.crop_type = #{cropType}")
List<SeedVO> selectWithCompany(@Param("cropType") String cropType);
```

### Q: 如何进行性能调优？
A: 
1. 为常用查询字段建立索引
2. 使用分页查询
3. 选择性字段查询（避免 SELECT *）
4. 考虑使用缓存（Redis）

## 参考资源

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis-Plus GitHub](https://github.com/baomidou/mybatis-plus)
- [条件构造器详解](https://baomidou.com/pages/10c804ab/)
