package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.CardTypeMapper;
import com.dlc.modules.sys.dao.FitCardDao;
import com.dlc.modules.sys.entity.CardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


import com.dlc.modules.sys.entity.FitCardEntity;
import com.dlc.modules.sys.service.SysFitCardService;


@Service("fitCardService")
public class SysFitCardServiceImpl implements SysFitCardService {
    @Autowired
    private FitCardDao fitCardDao;
    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Override
    public FitCardEntity queryObject(Long fitCardId) {
        return fitCardDao.queryObject(fitCardId);
    }

    @Override
    public List<FitCardEntity> queryList(Map<String, Object> map) {
        return fitCardDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return fitCardDao.queryTotal();
    }

    @Override
    public void save(FitCardEntity fitCardEntity) {
        fitCardDao.save(fitCardEntity);
    }

    @Override
    public void update(FitCardEntity fitCardEntity) {
        fitCardDao.update(fitCardEntity);
    }
/*
    @Override
    public void updates(FitCardEntity fitCardEntity) {

    }*/

    @Override
    public void delete(Long fitcardId) {
        fitCardDao.delete(fitcardId);
    }

    @Override
    public void deleteBatch(Long[] fitCardIds) {
        fitCardDao.deleteBatch(fitCardIds);
    }

    @Override
    public Integer fitCardCount(long cardId) {
        return fitCardDao.fitCardCount(cardId);
    }

    @Override
    public List<CardType> selectCardTypeList() {
        return cardTypeMapper.selectAllCardType();
    }

    @Override
    public int queryIfExistCardType(String ctName) {
        return cardTypeMapper.queryIfExistCardType(ctName);
    }

    @Override
    public Integer getLastCardType() {
        return cardTypeMapper.selectLastCardType();
    }

    @Override
    public int saveCardType(CardType cardType) {
        return cardTypeMapper.insertSelective(cardType);
    }

    @Override
    public int updateCardType(CardType cardType) {
        return cardTypeMapper.updateByPrimaryKeySelective(cardType);
    }

    @Override
    public int queryIfExistCardTypeByCtId(Long ctId) {
        return cardTypeMapper.queryIfExistCardTypeByCtId(ctId);
    }

    @Override
    public int deleteCardType(Long ctId) {
        return cardTypeMapper.deleteByPrimaryKey(ctId);
    }

    @Override
    public int updateOnOffCard(Long fitCardId, Integer status) {
        return fitCardDao.updateOnOffCard(fitCardId, status);
    }


}
