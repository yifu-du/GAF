/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.authority.service.impl;

import com.supermap.gaf.authority.commontype.*;
import com.supermap.gaf.authority.constant.CommonConstant;
import com.supermap.gaf.authority.constant.DbFieldNameConstant;
import com.supermap.gaf.authority.dao.AuthUserParttimeMapper;
import com.supermap.gaf.authority.enums.CodeBaseRoleEnum;
import com.supermap.gaf.authority.service.*;
import com.supermap.gaf.authority.vo.AuthUserParttimeSelectVo;
import com.supermap.gaf.authority.vo.AuthUserParttimeVo;
import com.supermap.gaf.exception.GafException;
import com.supermap.gaf.project.client.ProjCodeBaseUsersClient;
import com.supermap.gaf.shiro.SecurityUtilsExt;
import com.supermap.gaf.data.access.service.BatchSortAndCodeService;
import com.supermap.gaf.utils.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 用户挂职服务实现类
 * @author yangdong
 * @date:2021/3/25
 *
 */
@Service
public class AuthUserParttimeServiceImpl implements AuthUserParttimeService {
    private static final Logger logger = LogUtil.getLocLogger(AuthTenantServiceImpl.class);
    @Autowired
    private AuthUserParttimeMapper authUserParttimeMapper;
    @Autowired
    private AuthDepartmentService authDepartmentService;
    @Autowired
    private AuthPostService authPostService;
    @Autowired
    private AuthUserService authUserService;
    @Autowired
    private AuthRoleService authRoleService;
    @Autowired
    private AuthPostRoleService authPostRoleService;
    @Autowired
    private AuthUserRoleService authUserRoleService;
    @Autowired(required = false)
    private ProjCodeBaseUsersClient projCodeBaseUsersClient;
    @Autowired
    private BatchSortAndCodeService batchSortAndCodeService;

//    public AuthUserParttimeServiceImpl(AuthUserParttimeMapper authUserParttimeMapper, AuthDepartmentService authDepartmentService, AuthPostService authPostService, AuthUserService authUserService, AuthRoleService authRoleService, AuthPostRoleService authPostRoleService, AuthUserRoleService authUserRoleService, ProjCodeBaseUsersFeignService projCodeBaseUsersFeignService, BatchSortAndCodeService batchSortAndCodeService) {
//        this.authUserParttimeMapper = authUserParttimeMapper;
//        this.authDepartmentService = authDepartmentService;
//        this.authPostService = authPostService;
//        this.authUserService = authUserService;
//        this.authRoleService = authRoleService;
//        this.authPostRoleService = authPostRoleService;
//        this.authUserRoleService = authUserRoleService;
//        this.projCodeBaseUsersFeignService = projCodeBaseUsersFeignService;
//        this.batchSortAndCodeService = batchSortAndCodeService;
//    }

    @Override
    public AuthUserParttime getById(String userParttimeId) {
        if (userParttimeId == null) {
            throw new IllegalArgumentException("userParttimeId不能为空");
        }
        return authUserParttimeMapper.select(userParttimeId);
    }

