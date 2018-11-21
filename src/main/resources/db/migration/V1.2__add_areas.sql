CREATE TABLE Area (
    id varchar(36) not null,
    mapId varchar(36) not null,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id)
);

ALTER TABLE Area
ADD CONSTRAINT area_fk_mapId
FOREIGN KEY (mapId)
REFERENCES Map(id)
ON DELETE CASCADE;

ALTER TABLE Coordinate
ADD areaId varchar(36) null;

ALTER TABLE Coordinate
ADD CONSTRAINT coordinate_fk_areaId
FOREIGN KEY (areaId)
REFERENCES Area(id)
ON DELETE CASCADE;
