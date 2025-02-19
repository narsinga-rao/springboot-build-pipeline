package com.nrp.service.driver;

import com.nrp.dataaccessobject.DriverRepository;
import com.nrp.domainobject.CarDO;
import com.nrp.domainobject.DriverDO;
import com.nrp.domainvalue.GeoCoordinate;
import com.nrp.domainvalue.OnlineStatus;
import com.nrp.exception.ConstraintsViolationException;
import com.nrp.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some driver specific things.
 * <p/>
 */
@Service
public class DefaultDriverService implements DriverService
{

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(DefaultDriverService.class);

    private final DriverRepository driverRepository;


    public DefaultDriverService(final DriverRepository driverRepository)
    {
        this.driverRepository = driverRepository;
    }


    /**
     * Selects a driver by id.
     *
     * @param driverId
     * @return found driver
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    @Transactional
    public DriverDO find(Long driverId) throws EntityNotFoundException
    {
        return findDriverChecked(driverId);
    }


    /**
     * Creates a new driver.
     *
     * @param driverDO
     * @return
     * @throws ConstraintsViolationException if a driver already exists with the given username, ... .
     */
    @Override
    @Transactional
    public DriverDO create(DriverDO driverDO) throws ConstraintsViolationException
    {
        DriverDO driver;
        try
        {
            driver = driverRepository.save(driverDO);
        }
        catch (DataIntegrityViolationException e)
        {
            LOG.warn("Some constraints are thrown due to driver creation", e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return driver;
    }


    /**
     * Deletes an existing driver by id.
     *
     * @param driverId
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    @Transactional
    public void delete(Long driverId) throws EntityNotFoundException
    {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setDeleted(true);
    }


    /**
     * Update the location for a driver.
     *
     * @param driverId
     * @param longitude
     * @param latitude
     * @throws EntityNotFoundException
     */
    @Override
    @Transactional
    public void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException, ConstraintsViolationException {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setCoordinate(new GeoCoordinate(latitude, longitude));
        create(driverDO);
    }

    @Override
    @Transactional
    public void updateCar(long driverId, CarDO carDO) throws EntityNotFoundException, ConstraintsViolationException {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setCar(carDO);
        create(driverDO);
    }


    /**
     * Find all drivers by online state.
     *
     * @param onlineStatus
     */
    @Override
    public List<DriverDO> find(OnlineStatus onlineStatus)
    {
        return driverRepository.findByOnlineStatus(onlineStatus);
    }

    @Override
    public Iterable<DriverDO> findAll()
    {
        return driverRepository.findAll();
    }


    private DriverDO findDriverChecked(Long driverId) throws EntityNotFoundException
    {
        Optional<DriverDO> driverDO = driverRepository.findById(driverId);
        if (driverDO.isEmpty())
        {
            throw new EntityNotFoundException("Could not find entity with id: " + driverId);
        }
        return driverDO.get();
    }

}
