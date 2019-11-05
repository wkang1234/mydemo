package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import com.itheima.health.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CheckItemServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:57
 * @Version V1.0
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    // 会员
    @Autowired
    MemberDao memberDao;

    @Override
    public Member findMemberByTelephone(String telephone) {
        return memberDao.findMemberByTelephone(telephone);
    }

    @Override
    public void addMember(Member member) {
        // 新增会员的时候，对密码进行加密
        if(member!=null && member.getPassword()!=null){
            member.setPassword(MD5Utils.md5(member.getPassword())); // 密文保存到数据库，保证安全
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountsByRegTime(List<String> months) {
        // 组织数据
        List<Integer> memberCounts = new ArrayList<>();
        // 格式：[2018-12, 2019-01, 2019-02, 2019-03, 2019-04, 2019-05, 2019-06, 2019-07, 2019-08, 2019-09, 2019-10, 2019-11]
        for (String month : months) {
            String regTime = month+"-32";
            Integer counts = memberDao.findMemberCountsByRegTime(regTime);
            memberCounts.add(counts);
        }
        return memberCounts;
    }
}
