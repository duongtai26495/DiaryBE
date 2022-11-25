package com.duongtai.syndiary.services.impl;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.ResponseObject;
import com.duongtai.syndiary.entities.Role;
import com.duongtai.syndiary.repositories.RoleRepository;
import com.duongtai.syndiary.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ResponseEntity<ResponseObject> saveNewRole(Role role) {
        if(roleRepository.getRoleByName(role.getName())!=null){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject(Snippets.FAILED,String.format(Snippets.ROLE_EXIST,role.getName()),null)
            );
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS,String.format(Snippets.CREATE_ROLE_SUCCESS,role.getName()),roleRepository.save(role))
            );
        }

    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.getRoleByName(name);
    }
}
