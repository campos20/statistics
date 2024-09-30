import os
import re

import mysql.connector
from misc.python.util.log_util import log
import datetime
import inspect

user = os.environ.get("DB_USERNAME", "root")
host = os.environ.get("DB_HOST", "localhost")
password = os.environ.get("DB_PASSWORD", "")
database = os.environ.get("DB_DATABASE", "wca_development")


def get_database_connection():
    return mysql.connector.connect(
        user=user, password=password, host=host, database=database
    )


def __get_file_name(sql_file_name):
    return f"misc/python/sql/{sql_file_name}.sql"


def sql_replace(query_str):
    """Replace sql-onic :PLACEHOLDER with pythonic %(PLACEHOLDER)s"""
    for y in re.findall(":[a-zA-Z]{1}[a-zA-Z0-9_]+", query_str):
        query_str = query_str.replace(y, f"%({y[1:]})s")
    return query_str


def execute_query(sql_file_name, values=()):
    try:
        with open(__get_file_name(sql_file_name), encoding="utf-8") as query:
            query_str = sql_replace(query.read())
            cursor.execute(query_str, values)

            return cursor.fetchall()

    except Exception as e:
        raise Exception(e)


def start_process():
    cnx = get_database_connection()
    cursor = cnx.cursor()

    path = get_caller_filename()
    print(path)

    cursor.execute("delete from statistics_control where path = '%s';" % path)
    cursor.execute(
        "insert into statistics_control (`path`, status, created_at) VALUES('%s', 'STARTED', '%s');"
        % (path, datetime.datetime.now())
    )
    cnx.commit()
    cnx.close()


def complete_process():
    cnx = get_database_connection()
    cursor = cnx.cursor()

    path = get_caller_filename()
    cursor.execute(
        "update statistics_control set status = 'COMPLETED', completed_at = '%s' where path = '%s';"
        % (datetime.datetime.now(), path)
    )
    cnx.commit()
    cnx.close()


def error_process(message):
    cnx = get_database_connection()
    cursor = cnx.cursor()
    sql_sanitized = message.replace("'", "")

    path = get_caller_filename()

    cursor.execute(
        "update statistics_control set status = 'FAILED', message = '%s' where path = '%s';"
        % (sql_sanitized[:200], path)
    )
    cnx.commit()
    cnx.close()


def get_caller_filename():
    caller = inspect.stack()[2]
    caller_filename = caller.filename
    return caller_filename.split("/")[-1].split(".")[0]


cnx = get_database_connection()
cursor = cnx.cursor(dictionary=True)
