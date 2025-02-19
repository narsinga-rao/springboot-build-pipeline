package com.nrp.service.driver;

import com.nrp.domainobject.CarDO;
import com.nrp.domainobject.DriverDO;
import com.nrp.domainvalue.OnlineStatus;
import com.nrp.exception.ConstraintsViolationException;
import com.nrp.exception.EntityNotFoundException;
import java.util.List;

public interface DriverService
{

    DriverDO find(Long driverId) throws EntityNotFoundException;

    DriverDO create(DriverDO driverDO) throws ConstraintsViolationException;

    void delete(Long driverId) throws EntityNotFoundException;

    void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException, ConstraintsViolationException;

    void updateCar(long driverId, CarDO carDO) throws EntityNotFoundException, ConstraintsViolationException;

    List<DriverDO> find(OnlineStatus onlineStatus);

    Iterable<DriverDO> findAll();

}
