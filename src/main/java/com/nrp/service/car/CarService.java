package com.nrp.service.car;

import com.nrp.domainobject.CarDO;
import com.nrp.exception.CarAlreadyInUseException;
import com.nrp.exception.ConstraintsViolationException;
import com.nrp.exception.EntityNotFoundException;

public interface CarService {

    CarDO find(String licensePlate) throws EntityNotFoundException;

    CarDO create(CarDO carDO) throws ConstraintsViolationException;

    void delete(String licensePlate) throws EntityNotFoundException, ConstraintsViolationException;

    void addDriver(Long driverId, String licensePlate) throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException;

    void deleteDriver(Long driverId, String licensePlate) throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException;

    Iterable<CarDO> findAll();
}
