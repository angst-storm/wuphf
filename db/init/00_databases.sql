SELECT 'CREATE DATABASE ums'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ums')\gexec

SELECT 'CREATE DATABASE messages'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'messages')\gexec
