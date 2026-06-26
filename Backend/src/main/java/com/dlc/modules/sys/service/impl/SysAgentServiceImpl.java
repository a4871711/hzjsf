package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysAgentDao;
import com.dlc.modules.sys.entity.SysAgentEntity;
import com.dlc.modules.sys.service.SysAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAgentServiceImpl implements SysAgentService {

	@Autowired
    private SysAgentDao agentMapper;

    /**
     * 查询代理商
     * 
     * @param id 代理商ID
     * @return 代理商
     */
    @Override
    public SysAgentEntity selectAgentById(Long id)
    {
        return agentMapper.selectAgentById(id);
    }

    /**
     * 查询代理商列表
     * 
     * @param agent 代理商
     * @return 代理商
     */
    @Override
    public List<SysAgentEntity> selectAgentList(SysAgentEntity agent)
    {
        return agentMapper.selectAgentList(agent);
    }
    
    @Override
    public int selectAgentCount(SysAgentEntity agent)
    {
        return agentMapper.selectAgentCount(agent);
    }

    /**
     * 新增代理商
     * 
     * @param agent 代理商
     * @return 结果
     */
    @Override
    public int insertAgent(SysAgentEntity agent)
    {
        return agentMapper.insertAgent(agent);
    }

    /**
     * 修改代理商
     * 
     * @param agent 代理商
     * @return 结果
     */
    @Override
    public int updateAgent(SysAgentEntity agent)
    {
        return agentMapper.updateAgent(agent);
    }

    /**
     * 批量删除代理商
     * 
     * @param ids 需要删除的代理商ID
     * @return 结果
     */
    @Override
    public int deleteAgentByIds(Long[] ids)
    {
        return agentMapper.deleteAgentByIds(ids);
    }

    /**
     * 删除代理商信息
     * 
     * @param id 代理商ID
     * @return 结果
     */
    @Override
    public int deleteAgentById(Long id)
    {
        return agentMapper.deleteAgentById(id);
    }
}
