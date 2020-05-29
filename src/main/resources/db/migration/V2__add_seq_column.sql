# Hack to be able to do bulk insert while also using guid
ALTER TABLE hierarchy ADD COLUMN batch_seq VARCHAR(36) NOT NULL;

ALTER TABLE hierarchy ADD INDEX (name);

ALTER TABLE hierarchy ADD `unique_parentid_name` VARCHAR(300) NOT NULL DEFAULT '-';

# Required since mysql doesn't like null values in a unique index
ALTER TABLE hierarchy ADD UNIQUE KEY `unique_parentid_name` (`unique_parentid_name`);

DELIMITER ;;
CREATE TRIGGER hierarchy_before_insert BEFORE INSERT ON hierarchy
    FOR EACH ROW
BEGIN
    SET NEW.id = UUID();
    SET NEW.unique_parentid_name = CONCAT(IFNULL(NEW.parent_id, ''), '-', IFNULL(NEW.name, ''));
END;;
DELIMITER ;

DELIMITER ;;
CREATE TRIGGER hierarchy_before_update BEFORE UPDATE ON hierarchy
    FOR EACH ROW
BEGIN
    IF NEW.name != OLD.name THEN
        SET NEW.unique_parentid_name = CONCAT(IFNULL(NEW.parent_id, ''), '-', IFNULL(NEW.name, ''));
    END IF;
END;;
DELIMITER ;

