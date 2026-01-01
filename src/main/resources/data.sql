-- Insert sample seed data
INSERT INTO seeds (variety_name, approval_number, approval_year, approval_region, crop_type, company, company_phone, company_address, description, characteristics, adaptive_regions) VALUES
('中国水稻 1 号', 'ZZ2023001', 2023, '全国', '水稻', '中国农业科学院', '010-82109801', '北京市朝阳区', '中国水稻 1 号是由中国农业科学院培育的优质高产水稻品种，具有抗病性强、产量高、品质优等特点。', 
 '{"growthPeriod":"120-130 天","yield":"600-700 kg/亩","diseaseResistance":"抗稻瘟病、抗纹枯病","qualityTraits":"优质、高产"}', 
 '["华东", "华中", "华南"]'),

('豫麦 70 号', 'ZZ2023002', 2023, '黄淮地区', '小麦', '河南农业大学', '0371-63558015', '河南省郑州市', '豫麦 70 号是河南农业大学选育的强筋小麦品种，适合黄淮冬麦区种植。', 
 '{"growthPeriod":"220-230 天","yield":"500-600 kg/亩","diseaseResistance":"抗条纹花叶病、抗赤霉病","qualityTraits":"强筋、高产"}', 
 '["黄淮", "华北"]'),

('郑单 958', 'ZZ2023003', 2023, '全国', '玉米', '郑州农业研究所', '0371-65730928', '河南省郑州市', '郑单 958 是我国应用最广泛的玉米品种之一，具有高产、稳产、适应性强等优点。', 
 '{"growthPeriod":"110-115 天","yield":"700-800 kg/亩","diseaseResistance":"抗南方锈病、抗玉米螟","qualityTraits":"高产、稳产"}', 
 '["华北", "华东", "华中"]'),

('晋麦 78', 'ZZ2023004', 2023, '华北地区', '小麦', '山西农业大学', '0351-2288388', '山西省太原市', '晋麦 78 是山西农业大学选育的抗旱小麦品种，特别适合华北地区种植。', 
 '{"growthPeriod":"225-235 天","yield":"480-580 kg/亩","diseaseResistance":"抗条纹花叶病、抗白粉病","qualityTraits":"中筋、抗旱"}', 
 '["华北", "西北"]'),

('隆平高科 Y 两优 900', 'ZZ2023005', 2023, '全国', '水稻', '隆平高科', '0731-84696000', '湖南省长沙市', '隆平高科 Y 两优 900 是隆平高科公司推出的超级杂交水稻品种，具有产量高、品质优、抗性强等特点。', 
 '{"growthPeriod":"125-135 天","yield":"650-750 kg/亩","diseaseResistance":"抗稻瘟病、抗纹枯病","qualityTraits":"优质、高产、多抗"}', 
 '["华东", "华中", "华南"]');
