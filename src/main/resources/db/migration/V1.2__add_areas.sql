CREATE TABLE Area (
    id varchar(36) not null,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id)
);

ALTER TABLE Coordinate
ADD areaId varchar(36) null;

ALTER TABLE Coordinate
ADD CONSTRAINT coordinate_fk_areaId
FOREIGN KEY (areaId)
REFERENCES Area(id)
ON DELETE CASCADE;
