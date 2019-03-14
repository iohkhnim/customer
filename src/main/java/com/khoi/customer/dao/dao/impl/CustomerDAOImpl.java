package com.khoi.customer.dao.dao.impl;

import com.khoi.basecrud.dao.dao.impl.BaseDAOImpl;
import com.khoi.customer.dao.ICustomerDAO;
import com.khoi.customer.dto.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CustomerDAOImpl extends BaseDAOImpl<Customer, Integer> implements ICustomerDAO {

}
