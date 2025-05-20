-- Thêm cột is_deleted vào bảng jobs
ALTER TABLE jobs ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng resumes
ALTER TABLE resumes ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng cv
ALTER TABLE cv ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng users
ALTER TABLE users ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng companies
ALTER TABLE companies ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng skills
ALTER TABLE skills ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng roles
ALTER TABLE roles ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng promotions
ALTER TABLE promotions ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Thêm cột is_deleted vào bảng subscription_packages
ALTER TABLE subscription_packages ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Cập nhật dữ liệu hiện có
UPDATE jobs SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE resumes SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE cv SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE users SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE companies SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE skills SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE roles SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE promotions SET is_deleted = FALSE WHERE is_deleted IS NULL;
UPDATE subscription_packages SET is_deleted = FALSE WHERE is_deleted IS NULL;

-- Tạo index
CREATE INDEX idx_jobs_is_deleted ON jobs(is_deleted);
CREATE INDEX idx_resumes_is_deleted ON resumes(is_deleted);
CREATE INDEX idx_cv_is_deleted ON cv(is_deleted);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);
CREATE INDEX idx_companies_is_deleted ON companies(is_deleted);
CREATE INDEX idx_skills_is_deleted ON skills(is_deleted);
CREATE INDEX idx_roles_is_deleted ON roles(is_deleted);
CREATE INDEX idx_promotions_is_deleted ON promotions(is_deleted);
CREATE INDEX idx_subscription_packages_is_deleted ON subscription_packages(is_deleted); 