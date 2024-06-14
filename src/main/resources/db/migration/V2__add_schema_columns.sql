ALTER TABLE task
    ADD COLUMN solution_elements   TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN solution_attributes TEXT[] NOT NULL DEFAULT '{}';
