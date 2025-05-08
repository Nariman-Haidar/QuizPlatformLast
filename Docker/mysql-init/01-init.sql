-- Create quizservice_db if it doesn't exist
CREATE DATABASE IF NOT EXISTS quizservice_db;

-- Grant privileges to root user for both databases
GRANT ALL PRIVILEGES ON userservice_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON quizservice_db.* TO 'root'@'%';
FLUSH PRIVILEGES; 