CREATE TABLE Customer (
    id varchar(32) not null,
    name text,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE Map (
    id varchar(32) not null,
    customerId varchar(32) not null,
    name text,
    svgPath text,
    width float,
    height float,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES Customer(id)
);

CREATE TABLE Coordinate (
    id varchar(32) not null,
    x float,
    y float,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE Router (
    id varchar(32) not null,
    coordinateId varchar(32) not null,
    mapId varchar(32) not null,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (coordinateId) REFERENCES Coordinate(id),
    FOREIGN KEY (mapId) REFERENCES Map(id)
);

CREATE TABLE TrackedDevice (
    id varchar(32) not null,
    mapId varchar(32) not null,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (mapId) REFERENCES Map(id)
);

CREATE TABLE TrackingPoint (
    id varchar(32) not null,
    trackedDeviceId varchar(32) not null,
    coordinateId varchar(32) not null,
    createdAt timestamp,
    lastUpdatedAt timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (trackedDeviceId) REFERENCES TrackedDevice(id)
);
