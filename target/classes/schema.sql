-- Create seeds table
CREATE TABLE IF NOT EXISTS seeds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    variety_name VARCHAR(255) NOT NULL UNIQUE,
    approval_number VARCHAR(255) NOT NULL UNIQUE,
    approval_year INT NOT NULL,
    approval_region VARCHAR(255) NOT NULL,
    crop_type VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    company_phone VARCHAR(20),
    company_address VARCHAR(500),
    description VARCHAR(500),
    characteristics JSON,
    adaptive_regions JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_crop_type (crop_type),
    INDEX idx_approval_region (approval_region),
    INDEX idx_approval_year (approval_year),
    INDEX idx_company (company),
    FULLTEXT INDEX ft_variety_name (variety_name),
    FULLTEXT INDEX ft_approval_number (approval_number)
);

-- Create search history table
CREATE TABLE IF NOT EXISTS search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    query VARCHAR(500) NOT NULL,
    search_type VARCHAR(50) DEFAULT 'keyword',
    result_count INT DEFAULT 0,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_query (query),
    INDEX idx_created_at (created_at),
    INDEX idx_user_query (user_id, query)
);

-- Create seed approval details table
CREATE TABLE IF NOT EXISTS seed_approval_details (
    id VARCHAR(50) PRIMARY KEY,
    approval_number VARCHAR(50) UNIQUE NOT NULL,
    variety_name VARCHAR(200) NOT NULL,
    crop_name VARCHAR(100) NOT NULL,
    approval_year INT NOT NULL,
    
    -- 申请信息
    applicant VARCHAR(500) NOT NULL,
    breeder VARCHAR(500) NOT NULL,
    variety_source TEXT,
    
    -- 法规信息
    is_gmo BOOLEAN DEFAULT FALSE,
    license_info TEXT,
    variety_rights TEXT,
    approval_authority VARCHAR(200) NOT NULL,
    
    -- 特征特性
    detailed_description TEXT,
    growth_period VARCHAR(100),
    plant_height VARCHAR(50),
    resistance TEXT,
    quality_traits TEXT,
    
    -- 产量表现
    yield_summary TEXT,
    comparison_data TEXT,
    
    -- 栽培技术
    cultivation_requirements TEXT,
    cultivation_techniques TEXT,
    cultivation_precautions TEXT,
    
    -- 审定意见
    approval_opinion TEXT,
    suitable_regions JSON,
    planting_restrictions TEXT,
    
    -- 元数据
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    
    -- 索引
    INDEX idx_approval_number (approval_number),
    INDEX idx_variety_name (variety_name),
    INDEX idx_applicant (applicant),
    INDEX idx_breeder (breeder),
    INDEX idx_crop_name (crop_name),
    INDEX idx_approval_year (approval_year),
    INDEX idx_is_gmo (is_gmo)
);

-- Create yield performance data table
CREATE TABLE IF NOT EXISTS yield_performance_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    approval_id VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    location VARCHAR(200) NOT NULL,
    yield_value DECIMAL(10,2) NOT NULL,
    yield_unit VARCHAR(20) NOT NULL,
    comparison_variety VARCHAR(200),
    comparison_yield DECIMAL(10,2),
    
    FOREIGN KEY (approval_id) REFERENCES seed_approval_details(id),
    INDEX idx_approval_year (approval_id, year),
    INDEX idx_location (location)
);
