package org.crazymages.bankingspringproject.service.database;

import org.crazymages.bankingspringproject.entity.Manager;
import org.crazymages.bankingspringproject.entity.enums.ManagerStatus;

import java.util.List;
import java.util.UUID;

/**
 * A service interface for managing Manager entities in the database.
 * It provides methods for creating, retrieving, updating, and deleting Manager entities.
 */
public interface ManagerDatabaseService {

    /**
     * Creates a new Manager entity in the database.
     *
     * @param manager The Manager entity to be created.
     */
    void create(Manager manager);

    /**
     * Retrieves all Manager entities from the database.
     *
     * @return A list of all Manager entities.
     */
    List<Manager> findAll();

    /**
     * Retrieves all non-deleted Manager entities from the database.
     *
     * @return A list of all non-deleted Manager entities.
     */
    List<Manager> findAllNotDeleted();

    /**
     * Retrieves all deleted Manager entities from the database.
     *
     * @return A list of all deleted Manager entities.
     */
    List<Manager> findDeletedAccounts();

    /**
     * Retrieves a Manager entity from the database by its UUID.
     *
     * @param uuid The UUID of the Manager to retrieve.
     * @return The Manager entity with the specified UUID, or null if not found.
     */
    Manager findById(UUID uuid);

    /**
     * Updates a Manager entity in the database with the specified UUID.
     *
     * @param uuid    The UUID of the Manager to update.
     * @param manager The updated Manager entity.
     */
    void update(UUID uuid, Manager manager);

    /**
     * Deletes a Manager entity from the database with the specified UUID.
     *
     * @param uuid The UUID of the Manager to delete.
     */
    void delete(UUID uuid);

    /**
     * Retrieves all Manager entities from the database sorted by the quantity of associated clients,
     * filtered by the specified ManagerStatus.
     *
     * @param status The ManagerStatus to filter by.
     * @return A list of Manager entities sorted by the quantity of associated clients.
     */
    List<Manager> findManagersSortedByClientQuantityWhereManagerStatusIs(ManagerStatus status);

    /**
     * Retrieves all Manager entities from the database sorted by the quantity of associated products,
     * filtered by the specified ManagerStatus.
     *
     * @param status The ManagerStatus to filter by.
     * @return A list of Manager entities sorted by the quantity of associated products.
     */
    List<Manager> findManagersSortedByProductQuantityWhereManagerStatusIs(ManagerStatus status);

    /**
     * Retrieves the first Manager entity from the specified list of active Managers.
     *
     * @param activeManagers The list of active Managers.
     * @return The first Manager entity from the list, or null if the list is empty.
     */
    Manager getFirstManager(List<Manager> activeManagers);
}
