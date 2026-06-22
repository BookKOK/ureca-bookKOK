package com.bookkok.admin.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.user.entity.User;

public interface AdminUserRepository extends JpaRepository<User, Long> {


}