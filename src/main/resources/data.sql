-- 카테고리 초기 데이터 삽입
INSERT IGNORE INTO category (name) VALUES
    ('IT/개발'),
    ('외국어'),
    ('자격증'),
    ('문학/책'),
    ('음악/예술'),
    ('경영/경제'),
    ('수학/과학'),
    ('취업/면접'),
    ('자기계발'),
    ('취미/여가'),
    ('시사/트렌드'),
    ('운동/건강');

-- 기존 Study 레코드의 isPrivate가 null인 경우 false로 업데이트
UPDATE study SET is_private = false WHERE is_private IS NULL;