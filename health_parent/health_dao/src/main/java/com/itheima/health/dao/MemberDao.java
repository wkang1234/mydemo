package com.itheima.health.dao;

import com.itheima.health.pojo.Member;

public interface MemberDao {


    Member findMemberByTelephone(String telephone);

    void add(Member member);

    Integer findMemberCountsByRegTime(String regTime);

    Integer findTodayNewMember(String today);

    Integer findTotalMember();

    Integer findThisNewMember(String date);
}
