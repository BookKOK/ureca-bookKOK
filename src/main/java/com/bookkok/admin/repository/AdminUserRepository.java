package com.bookkok.admin.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.member.entity.Member;

public interface AdminUserRepository extends JpaRepository<Member, String> {


}