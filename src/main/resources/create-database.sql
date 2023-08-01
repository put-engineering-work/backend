CREATE DATABASE codepred;
create user postgres with encrypted password 'admin';
grant all privileges on database codepred to postgres;
