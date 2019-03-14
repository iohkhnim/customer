package com.khoi.customer.service.service.impl;

import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.service.ICustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer> implements
    ICustomerService {

}
