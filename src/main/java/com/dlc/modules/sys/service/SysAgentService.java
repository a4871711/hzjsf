package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysAgentEntity;

import java.util.List;

public interface SysAgentService {
	/**
     * 查询代理商
     * 
     * @param id 代理商ID
     * @return 代理商
     */
    public SysAgentEntity selectAgentById(Long id);

    /**
     * 查询代理商列表
     * 
     * @param agent 代理商
     * @return 代理商集合
     */
    public List<SysAgentEntity> selectAgentList(SysAgentEntity agent);
    public int selectAgentCount(SysAgentEntity agent);

    /**
     * 新增代理商
     * 
     * @param agent 代理商
     * @return 结果
     */
    public int insertAgent(SysAgentEntity agent);

    /**
     * 修改代理商
     * 
     * @param agent 代理商
     * @return 结果
     */
    public int updateAgent(SysAgentEntity agent);

    /**
     * 批量删除代理商
     * 
     * @param ids 需要删除的代理商ID
     * @return 结果
     */
    public int deleteAgentByIds(Long[] ids);

    /**
     * 删除代理商信息
     * 
     * @param id 代理商ID
     * @return 结果
     */
    public int deleteAgentById(Long id);

}
