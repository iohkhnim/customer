package com.khoi.customer.dao.dao.impl;

import com.khoi.basecrud.dao.dao.impl.BaseDAOImpl;
import com.khoi.customer.dao.IUserDAO;
import com.khoi.customer.dto.User;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDAOImpl extends BaseDAOImpl<User, Integer> implements IUserDAO {

  /**
   *  <p>Retrieve user's information with provided username</p>
   * @param username Username of customer
   * @return Customer's information
   */
  @Override
  public User findByUsername(String username) {
    String hql = "FROM User u WHERE u.username = :username";
    Query query = entityManager.createQuery(hql);
    query.setParameter("username", username);
    return (User) query.setMaxResults(1).getSingleResult();
  }

  /**
   *  <p>Retrieve Customer ID of provided username</p>
   * @param username username of customer
   * @return id of that username
   */
  @Override
  public int getCustomerIdByUsername(String username) {
    String hql = "SELECT u.customer_id FROM User u WHERE u.username = :username";
    Query query = entityManager.createQuery(hql);
    query.setParameter("username", username);
    return Integer.parseInt(query.setMaxResults(1).getSingleResult().toString());
  }
}
