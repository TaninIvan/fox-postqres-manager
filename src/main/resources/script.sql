-- загрузка в Базу данных дополнительного модуля
CREATE EXTENSION dblink;

-- функция создания базы данных
CREATE OR REPLACE FUNCTION create_db(text)
  RETURNS void LANGUAGE sql AS
$func$
SELECT dblink_exec('port=5432 dbname=postgres user=postgres password=postgres'
                  ,'CREATE DATABASE ' || quote_ident($1));
SELECT dblink_exec('port=5432 user=postgres password=postgres dbname=' || quote_ident($1)
                  ,'CREATE TABLE Fox (
		id int PRIMARY KEY,
		name text UNIQUE,
		owner_fio text,
		birthday date NOT NULL,
		color text NOT NULL
	);');
$func$;

-- функция удаления базы данных
CREATE OR REPLACE FUNCTION drop_db(text)
  RETURNS void LANGUAGE sql AS
$func$
SELECT dblink_exec('port=5432 dbname=postgres user=postgres password=postgres'
                  ,'DROP DATABASE ' || quote_ident($1))
$func$;

-- функция очистки таблицы
CREATE OR REPLACE FUNCTION clear_table() RETURNS void
LANGUAGE sql AS
$func$
DELETE FROM ONLY Fox;
$func$;

-- функция вставки новой строки
CREATE OR REPLACE FUNCTION new_fox(id integer, name text, owner_fio text, birthday date, color text) RETURNS void
LANGUAGE sql AS
$func$
INSERT INTO Fox VALUES (id, name, owner_fio, birthday, color);
$func$;

-- функция удаления лисы
CREATE OR REPLACE FUNCTION delete_fox(newname text) RETURNS void
LANGUAGE sql AS
$func$
DELETE FROM Fox AS f WHERE f.name = newname;
$func$;

-- функция поиска лисы
CREATE OR REPLACE FUNCTION find_fox(newname text) RETURNS fox
LANGUAGE sql AS
$func$
SELECT * FROM Fox AS f WHERE f.name = newname;
$func$;

-- функция обновления лисы
CREATE OR REPLACE FUNCTION update_fox(newid integer, newname text, newowner_fio text, newbirthday date, newcolor text) RETURNS void
LANGUAGE sql AS
$func$
UPDATE fox SET name=newname, owner_fio=newowner_fio, birthday=newbirthday, color=newcolor WHERE id = newid;
$func$;

-- функция создания нового пользователя
CREATE OR REPLACE FUNCTION create_user(
    u_name text,
    u_password text,
	u_attr text)
  RETURNS void AS
$BODY$
BEGIN
	EXECUTE 'CREATE ROLE ' || "u_name" || ' PASSWORD ''' || u_password ||''' ' || $3;
END;
$BODY$
  LANGUAGE plpgsql security definer;
;;