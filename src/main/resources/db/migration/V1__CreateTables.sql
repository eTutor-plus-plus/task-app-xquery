-- !!! DO NOT MODIFY THIS MIGRATION !!!
-- IF YOU WANT TO ADD/CHANGE SOMETHING CREATE A NEW MIGRATION, e.g. V2__add_column_to_table.sql

CREATE TYPE task_status AS ENUM ('draft', 'ready_for_approval', 'approved');
CREATE TYPE submission_mode AS ENUM ('run', 'diagnose', 'submit');
CREATE TYPE grading_strategy AS ENUM ('each', 'group', 'ko');

CREATE CAST (CHARACTER VARYING as task_status) WITH INOUT AS IMPLICIT;
CREATE CAST (CHARACTER VARYING as submission_mode) WITH INOUT AS IMPLICIT;
CREATE CAST (CHARACTER VARYING as grading_strategy) WITH INOUT AS IMPLICIT;

CREATE TABLE task_group
(
    id           BIGINT      NOT NULL,
    status       TASK_STATUS NOT NULL,
    doc_diagnose TEXT        NOT NULL,
    doc_submit   TEXT        NOT NULL,
    CONSTRAINT task_group_pk PRIMARY KEY (id)
);

CREATE TABLE task
(
    id                                 BIGINT           NOT NULL,
    max_points                         NUMERIC(7, 2)    NOT NULL,
    status                             TASK_STATUS      NOT NULL,
    task_group_id                      BIGINT           NOT NULL,
    solution                           TEXT             NOT NULL,
    sorting                            VARCHAR(2000)[],
    missing_node_penalty               NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    missing_node_strategy              grading_strategy NOT NULL DEFAULT 'ko',
    superfluous_node_penalty           NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    superfluous_node_strategy          grading_strategy NOT NULL DEFAULT 'ko',
    incorrect_text_penalty             NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    incorrect_text_strategy            grading_strategy NOT NULL DEFAULT 'ko',
    displaced_node_penalty             NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    displaced_node_strategy            grading_strategy NOT NULL DEFAULT 'ko',
    missing_attribute_penalty          NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    missing_attribute_strategy         grading_strategy NOT NULL DEFAULT 'ko',
    superfluous_attribute_penalty      NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    superfluous_attribute_strategy     grading_strategy NOT NULL DEFAULT 'ko',
    incorrect_attribute_value_penalty  NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    incorrect_attribute_value_strategy grading_strategy NOT NULL DEFAULT 'ko',
    CONSTRAINT task_pk PRIMARY KEY (id),
    CONSTRAINT task_task_group_fk FOREIGN KEY (task_group_id) REFERENCES task_group (id)
        ON DELETE CASCADE,
    CONSTRAINT task_missing_node_penalty_ck CHECK (missing_node_penalty >= 0),
    CONSTRAINT task_superfluous_node_penalty_ck CHECK (superfluous_node_penalty >= 0),
    CONSTRAINT task_incorrect_text_penalty_ck CHECK (incorrect_text_penalty >= 0),
    CONSTRAINT task_displaced_node_penalty_ck CHECK (displaced_node_penalty >= 0),
    CONSTRAINT task_missing_attribute_penalty_ck CHECK (missing_attribute_penalty >= 0),
    CONSTRAINT task_superfluous_attribute_penalty_ck CHECK (superfluous_attribute_penalty >= 0),
    CONSTRAINT task_incorrect_attribute_value_penalty_ck CHECK (incorrect_attribute_value_penalty >= 0)
);

CREATE TABLE submission
(
    id                UUID            NOT NULL DEFAULT gen_random_uuid(),
    user_id           VARCHAR(255),
    assignment_id     VARCHAR(255),
    task_id           BIGINT,
    submission_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    language          VARCHAR(2)      NOT NULL DEFAULT 'en',
    mode              submission_mode NOT NULL,
    feedback_level    INT             NOT NULL,
    evaluation_result JSONB,
    submission        TEXT,
    CONSTRAINT submission_pk PRIMARY KEY (id),
    CONSTRAINT submission_task_fk FOREIGN KEY (task_id) REFERENCES task (id)
        ON DELETE CASCADE
);
