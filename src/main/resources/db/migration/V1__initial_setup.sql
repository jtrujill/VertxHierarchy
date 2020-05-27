CREATE TABLE hierarchy (
    id VARCHAR(36) NOT NULL,
    name varchar(255) NOT NULL,
    parent_id VARCHAR(36) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY fk_hierarchy_hierarchy (parent_id) REFERENCES hierarchy(id) ON DELETE CASCADE ON UPDATE CASCADE
)