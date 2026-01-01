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
