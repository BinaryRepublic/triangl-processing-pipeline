ALTER TABLE TrackedDevice
MODIFY id varchar(100);

ALTER TABLE TrackingPoint
MODIFY trackedDeviceId varchar(100);