    @Override
    public Map<String, Object> pageList(AuthUserParttimeSelectVo authUserParttimeSelectVo) {
        if (authUserParttimeSelectVo.getPageSize() == null || authUserParttimeSelectVo.getPageSize() == 0) {
            authUserParttimeSelectVo.setPageSize(50);
        }
        List<AuthUserParttime> pageList;
        if (authUserParttimeSelectVo.getOffset() == null || authUserParttimeSelectVo.getOffset() < CommonConstant.OFFSET_MAX_FOR_SQL_BETTER) {
            pageList = authUserParttimeMapper.pageList(authUserParttimeSelectVo);
        } else {
            pageList = authUserParttimeMapper.bigOffsetPageList(authUserParttimeSelectVo);
        }
        int totalCount = authUserParttimeMapper.pageListCount();
        Map<String, Object> result = new HashMap<>(2);
        result.put(DbFieldNameConstant.PAGE_LIST, pageList);
        result.put(DbFieldNameConstant.TOTAL_COUNT, totalCount);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> searchList(AuthUserParttimeSelectVo authUserParttimeSelectVo) {
        if (authUserParttimeSelectVo.getPageSize() == null || authUserParttimeSelectVo.getPageSize() == 0) {
            authUserParttimeSelectVo.setPageSize(50);
        }
        List<AuthUserParttime> pageList;
        pageList = authUserParttimeMapper.searchList(authUserParttimeSelectVo);
        List<AuthUserParttimeVo> pageListVo = new ArrayList<>();
        if (pageList.size() > 0) {
            Set<String> departmentIds = new HashSet<>(pageList.size());
            Set<String> postIds = new HashSet<>(pageList.size());
            pageList.forEach(authUserParttime -> {
                String postId = authUserParttime.getPostId();
                if (!StringUtils.isEmpty(postId)) {
                    postIds.add(postId);
                }
                String departmentId = authUserParttime.getDepartmentId();
                if (!StringUtils.isEmpty(departmentId)) {
                    departmentIds.add(departmentId);
                }
            });
            Map<String,AuthDepartment> idAndDepartmentMap = new HashMap<>(16);
            Map<String,AuthPost> idAndPostMap = new HashMap<>(16);
            if (!CollectionUtils.isEmpty(departmentIds)) {
                List<AuthDepartment> departments = authDepartmentService.getByIds(departmentIds);
                if (!CollectionUtils.isEmpty(departmentIds)) {
                    idAndDepartmentMap = departments.stream().collect(Collectors.toMap(AuthDepartment::getDepartmentId, authDepartment -> authDepartment));
                }
            }
            if (!CollectionUtils.isEmpty(postIds)) {
                List<AuthPost> posts = authPostService.getByIds(postIds);
                if (!CollectionUtils.isEmpty(departmentIds)) {
                    idAndPostMap = posts.stream().collect(Collectors.toMap(AuthPost::getPostId, authPost -> authPost));
                }
            }
            Map<String, AuthDepartment> finalIdAndDepartmentMap = idAndDepartmentMap;
            Map<String, AuthPost> finalIdAndPostMap = idAndPostMap;
            pageListVo = pageList.stream().map(authUserParttime -> {
                AuthUserParttimeVo authUserParttimeVo = new AuthUserParttimeVo();
                BeanUtils.copyProperties(authUserParttime, authUserParttimeVo);
                authUserParttimeVo.setDepartmentName(finalIdAndDepartmentMap.get(authUserParttime.getDepartmentId()).getDepartmentName());
                authUserParttimeVo.setPostName(finalIdAndPostMap.get(authUserParttime.getPostId()).getPostName());
                return authUserParttimeVo;
            }).collect(Collectors.toList());

        }
        Integer totalCount = authUserParttimeMapper.countOneField(authUserParttimeSelectVo.getSearchFieldName(), authUserParttimeSelectVo.getSearchFieldValue());
        Map<String, Object> result = new HashMap<>(2);
        result.put(DbFieldNameConstant.PAGE_LIST, pageListVo);
        result.put(DbFieldNameConstant.TOTAL_COUNT, totalCount);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthUserParttime insertAuthUserParttime(AuthUserParttime authUserParttime) {
        // todo 获取当前登录人员的用户名称
        authUserParttime.setUserParttimeId(UUID.randomUUID().toString());
        checkUniqueness(authUserParttime, false);
        // 判断是否有AppRole
        List<String> nameEns = CodeBaseRoleEnum.getAllNames();
        List<AuthRole> appRoles = authRoleService.listByNameEn(nameEns);
        Set<String> appRoleIds = appRoles.stream().map(AuthRole::getRoleId).collect(Collectors.toSet());
        String postId = authUserParttime.getPostId();
        List<AuthPostRole> postRoleList = authPostRoleService.getByPostId(postId, true);
        boolean hasAppRoleWithInsertPost = postRoleList.stream().anyMatch(authPostRole -> appRoleIds.contains(authPostRole.getRoleId()));
        if (hasAppRoleWithInsertPost) {
            AuthUser user = authUserService.getById(authUserParttime.getUserId());
            if (user == null) {
                throw new GafException("未找到该用户信息");
            }
            boolean hasAppRoleBeforeInsert = hasAppRole(user, appRoleIds, authUserParttimes -> {});
            if (!hasAppRoleBeforeInsert) {
                try{
                    projCodeBaseUsersClient.addDevUser(user.getRealName(), user.getName(), user.getEmail());
                } catch (Exception e) {
                    logger.info("新增代码库用户失败", e);
                }
            }
        }

        authUserParttime.setTenantId(Objects.requireNonNull(SecurityUtilsExt.getUser()).getTenantId());
        authUserParttimeMapper.insert(authUserParttime);
        batchSortAndCodeService.revisionSortSnForInsertOrDelete(AuthUserParttime.class, Collections.singletonList(authUserParttime.getUserId()));
        return authUserParttime;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<AuthUserParttime> authUserParttimes) {
        if (authUserParttimes != null && authUserParttimes.size() > 0) {
            Set<String> parentIds = new HashSet<>();
            authUserParttimes.forEach(authUserParttime -> {
                authUserParttime.setUserParttimeId(UUID.randomUUID().toString());
                parentIds.add(authUserParttime.getUserId());
            });
            authUserParttimeMapper.batchInsert(authUserParttimes);
            batchSortAndCodeService.revisionSortSnForInsertOrDelete(AuthUserParttime.class,parentIds);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAuthUserParttime(String userParttimeId) {
        AuthUserParttime parttime = this.getById(userParttimeId);
        String postId = parttime.getPostId();
        List<AuthPostRole> authPostRoles = authPostRoleService.getByPostId(postId, true);
        List<String> nameEns = CodeBaseRoleEnum.getAllNames();
        List<AuthRole> appRoles = authRoleService.listByNameEn(nameEns);
        Set<String> appRoleIds = appRoles.stream().map(AuthRole::getRoleId).collect(Collectors.toSet());
        boolean hasAppRoleWithDeleteParttime = authPostRoles.stream().anyMatch(authPostRole -> appRoleIds.contains(authPostRole.getRoleId()));
        authUserParttimeMapper.delete(userParttimeId);
        if (hasAppRoleWithDeleteParttime) {
            // 删除兼职
            AuthUser user = authUserService.getById(parttime.getUserId());
            if (user == null) {
                throw new GafException("未找到该用户信息");
            }
            boolean hasAppRoleAfterDelete = this.hasAppRole(user, appRoleIds, authUserParttimes -> {});
            if (!hasAppRoleAfterDelete) {
                try{
                    projCodeBaseUsersClient.blockDevUser(user.getName());
                } catch (Exception e) {
                    logger.info("删除代码库用户失败", e);
                }
            }
        }


    }

    private boolean hasAppRole(AuthUser user , Set<String> appRoleIds, Consumer<List<AuthUserParttime>> handleParttimes) {
        boolean hasAppRole = false;
        // todo: 获取用户所属租户  后序要改
        // 查询用户所在当前租户下是否有挂职信息
        // 查询岗位是否关联到App角色
        List<AuthUserParttime> userParttimes = this.getByUserId(user.getUserId());
        handleParttimes.accept(userParttimes);
        List<String> postIds = userParttimes.stream().map(AuthUserParttime::getPostId).collect(Collectors.toList());
        if (!StringUtils.isEmpty(user.getPostId())) {
            postIds.add(user.getPostId());
        }
        if (postIds.size() > 0) {
            List<AuthPostRole>  authPostRoleList = authPostRoleService.listByPostIds(postIds);
            hasAppRole = authPostRoleList.stream().anyMatch(authPostRole -> appRoleIds.contains(authPostRole.getRoleId()));
        }
        if (!hasAppRole) {
            List<AuthUserRole> authUserRoles = authUserRoleService.listByUser(user.getUserId());
            hasAppRole = authUserRoles.stream().anyMatch(authUserRole -> appRoleIds.contains(authUserRole.getRoleId()));
        }
        return hasAppRole;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthUserParttime updateAuthUserParttime(AuthUserParttime authUserParttime) {
        checkUniqueness(authUserParttime, true);
        AuthUserParttime oldParttime = this.getById(authUserParttime.getUserParttimeId());
        authUserParttimeMapper.update(authUserParttime);
        String parentId = authUserParttime.getUserId()!=null? authUserParttime.getUserId():oldParttime.getUserId();
        batchSortAndCodeService.revisionSortSnForUpdate(AuthUserParttime.class,parentId,oldParttime.getSortSn(),authUserParttime.getSortSn());

        String oldPostId = oldParttime.getPostId();
        String newPostId = authUserParttime.getPostId();
        if (!oldPostId .equals( newPostId )) {
            List<String> nameEns = CodeBaseRoleEnum.getAllNames();
            List<AuthRole> appRoles = authRoleService.listByNameEn(nameEns);
            Set<String> appRoleIds = appRoles.stream().map(AuthRole::getRoleId).collect(Collectors.toSet());
            AuthUser user = authUserService.getById(authUserParttime.getUserId());
            if (user == null) {
                throw new GafException("未找到该用户信息");
            }
            boolean hasAppRole = this.hasAppRole(user, appRoleIds, authUserParttimes -> authUserParttimes.removeIf(parttime -> parttime.getUserParttimeId() .equals( authUserParttime.getUserParttimeId() )));
            if (!hasAppRole) {
                // 查看新的岗位是否有app角色
                List<AuthPostRole>  authNewPostRoles = authPostRoleService.getByPostId(newPostId, true);
                boolean newPostHasAppRole = authNewPostRoles.stream().anyMatch(authPostRole -> appRoleIds.contains(authPostRole.getRoleId()));
                // 查看原来的岗位是否有app角色
                List<AuthPostRole>  authPostRoles = authPostRoleService.getByPostId(oldPostId, true);
                boolean oldPostHasAppRole = authPostRoles.stream().anyMatch(authPostRole -> appRoleIds.contains(authPostRole.getRoleId()));
                if (newPostHasAppRole && !oldPostHasAppRole) {
                    try{
                        projCodeBaseUsersClient.addDevUser(user.getRealName(),user.getName(),user.getEmail());
                    } catch (Exception e) {
                        logger.info("添加代码库用户失败", e);
                    }
                } else if (!newPostHasAppRole && oldPostHasAppRole) {
                    try{
                        projCodeBaseUsersClient.blockDevUser(user.getName());
                    } catch (Exception e) {
                        logger.info("删除代码库用户失败", e);
                    }
                }
            }
        }
        return authUserParttime;
    }

    @Override
    public List<AuthUserParttime> getByUserId(String userId) {
        return authUserParttimeMapper.selectByCombination(AuthUserParttime.builder().status(true).userId(userId).build());
    }

    @Override
    public void deleteByUserId(String userId) {
        authUserParttimeMapper.deleteByUserId(userId);
    }

    /**
     * 唯一性校验，每个用户与每个岗位的关系只能出现一次
     *
     * @param authUserParttime 用户挂职
     * @param isUpdate         是否为更新，更新时需要排除当前用户
     */
    private void checkUniqueness(AuthUserParttime authUserParttime, boolean isUpdate) {
        List<AuthUserParttime> authUserParttimes = authUserParttimeMapper.listUserParttime(authUserParttime.getPostId(), authUserParttime.getUserId());
        if (!CollectionUtils.isEmpty(authUserParttimes)) {
            boolean isUserParttime;
            if (isUpdate) {
                isUserParttime = authUserParttimes.stream().anyMatch(parttime -> !parttime.getUserParttimeId().equals(authUserParttime.getUserParttimeId()));
            } else {
                isUserParttime = authUserParttimes.size() > 0;
            }
            if (isUserParttime) {
                throw new GafException("该用户已在该岗位挂职");
            }
        }
    }

}
