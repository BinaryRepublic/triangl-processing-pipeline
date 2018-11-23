ALTER TABLE Router
MODIFY COLUMN id VARCHAR(17);

ALTER TABLE Coordinate
ADD routerId varchar(17) null;
ALTER TABLE Coordinate
ADD CONSTRAINT coordinate_fk_routerId
FOREIGN KEY (routerId)
REFERENCES Router(id)
ON DELETE CASCADE;

ALTER TABLE Coordinate
ADD trackingPointId varchar(36) null;
ALTER TABLE Coordinate
ADD CONSTRAINT coordinate_fk_trackingPointId
FOREIGN KEY (trackingPointId)
REFERENCES TrackingPoint(id)
ON DELETE CASCADE;

UPDATE Coordinate c, Router r
SET c.routerId = r.id
WHERE c.id = r.coordinateId;

/* added temporarily to make the next query faster */
ALTER TABLE TrackingPoint
ADD CONSTRAINT trackingPoint_fk_coordinateId
FOREIGN KEY (coordinateId)
REFERENCES Coordinate(id);

UPDATE Coordinate c, TrackingPoint t
SET c.trackingPointId = t.id
WHERE c.id = t.coordinateId;

ALTER TABLE Router
DROP FOREIGN KEY router_fk_coordinateId;
ALTER TABLE Router
DROP COLUMN coordinateId;

ALTER TABLE TrackingPoint
DROP FOREIGN KEY trackingPoint_fk_coordinateId;
ALTER TABLE TrackingPoint
DROP COLUMN coordinateId;

DELIMITER //
CREATE TRIGGER InsertCoordinateForeignKeyNotNull BEFORE INSERT ON Coordinate
FOR EACH ROW BEGIN
  IF (NEW.routerId IS NULL AND NEW.areaId IS NULL AND NEW.trackingPointId IS NULL) THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'One of \'routerId\', \'areaId\' or \'trackingPointId\' must not be null';
  END IF;
END//
CREATE TRIGGER UpdateCoordinateForeignKeyNotNull BEFORE UPDATE ON Coordinate
FOR EACH ROW BEGIN
  IF (NEW.routerId IS NULL AND NEW.areaId IS NULL AND NEW.trackingPointId IS NULL) THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'One of \'routerId\', \'areaId\' or \'trackingPointId\' must not be null';
  END IF;
END//
DELIMITER ;