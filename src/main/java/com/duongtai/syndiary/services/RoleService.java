package com.duongtai.syndiary.services;

import com.duongtai.syndiary.entities.ResponseObject;
import com.duongtai.syndiary.entities.Role;
import org.springframework.http.ResponseEntity;

public interface RoleService {

    ResponseEntity<ResponseObject> saveNewRole(Role role);
    Role getRoleByName(String name);
}
