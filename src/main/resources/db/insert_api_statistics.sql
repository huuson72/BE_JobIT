-- Insert API statistics endpoints
INSERT INTO api_statistics (api_path, created_at, method, module, name) VALUES
('/api/v1/admin/statistics', NOW(), 'GET', 'ADMIN', 'Get admin statistics'),
('/api/v1/admin/statistics/jobs', NOW(), 'GET', 'ADMIN', 'Get job statistics'),
('/api/v1/admin/statistics/users', NOW(), 'GET', 'ADMIN', 'Get user statistics'),
('/api/v1/admin/statistics/companies', NOW(), 'GET', 'ADMIN', 'Get company statistics'),
('/api/v1/admin/statistics/cvs', NOW(), 'GET', 'ADMIN', 'Get CV statistics'); 